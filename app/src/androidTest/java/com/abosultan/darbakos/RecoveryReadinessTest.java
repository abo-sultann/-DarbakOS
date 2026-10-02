package com.abosultan.darbakos;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.abosultan.darbakos.core.RecoveryReadiness;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class RecoveryReadinessTest {
    @Test public void readinessRequiresBackupHashAndVerifiedPath() {
        assertEquals(RecoveryReadiness.State.LOCKED_NO_BACKUP,RecoveryReadiness.evaluate("","",false).state);
        assertEquals(RecoveryReadiness.State.LOCKED_NO_HASH,RecoveryReadiness.evaluate("golden-1","bad",false).state);
        String hash="aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        assertEquals(RecoveryReadiness.State.LOCKED_PATH_UNVERIFIED,RecoveryReadiness.evaluate("golden-1",hash,false).state);
        assertEquals(RecoveryReadiness.State.ELIGIBLE,RecoveryReadiness.evaluate("golden-1",hash,true).state);
    }
}
