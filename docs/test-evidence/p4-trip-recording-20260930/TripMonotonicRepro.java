import com.abosultan.darbakos.core.PositionFix;
import com.abosultan.darbakos.core.TripRecorder;

/** Focused reproduction of the task's monotonic-order contract across segments. */
public final class TripMonotonicRepro {
    public static void main(String[] args) {
        int failures = 0;
        for (boolean restart : new boolean[] {false, true}) {
            for (long next : new long[] {9_999L, 10_000L}) {
                TripRecorder recorder = new TripRecorder();
                recorder.start();
                if (!recorder.accept(fix(10_000L), 10_000L)) throw new AssertionError("setup");
                if (restart) { recorder.finish(); recorder.start(); }
                else { recorder.pause(); recorder.resume(); }
                boolean rejected = !recorder.accept(fix(next), 10_000L);
                System.out.println((rejected ? "PASS" : "FAIL") + ": "
                        + (restart ? "finish/start" : "pause/resume")
                        + " rejects " + next + " after accepted 10000");
                if (!rejected) failures++;
            }
        }
        if (failures != 0) throw new AssertionError(failures + " monotonic-order violations");
    }

    private static PositionFix fix(long time) {
        return PositionFix.create(24.7, 46.7, 5f, 10f, time);
    }
}
