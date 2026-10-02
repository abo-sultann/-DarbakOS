package com.abosultan.darbakos.core;

public final class RecoveryReadiness {
    public enum State { LOCKED_NO_BACKUP, LOCKED_NO_HASH, LOCKED_PATH_UNVERIFIED, ELIGIBLE }
    public final State state;
    public final String backupId;
    public final String sha256;

    private RecoveryReadiness(State state, String backupId, String sha256) {
        this.state=state; this.backupId=backupId; this.sha256=sha256;
    }

    public static RecoveryReadiness evaluate(String backupId, String sha256, boolean recoveryPathVerified) {
        String id=backupId == null ? "" : backupId.trim();
        String hash=sha256 == null ? "" : sha256.trim().toLowerCase(java.util.Locale.US);
        if (id.length()==0) return new RecoveryReadiness(State.LOCKED_NO_BACKUP,id,hash);
        if (!hash.matches("[0-9a-f]{64}")) return new RecoveryReadiness(State.LOCKED_NO_HASH,id,hash);
        if (!recoveryPathVerified) return new RecoveryReadiness(State.LOCKED_PATH_UNVERIFIED,id,hash);
        return new RecoveryReadiness(State.ELIGIBLE,id,hash);
    }
}
