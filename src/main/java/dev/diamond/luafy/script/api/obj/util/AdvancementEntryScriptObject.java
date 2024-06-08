package dev.diamond.luafy.script.api.obj.util;

import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class AdvancementEntryScriptObject implements IScriptObject<AdvancementEntry> {

    private final AdvancementEntry entry;

    public AdvancementEntryScriptObject(AdvancementEntry entry) {
        this.entry = entry;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {
        set.put("get_id", args -> entry.id().toString());

        set.put("is_root", args -> entry.value().isRoot());

        set.put("has_display", args -> entry.value().display().isPresent());
        set.put("get_display_frame", args -> entry.value().display().map(c -> c.getFrame().asString()).orElse(null));
        set.put("get_display_title", args -> entry.value().display().map(c -> c.getTitle().getString()).orElse(null));
        set.put("get_display_desc", args -> entry.value().display().map(c -> c.getDescription().getString()).orElse(null));
        set.put("get_display_announces", args -> entry.value().display().map(AdvancementDisplay::shouldAnnounceToChat).orElse(null));

        set.put("get_reward_exp", args -> entry.value().rewards().experience());
        set.put("get_reward_recipes", args -> entry.value().rewards().recipes().stream().map(Identifier::toString).toList());
        set.put("get_reward_function", args -> entry.value().rewards().function().map(container -> container.getId().toString()).orElse(null));
    }

    @Override
    public AdvancementEntry get() {
        return entry;
    }
}
