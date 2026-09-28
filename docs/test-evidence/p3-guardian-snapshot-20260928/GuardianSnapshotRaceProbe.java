import com.abosultan.darbakos.core.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class GuardianSnapshotRaceProbe {
    public static void main(String[] args) throws Exception {
        GuardianRegistry registry = GuardianRegistry.get();
        registry.resetForColdBoot();
        for (GuardianRegistry.Component c : GuardianRegistry.Component.values()) {
            registry.update(c, GuardianState.Health.HEALTHY);
        }
        AtomicBoolean running = new AtomicBoolean(true);
        CountDownLatch ready = new CountDownLatch(1);
        Thread writer = new Thread(() -> {
            while (running.get()) {
                registry.update(GuardianRegistry.Component.CORE, GuardianState.Health.FAILED);
                ready.countDown();
                registry.update(GuardianRegistry.Component.CORE, GuardianState.Health.HEALTHY);
            }
        }, "snapshot-test-writer");
        writer.start();
        String mismatch = null;
        try {
            if (!ready.await(5, TimeUnit.SECONDS)) throw new AssertionError("Writer did not start");
            for (int capture = 0; capture < 20000; capture++) {
                GuardianSnapshot snapshot = GuardianSnapshot.capture(registry);
                if (snapshot.overall != snapshot.core.health) {
                    mismatch = "capture=" + capture + " core=" + snapshot.core.health
                            + " coreRevision=" + snapshot.core.revision + " overall=" + snapshot.overall;
                    break;
                }
            }
        } finally {
            running.set(false);
            writer.join(5000);
            if (writer.isAlive()) throw new AssertionError("Test writer did not stop");
            registry.resetForColdBoot();
        }
        if (mismatch != null) throw new AssertionError("Inconsistent point-in-time snapshot: " + mismatch);
        System.out.println("PASS: 20000 concurrent captures; test writer joined; registry reset");
    }
}
