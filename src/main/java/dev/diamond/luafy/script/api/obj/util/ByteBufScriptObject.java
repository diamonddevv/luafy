package dev.diamond.luafy.script.api.obj.util;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ByteBufScriptObject implements IScriptObject<Byte[]> {

    private final byte[] buf;

    public ByteBufScriptObject(byte[] buf) {
        this.buf = buf;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {

        set.put("length", args -> buf.length);
        set.put("get_byte", args -> buf[args[0].asInt()]);
        set.put("set_byte", args -> {
            buf[args[0].asInt()] = args[1].asByte();
            return null;
        });

        set.put("decode", args -> {
            Identifier id = Identifier.of(args[0].asString());
            var opt = Luafy.Registries.BYTEBUF_DECODERS.getOrEmpty(id);
            return opt.<Object>map(byteBufDecoder -> byteBufDecoder.decode(buf, args[0]::adapt)).orElse(null);
        });

        set.put("to_int_list", args -> {
            Collection<Integer> ints = new ArrayList<>();

            for (byte b : buf) {
                ints.add((int) b);
            }

            return ints;
        });
    }

    @Override
    public Byte[] get() {
        Byte[] bytes = new Byte[buf.length];
        for (int i = 0; i < buf.length; i++) {
            bytes[i] = buf[i];
        }
        return bytes;
    }
}
