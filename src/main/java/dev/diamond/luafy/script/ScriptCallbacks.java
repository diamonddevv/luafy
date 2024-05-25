package dev.diamond.luafy.script;

import com.google.gson.annotations.SerializedName;
import net.minecraft.advancement.AdvancementEntry;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ScriptCallbacks
{
    public static class CallbackScriptBean {

        @SerializedName("event")
        public List<CallbackEventBean> eventCallbacks;

    }

    public static class CallbackEventBean {
        @SerializedName("id")
        public String id;

        @SerializedName("scripts")
        public List<String> scriptIds;

        @SerializedName("threaded")
        public boolean threaded = false;
    }


}
