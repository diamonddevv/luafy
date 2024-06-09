package dev.diamond.luafy.script.registry.callback;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.abstraction.NamedParam;
import dev.diamond.luafy.util.DescriptionProvider;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class ScriptCallbackEvent implements DescriptionProvider {
    private final Identifier id;
    private final String description;
    private final NamedParam[] contextParams;


    public ScriptCallbackEvent(Identifier id, String description, ContextParamsBuilder contextParams) {
        this.id = id;
        this.description = description;
        this.contextParams = contextParams.build();
    }

    public ScriptCallbackEvent(Identifier id, String description, NamedParam... contextParams) {
        this.id = id;
        this.description = description;
        this.contextParams = contextParams;
    }

    public ScriptCallbackEvent(Identifier id, String description) {
        this.id = id;
        this.description = description;
        this.contextParams = null;
    }

    public ScriptCallbackEvent(Identifier id) {
        this.id = id;
        this.description = null;
        this.contextParams = null;
    }



    public void register() {
        Registry.register(Luafy.Registries.CALLBACK_REGISTRY, this.id, this);
    }

    @Override
    public String toString() {
        return id.toString();
    }

    public static ScriptCallbackEvent fromStringId(String id) {
        ScriptCallbackEvent event = Luafy.Registries.CALLBACK_REGISTRY.getOrEmpty(new Identifier(id)).orElse(null);

        if (event == null) Luafy.LOGGER.error("Couldn't locate callback event '{}'. Available options are: {}", id, getAll().stream().map(ScriptCallbackEvent::toString).toList());

        return event;
    }

    public static Collection<ScriptCallbackEvent> getAll() {
        return Luafy.Registries.CALLBACK_REGISTRY.stream().toList();
    }

    @Override
    public String getDescription() {
        return description;
    }

    public NamedParam[] getContextParams() {
        return contextParams;
    }


    public static class ContextParamsBuilder {
        private final Collection<NamedParam> list;

        private ContextParamsBuilder(ArrayList<NamedParam> list) {
            this.list = list;
        }

        public static ContextParamsBuilder paramsBuilder() {
            return new ContextParamsBuilder(new ArrayList<>());
        }

        public ContextParamsBuilder add(Consumer<Collection<NamedParam>> consumer) {
            ArrayList<NamedParam> n = new ArrayList<>();
            consumer.accept(n);
            list.addAll(n);
            return this;
        }
        public ContextParamsBuilder add(NamedParam n) {
            list.add(n);
            return this;
        }
        public ContextParamsBuilder add(String s, Class<?> clazz) {
            list.add(new NamedParam(s, clazz));
            return this;
        }


        public NamedParam[] build() {
            return list.toArray(new NamedParam[0]);
        }
    }
}
