package com.abosultan.darbakos;

import com.abosultan.darbakos.core.CoreStateStore;
import com.abosultan.darbakos.core.DarbakState;
import com.abosultan.darbakos.core.DarbakState.Availability;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** Synthetic states stay inside this test process and are never bound to UI. */
@RunWith(AndroidJUnit4.class)
public final class CorePublishTest {
    private final CoreStateStore store = CoreStateStore.get();
    private final List<CoreStateStore.Listener> registered = new ArrayList<>();

    @Before public void reset() { store.resetForColdBoot(); }
    @After public void cleanup() {
        for (CoreStateStore.Listener listener : registered) store.removeListener(listener);
        store.resetForColdBoot();
    }
    private void listen(CoreStateStore.Listener listener) {
        registered.add(listener);
        store.addListener(listener);
    }
    private DarbakState changed() {
        return store.snapshot().withAvailability(Availability.AVAILABLE,
                Availability.UNKNOWN, Availability.UNAVAILABLE, true);
    }

    @Test public void revisionsAdvanceWithoutMutatingPriorStates() {
        DarbakState initial = DarbakState.coldBoot();
        DarbakState first = initial.withAvailability(Availability.AVAILABLE,
                Availability.UNKNOWN, Availability.UNAVAILABLE, true);
        DarbakState second = first.withAvailability(Availability.UNKNOWN,
                Availability.AVAILABLE, Availability.AVAILABLE, false);
        assertEquals(0L, initial.revision);
        assertEquals(1L, first.revision);
        assertEquals(2L, second.revision);
        assertNotSame(initial, first);
        assertNotSame(first, second);
        assertEquals(Availability.UNAVAILABLE, initial.speed);
        assertEquals(Availability.UNAVAILABLE, initial.navigation);
        assertEquals(Availability.UNAVAILABLE, initial.vehicle);
        assertFalse(initial.mediaPlaying);
        assertEquals(Availability.AVAILABLE, first.speed);
        assertEquals(Availability.UNKNOWN, first.navigation);
        assertEquals(Availability.UNAVAILABLE, first.vehicle);
        assertTrue(first.mediaPlaying);
        assertEquals(Availability.UNKNOWN, second.speed);
        assertEquals(Availability.AVAILABLE, second.navigation);
        assertEquals(Availability.AVAILABLE, second.vehicle);
        assertFalse(second.mediaPlaying);
    }

    @Test public void publishUpdatesSnapshotAndCallsListenersInlineWithExactState() {
        DarbakState next = changed();
        Thread publisher = Thread.currentThread();
        int[] callbacks = {0};
        boolean[] insidePublish = {false};
        CoreStateStore.Listener first = state -> {
            assertTrue("Callback must complete inside publish", insidePublish[0]);
            assertSame(publisher, Thread.currentThread());
            assertSame(next, state);
            assertSame(next, store.snapshot());
            callbacks[0]++;
        };
        listen(first);
        listen(state -> {
            assertTrue(insidePublish[0]);
            assertSame(publisher, Thread.currentThread());
            assertSame(next, state);
            callbacks[0]++;
        });
        assertEquals(0, callbacks[0]);
        insidePublish[0] = true;
        store.publish(next);
        insidePublish[0] = false;
        assertSame(next, store.snapshot());
        assertEquals(1L, store.snapshot().revision);
        assertEquals(2, callbacks[0]);
    }

    @Test public void duplicateRegistrationDoesNotMultiplyCallbacks() {
        int[] calls = {0};
        CoreStateStore.Listener listener = state -> calls[0]++;
        listen(listener);
        store.addListener(listener);
        store.publish(changed());
        assertEquals(1, calls[0]);
        store.publish(changed());
        assertEquals(2, calls[0]);
        assertEquals(2L, store.snapshot().revision);
    }

    @Test public void removalStopsOnlyTheRemovedListener() {
        int[] removed = {0};
        int[] remaining = {0};
        CoreStateStore.Listener listener = state -> removed[0]++;
        listen(listener);
        listen(state -> remaining[0]++);
        store.publish(changed());
        store.removeListener(listener);
        DarbakState next = changed();
        store.publish(next);
        assertEquals(1, removed[0]);
        assertEquals(2, remaining[0]);
        assertSame(next, store.snapshot());
    }

