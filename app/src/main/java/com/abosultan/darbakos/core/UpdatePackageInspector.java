package com.abosultan.darbakos.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public final class UpdatePackageInspector {
    public enum State { COMPATIBLE, INCOMPATIBLE, UNREADABLE }
    public static final class Result {
        public final State state; public final long size; public final String sha256;
        public final String packageName; public final String versionName; public final int versionCode;
        Result(State s,long z,String h,String p,String v,int c){state=s;size=z;sha256=h;packageName=p;versionName=v;versionCode=c;}
    }
    private UpdatePackageInspector() {}

    public static Result inspect(Context context, File file) {
        if (file == null || !file.isFile() || !file.canRead())
            return new Result(State.UNREADABLE,0L,"","","",0);
        String hash = sha256(file);
        if (hash.length() == 0) return new Result(State.UNREADABLE,file.length(),"","","",0);
        PackageInfo info = context.getPackageManager().getPackageArchiveInfo(file.getAbsolutePath(), 0);
        if (info == null || info.packageName == null)
            return new Result(State.UNREADABLE,file.length(),hash,"","",0);
        String current = context.getPackageName();
        State state = current.equals(info.packageName) ? State.COMPATIBLE : State.INCOMPATIBLE;
        return new Result(state,file.length(),hash,info.packageName,
                info.versionName == null ? "" : info.versionName,info.versionCode);
    }

    private static String sha256(File file) {
        try (FileInputStream in = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192]; int read;
            while ((read=in.read(buffer))!=-1) digest.update(buffer,0,read);
            StringBuilder out=new StringBuilder();
            for(byte b:digest.digest()) out.append(String.format(java.util.Locale.US,"%02x",b & 0xff));
            return out.toString();
        } catch (Exception ignored) { return ""; }
    }
}
