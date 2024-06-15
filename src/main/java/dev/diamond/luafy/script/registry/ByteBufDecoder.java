package dev.diamond.luafy.script.registry;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.abstraction.BaseValueAdapter;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.api.obj.util.BufferedImageScriptObject;
import net.minecraft.registry.Registry;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ByteBufDecoder {
    private final Decoder function;

    public ByteBufDecoder(Decoder function) {
        this.function = function;
    }

    public AbstractBaseValue<?, ?> decode(byte[] buf, BaseValueAdapter adapter) {
        return adapter.adapt(function.decode(buf));
    }

    @FunctionalInterface
    public interface Decoder {
        Object decode(byte[] buf);
    }


    public static class Decoders {

        public static final ByteBufDecoder TEXT_UTF8 = new ByteBufDecoder(buf -> new String(buf, StandardCharsets.UTF_8));
        public static final ByteBufDecoder TEXT_UTF16 = new ByteBufDecoder(buf -> new String(buf, StandardCharsets.UTF_16));
        public static final ByteBufDecoder JSON = new ByteBufDecoder(buf -> new Gson().fromJson(new JsonReader(new InputStreamReader(new ByteArrayInputStream(buf))), JsonObject.class));
        public static final ByteBufDecoder PNG = new ByteBufDecoder(buf -> {
            try {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(buf));
                return new BufferedImageScriptObject(image);
            } catch (Exception e) {
                Luafy.LOGGER.warn("Could not read image from bytes: " + e);
            }
            return null;
        });

        public static void registerAll() {
            Registry.register(Luafy.Registries.BYTEBUF_DECODERS, Luafy.id("txt_utf8"), TEXT_UTF8);
            Registry.register(Luafy.Registries.BYTEBUF_DECODERS, Luafy.id("txt_utf16"), TEXT_UTF16);
            Registry.register(Luafy.Registries.BYTEBUF_DECODERS, Luafy.id("json"), JSON);
            Registry.register(Luafy.Registries.BYTEBUF_DECODERS, Luafy.id("png"), PNG);
        }
    }


}
