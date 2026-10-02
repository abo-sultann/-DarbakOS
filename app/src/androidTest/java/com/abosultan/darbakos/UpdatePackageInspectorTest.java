package com.abosultan.darbakos;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.abosultan.darbakos.core.UpdatePackageInspector;
import java.io.File;
import java.io.FileOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class UpdatePackageInspectorTest {
    @Test public void missingAndInvalidCandidatesStayUnreadable() throws Exception {
        android.content.Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        File missing=new File(context.getCacheDir(),"missing-update.apk");
        assertEquals(UpdatePackageInspector.State.UNREADABLE,UpdatePackageInspector.inspect(context,missing).state);
        File invalid=new File(context.getCacheDir(),"invalid-update.apk");
        try(FileOutputStream out=new FileOutputStream(invalid)){out.write(new byte[]{1,2,3,4});}
        UpdatePackageInspector.Result r=UpdatePackageInspector.inspect(context,invalid);
        assertEquals(UpdatePackageInspector.State.UNREADABLE,r.state);
        assertEquals(4L,r.size);
        assertEquals(64,r.sha256.length());
        invalid.delete();
    }
}
