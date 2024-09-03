package dev.diamond.luafy.script.api;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.config.LuafyConfig;
import dev.diamond.luafy.mixin.ParsedArgumentAccessor;
import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.NamedParam;
import dev.diamond.luafy.script.abstraction.api.AbstractTypedScriptApi;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.api.obj.argument.CommandArgument;
import dev.diamond.luafy.util.HexId;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.atomic.AtomicInteger;

public class CommandApi extends AbstractTypedScriptApi {

    public CommandApi(AbstractScript<?> script) {
        super(script, "command");
    }

    public static final NamedParam COMMAND_HEXID = new NamedParam("command_hexid", HexId.class);

    @Override
    public void getTypedFunctions(TypedFunctionList f) {
        f.add(
                "execute", args -> {
                    var parsed = parseCommand(args[0].asString(), script.source);
                    return executeCommand(parsed, script.source);
                }, Integer.class, new NamedParam("command", String.class));

        f.add(
                "parse", args -> {
                    var parsed = parseCommand(args[0].asString(), script.source);
                    var hexid = HexId.makeNewUnique(ScriptManager.ScriptCaches.PREPARSED_COMMANDS.keySet());
                    ScriptManager.ScriptCaches.PREPARSED_COMMANDS.put(hexid, parsed);

                    return hexid;
            }, HexId.class, new NamedParam("command", String.class));

        f.add(
                "get_preparsed_argument", args -> {
                    var hi = HexId.fromString(args[0].asString());
                    var parsed = hi.getHashed(ScriptManager.ScriptCaches.PREPARSED_COMMANDS);
                    return getArgument(parsed, args[1].asString());
                }, CommandArgument.class, COMMAND_HEXID, new NamedParam("argument", String.class));

        f.add_Void(
                "modify_preparsed_argument", args -> {
                    var hi = HexId.fromString(args[0].asString());
                    var parsed = hi.getHashed(ScriptManager.ScriptCaches.PREPARSED_COMMANDS);
                    modifyArgument(parsed, args[1].asString(), args[2]);
                    return null;
                }, COMMAND_HEXID, new NamedParam("argument_name", String.class), new NamedParam("new_value", CommandArgument.class));

        f.add_Void("execute_preparsed", args -> {
            var hi = HexId.fromString(args[0].asString());
            var parse = hi.getHashed(ScriptManager.ScriptCaches.PREPARSED_COMMANDS);
            return executeCommand(parse, script.source);
        }, COMMAND_HEXID);

        f.add_Void_Desc("free_preparsed", args -> {
            var hi = HexId.fromString(args[0].asString());
            hi.removeHashed(ScriptManager.ScriptCaches.PREPARSED_COMMANDS);
            return null;
        }, "Releases a preparsed command from the internal hash; in other words, deletes the parsed command.",
                COMMAND_HEXID);
    }

    @Override
    public String getDescription() {
        return "Allows the execution of any Minecraft command with experimental command-preparsing and limited argument modifying, allowing blazingly fast command execution.";
    }

    public static ParseResults<ServerCommandSource> parseCommand(String command, ServerCommandSource source) {
        return source.getDispatcher().parse(command, source);
    }
    public static int executeCommand(ParseResults<ServerCommandSource> command, ServerCommandSource source) {

        try {
            AtomicInteger r = new AtomicInteger();
            source.getDispatcher().setConsumer((context, success, result) -> {
                r.set(result);
            });

            source.getDispatcher().execute(command);

            return r.get();
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public AbstractBaseValue<?, ?> getArgument(ParseResults<ServerCommandSource> command, String argument) {
        return CommandArgument.adapt(command.getContext().getArguments().get(argument).getResult(),
                o -> script.getNullBaseValue().adapt(o));
    }

    public static void modifyArgument(ParseResults<ServerCommandSource> command, String argument, AbstractBaseValue<?, ?> value) {

        if (!LuafyConfig.GLOBAL_CONFIG.allowParsedCommandEditing) {
            Luafy.LOGGER.error("Modifying parsed commands has been disabled in the server config.");
            return;
        }

        Object o;
        var a = command.getContext().getArguments().get(argument);


        var so = value.asScriptObjectIfPresent();

        if (so.isPresent() && so.get() instanceof CommandArgument icaso)
            o = icaso.getArg();
        else {
            Class<?> castTo = a.getResult().getClass();
            Object casted = value.asJavaObject();

            if (castTo == Integer.class && casted instanceof Number n) {
                o = n.intValue();
            } else {
                o = castTo.cast(casted);
            }
        }


        ((ParsedArgumentAccessor) a).setResult(o);
    }
}
