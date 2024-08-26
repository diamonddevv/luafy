package dev.diamond.luafy.script.registry.callback;

public interface CallbackEventSubscription {
    String getScriptId();
    String getFunctionName();
    boolean usesOwnThread();

    static CallbackEventSubscription of(String scriptId, String function, boolean usesOwnThread) {
        return new CallbackEventSubscription() {
            @Override
            public String getScriptId() {
                return scriptId;
            }

            @Override
            public String getFunctionName() {
                return function;
            }

            @Override
            public boolean usesOwnThread() {
                return usesOwnThread;
            }

        };
    }
}
