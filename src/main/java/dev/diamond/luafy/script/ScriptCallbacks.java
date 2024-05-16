package dev.diamond.luafy.script;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ScriptCallbacks
{
    public static class CallbackScriptBean {
        @SerializedName("advancements")
        public List<IdentifierCallbackBean> advancementCallbacks;

        @SerializedName("load")
        public ScriptsCallbackBean loadCallbacks;

        @SerializedName("tick")
        public ScriptsCallbackBean tickCallbacks;

        //@SerializedName("entity_context")
        //public List<WithEntityContextCallbackBean> entityCtx;
    }

    public static class WithEntityContextCallbackBean {
        @SerializedName("entity_predicate")
        public String predicate_path;

        @SerializedName("on_death")
        public List<String> onDeath;

        @SerializedName("on_takes_damage")
        public List<String> onDamage;

        @SerializedName("on_deals_damage")
        public List<String> onDealsDamage;

        @SerializedName("on_kills_other")
        public List<String> onKillsOther;

        @SerializedName("tick")
        public List<String> tick;
    }
    public static class ScriptsCallbackBean {
        @SerializedName("scripts")
        public List<String> scriptIds;
    }
    public static class IdentifierCallbackBean {
        @SerializedName("id")
        public String id;

        @SerializedName("scripts")
        public List<String> scriptIds;
    }
}
