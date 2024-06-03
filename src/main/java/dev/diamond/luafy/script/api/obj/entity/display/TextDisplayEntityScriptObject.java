package dev.diamond.luafy.script.api.obj.entity.display;

import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.api.obj.util.BufferedImageScriptObject;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class TextDisplayEntityScriptObject extends DisplayEntityScriptObject {
    private static final String SQUARE = "■";

    private final DisplayEntity.TextDisplayEntity textDisp;

    public TextDisplayEntityScriptObject(DisplayEntity.TextDisplayEntity entity) {
        super(entity);
        this.textDisp = entity;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {
        super.addFunctions(set);
        set.put("set_image", args -> {
            BufferedImage img = args[0].asScriptObjectAssertive(BufferedImageScriptObject.class).get();
            MutableText text = Text.empty();

            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    text.append(Text.literal(SQUARE).withColor(img.getRGB(x, y) - 0xFF000000));
                }
                text.append(Text.literal("\n"));
            }

            textDisp.setText(text);

            return null;
        });
    }
}
