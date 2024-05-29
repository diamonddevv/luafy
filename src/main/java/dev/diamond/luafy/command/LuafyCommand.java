package dev.diamond.luafy.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.BaseValueConversions;
import dev.diamond.luafy.script.registry.callback.ScriptCallbackEvent;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LuafyCommand {
    private static final DynamicCommandExceptionType SCRIPT_NOT_EXIST = new DynamicCommandExceptionType((o) -> Text.literal("Script '" + o + "' does not exist. Run { /lua list } to get a list of all scripts."));
    private static final DynamicCommandExceptionType SANDBOX_STRATEGY_NOT_EXIST = new DynamicCommandExceptionType((o) -> Text.literal("No sandbox strategy with id '" + o + "' was found"));

    public static void registerLuaCommand(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        

        dispatcher.register(
                literal("luafy").requires(src -> src.hasPermissionLevel(2))
                        .then(
                                literal("execute")
                                        .then(
                                                argument("subscription", StringArgumentType.string())
                                                        .then(
                                                                argument("context", NbtCompoundArgumentType.nbtCompound())
                                                                        .executes(s -> luaCommand_executeWithContext(s, false))
                                                                        .then(
                                                                                literal("threaded").executes(s -> luaCommand_executeWithContext(s, true))
                                                                        )
                                                        ).then(
                                                                literal("threaded")
                                                                        .executes(s -> luaCommand_execute(s, true))
                                                        ).executes(s -> luaCommand_execute(s, false))
                                        ) // execute branch
                        ).then(
                                literal("list")
                                        .then(
                                                literal("scripts").executes(LuafyCommand::luaCommand_listScripts)
                                        ).then(
                                                literal("events").then(argument("event", StringArgumentType.string()).executes(LuafyCommand::luaCommand_listEvents))
                                        ) // lists
                        ) // subcommands
        ); // root
    }


    private static int luaCommand_execute(CommandContext<ServerCommandSource> ctx, boolean threaded) throws CommandSyntaxException {
        String arg = StringArgumentType.getString(ctx, "subscription");
        boolean success = execute(arg, ctx, null, threaded);
        return success ? 1 : 0;
    }
    private static int luaCommand_executeWithContext(CommandContext<ServerCommandSource> ctx, boolean threaded) throws CommandSyntaxException {
        String arg = StringArgumentType.getString(ctx, "subscription");
        NbtCompound nbtContext = NbtCompoundArgumentType.getNbtCompound(ctx, "context");
        boolean success = execute(arg, ctx, nbtContext, threaded);
        return success ? 1 : 0;
    }

    private static int luaCommand_listScripts(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(() -> Text.literal("Total: " + ScriptManager.SCRIPTS.size()), false);
        ScriptManager.SCRIPTS.forEach((key, value) -> ctx.getSource().sendFeedback(() -> Text.literal(key), false));
        return 1;
    }
    private static int luaCommand_listEvents(CommandContext<ServerCommandSource> ctx) {
        ScriptCallbackEvent event = ScriptCallbackEvent.fromStringId(StringArgumentType.getString(ctx, "event"));

        ctx.getSource().sendFeedback(() -> Text.literal("Event: " + event.toString()), false);

        ctx.getSource().sendFeedback(() ->
                        Text.literal("Subscribed Scripts (" + ScriptManager.EVENT_CALLBACKS.get(event).size() + "): "), false
        );

        ScriptManager.EVENT_CALLBACKS.get(event)
                .forEach(subscription -> ctx.getSource()
                        .sendFeedback(() -> Text.literal(subscription.getScriptId()), false)
                );
        return 1;
    }

    //
    private static boolean execute(String id, CommandContext<ServerCommandSource> ctx, @Nullable NbtCompound nbtContext, boolean threaded) throws CommandSyntaxException {
        if (!ScriptManager.hasScript(id)) {
            throw SCRIPT_NOT_EXIST.create(id);
        }
        var script = ScriptManager.getScript(id);

        ScriptManager.execute(
                id,
                ctx.getSource(),
                nbtContext == null ? null : BaseValueConversions.nbtObjToBase(nbtContext, s -> script.getNullBaseValue().adapt(s)),
                threaded,
                "$command"
        );

        return true;
    }
}
