package dev.diamond.luafy.script.registry.callback;

public interface CallbackEventSubscription {
    String getScriptId();
    boolean usesOwnThread();

    static CallbackEventSubscription of(String scriptId, boolean usesOwnThread) {
        return new CallbackEventSubscription() {
            @Override
            public String getScriptId() {
                return scriptId;
            }

            @Override
            public boolean usesOwnThread() {
                return usesOwnThread;
            }
        };
    }
}
