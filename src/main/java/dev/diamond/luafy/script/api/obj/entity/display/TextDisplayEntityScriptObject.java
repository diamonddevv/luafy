package dev.diamond.luafy.script.api.obj.entity.display;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.api.obj.util.BufferedImageScriptObject;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class TextDisplayEntityScriptObject extends DisplayEntityScriptObject {
    private static final String PIXEL = "▇";
    private static final int TRANSPARENCY_CUTOFF = 0xF0;

    public final DisplayEntity.TextDisplayEntity textDisp;

    public TextDisplayEntityScriptObject(DisplayEntity.TextDisplayEntity entity) {
        super(entity);
        this.textDisp = entity;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {
        super.addFunctions(set);

        set.put("set_text_string", args -> {
            textDisp.setText(Text.of(args[0].asString()));
            return null;
        });


        set.put("set_text_component", args -> {
            String s = args[0].asString();
            textDisp.setText(Text.Serialization.fromJson(s));
            return null;
        });

        set.put("set_image", args -> {
            BufferedImage img = args[0].asScriptObjectAssertive(BufferedImageScriptObject.class).get();
            MutableText text = Text.empty();

            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    Color color = new Color(img.getRGB(x, y), true);

                    Text element = Text.literal(color.getAlpha() <= TRANSPARENCY_CUTOFF ? " " : PIXEL).withColor(color.getRGB());
                    text.append(element);
                }
                text.append(Text.literal("\n"));
            }

            // scaling

            try {

                NbtCompound nbt = StringNbtReader.parse("{transformation:{scale:[1f,0.5555555555555556f,1f],translation:[0f,0f,0f],left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f]},line_width:2147483647,background:0}");
                NbtCompound exists = new NbtCompound();

                textDisp.writeNbt(exists);

                for (String key : nbt.getKeys()) {
                    exists.put(key, nbt.get(key));
                }

                textDisp.readNbt(exists);
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
            textDisp.setText(text);

            return null;
        });
    }
}