    @Test public void nullPublishPreservesSnapshotRevisionAndSilence() {
        store.publish(changed());
        DarbakState before = store.snapshot();
        int[] calls = {0};
        listen(state -> calls[0]++);
        store.publish(null);
        assertSame(before, store.snapshot());
        assertEquals(1L, store.snapshot().revision);
        assertEquals(0, calls[0]);
    }

    @Test public void coldResetClearsPublishedAvailabilityPlaybackAndRevision() {
        store.publish(changed());
        store.publish(changed());
        DarbakState prior = store.snapshot();
        assertEquals(2L, prior.revision);
        assertTrue(prior.mediaPlaying);
        store.resetForColdBoot();
        DarbakState cold = store.snapshot();
        assertNotSame(prior, cold);
        assertEquals(0L, cold.revision);
        assertEquals(Availability.UNAVAILABLE, cold.speed);
        assertEquals(Availability.UNAVAILABLE, cold.navigation);
        assertEquals(Availability.UNAVAILABLE, cold.vehicle);
        assertFalse(cold.mediaPlaying);
        assertEquals(2L, prior.revision);
        assertTrue(prior.mediaPlaying);
    }

    private static void assertColdReset(DarbakState state) {
        assertEquals(0L, state.revision);
        assertEquals(Availability.UNAVAILABLE, state.speed);
        assertEquals(Availability.UNAVAILABLE, state.navigation);
        assertEquals(Availability.UNAVAILABLE, state.vehicle);
        assertFalse(state.mediaPlaying);
    }

    @Test public void resetPublishesOnceToEachUniqueListenerInline() {
        store.publish(changed());
        DarbakState previous = store.snapshot();
        Thread caller = Thread.currentThread();
        boolean[] insideReset = {false};
        int[] calls = {0, 0};
        DarbakState[] delivered = new DarbakState[2];
        CoreStateStore.Listener first = state -> {
            assertTrue(insideReset[0]);
            assertSame(caller, Thread.currentThread());
            assertSame(store.snapshot(), state);
            assertColdReset(state);
            delivered[0] = state;
            calls[0]++;
        };
        listen(first);
        store.addListener(first);
        listen(state -> {
            assertTrue(insideReset[0]);
            assertSame(caller, Thread.currentThread());
            assertSame(store.snapshot(), state);
            assertColdReset(state);
            delivered[1] = state;
            calls[1]++;
        });
        assertArrayEquals(new int[] {0, 0}, calls);
        insideReset[0] = true;
        store.resetForColdBoot();
        insideReset[0] = false;
        assertArrayEquals(new int[] {1, 1}, calls);
        assertSame(delivered[0], delivered[1]);
        assertSame(delivered[0], store.snapshot());
        assertNotSame(previous, delivered[0]);
        assertEquals(1L, previous.revision);
        assertTrue(previous.mediaPlaying);
    }

    @Test public void removedListenerReceivesNoColdReset() {
        int[] removedCalls = {0};
        int[] remainingCalls = {0};
        CoreStateStore.Listener removed = state -> removedCalls[0]++;
        listen(removed);
        listen(state -> remainingCalls[0]++);
        store.publish(changed());
        assertEquals(1, removedCalls[0]);
        assertEquals(1, remainingCalls[0]);
        store.removeListener(removed);
        store.resetForColdBoot();
        assertEquals(1, removedCalls[0]);
        assertEquals(2, remainingCalls[0]);
        assertColdReset(store.snapshot());
    }

    @Test public void repeatedResetsDeterministicallyPublishFreshColdSnapshots() {
        store.publish(changed());
        List<DarbakState> received = new ArrayList<>();
        listen(received::add);
        DarbakState previous = store.snapshot();
        for (int i = 1; i <= 3; i++) {
            store.resetForColdBoot();
            assertEquals(i, received.size());
            DarbakState current = received.get(i - 1);
            assertSame(current, store.snapshot());
            assertNotSame(previous, current);
            assertColdReset(current);
            previous = current;
        }
        for (DarbakState retained : received) assertColdReset(retained);
    }

}
