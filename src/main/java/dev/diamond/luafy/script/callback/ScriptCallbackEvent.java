package dev.diamond.luafy.script.callback;

import dev.diamond.luafy.Luafy;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class ScriptCallbackEvent {
    private final Identifier id;
    public ScriptCallbackEvent(Identifier id) {
        this.id = id;
    }
    public void register() {
        Registry.register(Luafy.CALLBACK_REGISTRY, this.id, this);
    }

    @Override
    public String toString() {
        return id.toString();
    }

    public static ScriptCallbackEvent fromStringId(String id) {
        ScriptCallbackEvent event = Luafy.CALLBACK_REGISTRY.getOrEmpty(new Identifier(id)).orElse(null);

        if (event == null) Luafy.LOGGER.error("Couldn't locate callback event '{}'. Available options are: {}", id, getAll().stream().map(ScriptCallbackEvent::toString).toList());

        return event;
    }

    public static Collection<ScriptCallbackEvent> getAll() {
        return Luafy.CALLBACK_REGISTRY.stream().toList();
    }
}
