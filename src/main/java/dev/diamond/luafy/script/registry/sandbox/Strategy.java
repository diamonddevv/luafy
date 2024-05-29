package dev.diamond.luafy.script.registry.sandbox;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

public class Strategy {
    @SerializedName("blacklist")
    public boolean blacklist = false;

    @SerializedName("apis")
    public List<String> apis;


    public static Strategy of(boolean blacklist, String... apis) {
        Strategy s = new Strategy();

        s.blacklist = blacklist;
        s.apis = Arrays.stream(apis).toList();

        return s;
    }
}
