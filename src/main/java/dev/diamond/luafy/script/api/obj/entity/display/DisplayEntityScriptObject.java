package dev.diamond.luafy.script.api.obj.entity.display;

import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.api.obj.entity.EntityScriptObject;
import dev.diamond.luafy.script.api.obj.math.Matrix4fScriptObject;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.util.math.AffineTransformation;

import java.util.HashMap;

public class DisplayEntityScriptObject extends EntityScriptObject {
    private final DisplayEntity display;

    public DisplayEntityScriptObject(DisplayEntity entity) {
        super(entity);
        this.display = entity;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {

        set.put("as_item", args -> new ItemDisplayEntityScriptObject((DisplayEntity.ItemDisplayEntity) display));
        set.put("as_text", args -> new TextDisplayEntityScriptObject((DisplayEntity.TextDisplayEntity) display));


        set.put("set_transformation_matrix", args -> {
            AffineTransformation affine = new AffineTransformation(args[0].asScriptObjectAssertive(Matrix4fScriptObject.class).get());
            display.setTransformation(affine);
            return null;
        });
        set.put("get_transformation_matrix", args -> new Matrix4fScriptObject(DisplayEntity.getTransformation(display.getDataTracker()).getMatrix()));
    }
}
