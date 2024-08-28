package dev.diamond.luafy.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.BaseValueConversions;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.registry.callback.ScriptCallbackEvent;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WritableBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LuafyCommand {
    private static final DynamicCommandExceptionType SCRIPT_NOT_EXIST = new DynamicCommandExceptionType((o) -> Text.literal("Script '" + o + "' does not exist. Run { /luafy list scripts } to get a list of all scripts."));
    private static final DynamicCommandExceptionType LANG_NOT_EXIST = new DynamicCommandExceptionType((o) -> Text.literal("No script language with id '" + o + "' was found"));

    public static void registerLuaCommand(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {


        dispatcher.register(
                literal("luafy").requires(src -> src.hasPermissionLevel(2))
                        .then(
                                literal("execute")
                                        .then(
                                                argument("script", StringArgumentType.string())
                                                        .then(
                                                                argument("context", NbtCompoundArgumentType.nbtCompound())
                                                                        .executes(s -> luaCommand_executeWithContext(s, false))
                                                                        .then(
                                                                                literal("threaded").executes(s -> luaCommand_executeWithContext(s, true))
                                                                        )
                                                        ).then(
                                                                literal("threaded")
                                                                        .executes(s -> luaCommand_execute(s, true))
                                                        ).then(
                                                               argument("function", StringArgumentType.string())
                                                                       .executes(s -> luaCommand_executeFunction(s, false))
                                                        ).executes(s -> luaCommand_execute(s, false))
                                        ) // execute branch
                        ).then(
                                literal("list")
                                        .then(
                                                literal("scripts").executes(LuafyCommand::luaCommand_listScripts)
                                        ).then(
                                                literal("events").then(argument("event", StringArgumentType.string()).executes(LuafyCommand::luaCommand_listEvents))
                                        ).then(
                                                literal("langs").executes(LuafyCommand::luaCommand_listLangs)
                                        ) // lists
                        ).then(
                                literal("eval").requires(src -> src.hasPermissionLevel(4))
                                        .then(
                                                argument("lang", StringArgumentType.string())
                                                        .then(
                                                                argument("code", StringArgumentType.greedyString())
                                                                        .executes(LuafyCommand::luaCommand_eval)
                                                        )
                                                        .then(
                                                                literal("book").executes(LuafyCommand::luaCommand_evalButBook)
                                                        )

                                        )
                        ) // subcommands
        ); // root
    }

    private static int luaCommand_execute(CommandContext<ServerCommandSource> ctx, boolean threaded) throws CommandSyntaxException {
        String arg = StringArgumentType.getString(ctx, "script");
        boolean success = execute(arg, null, ctx, null, threaded);
        return success ? 1 : 0;
    }

    private static int luaCommand_eval(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Identifier langId = Identifier.of(StringArgumentType.getString(ctx, "lang"));
        String code = StringArgumentType.getString(ctx, "code");

        var lang = Luafy.Registries.SCRIPT_LANGUAGES.getOrEmpty(langId);
        if (lang.isPresent()) {
            AbstractScript<?> script = lang.get().readScript(code);
            script.execute(ctx.getSource(), null);

            return 1;
        } else {
            throw LANG_NOT_EXIST.create(langId);
        }
    }

    private static int luaCommand_evalButBook(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Identifier langId = Identifier.of(StringArgumentType.getString(ctx, "lang"));

        var player = ctx.getSource().getPlayer();

        var ex = new SimpleCommandExceptionType(() -> "This command must be executed as a player entity holding a book and quill in mainhand");

        if (player == null) throw ex.create();
        if (!(player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof WritableBookItem)) throw ex.create();

        ItemStack baq = player.getStackInHand(Hand.MAIN_HAND);

        Stream<String> content = baq.get(DataComponentTypes.WRITABLE_BOOK_CONTENT).stream(false);
        // concatenate content strings page by page. new page =/= new line
        String code = content.reduce((accum, s) -> accum + s).get();

        var lang = Luafy.Registries.SCRIPT_LANGUAGES.getOrEmpty(langId);
        if (lang.isPresent()) {
            AbstractScript<?> script = lang.get().readScript(code);
            script.execute(ctx.getSource(), null);

            return 1;
        } else {
            throw LANG_NOT_EXIST.create(langId);
        }
    }

    private static int luaCommand_executeFunction(CommandContext<ServerCommandSource> ctx, boolean threaded) throws CommandSyntaxException {
        String arg = StringArgumentType.getString(ctx, "script");
        String function = StringArgumentType.getString(ctx, "function");

        boolean success = execute(arg, function, ctx, null, threaded);
        return success ? 1 : 0;
    }

    private static int luaCommand_executeWithContext(CommandContext<ServerCommandSource> ctx, boolean threaded) throws CommandSyntaxException {
        String arg = StringArgumentType.getString(ctx, "script");
        NbtCompound nbtContext = NbtCompoundArgumentType.getNbtCompound(ctx, "context");
        boolean success = execute(arg, null, ctx, nbtContext, threaded);
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

    private static int luaCommand_listLangs(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(() -> Text.literal("Total: " + Luafy.Registries.SCRIPT_LANGUAGES.size()), false);
        Luafy.Registries.SCRIPT_LANGUAGES.forEach((lang) ->
                ctx.getSource().sendFeedback(() ->
                        Text.literal(Luafy.Registries.SCRIPT_LANGUAGES.getId(lang).toString()), false));
        return 1;
    }

    //
    private static boolean execute(String id, String function, CommandContext<ServerCommandSource> ctx, @Nullable NbtCompound nbtContext, boolean threaded) throws CommandSyntaxException {
        if (!ScriptManager.hasScript(id)) {
            throw SCRIPT_NOT_EXIST.create(id);
        }
        var script = ScriptManager.getScript(id);

        ScriptManager.execute(
                id,
                function,
                ctx.getSource(),
                nbtContext == null ? null : BaseValueConversions.nbtObjToBase(nbtContext, s -> script.getNullBaseValue().adapt(s)),
                threaded,
                "$command"
        );

        return true;
    }
}
