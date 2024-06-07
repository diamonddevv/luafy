package dev.diamond.luafy.script.api.obj.util;

import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class BufferedImageScriptObject implements IScriptObject {
    private final BufferedImage image;

    public BufferedImageScriptObject(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {
        set.put("get_pixel", args -> {
           int x = args[0].asInt();
           int y = args[1].asInt();

           return image.getRGB(x, y);
        });
    }

    public BufferedImage get() {
        return image;
    }
}
