package dev.diamond.luafy.commmand;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.diamond.luafy.config.LuafyConfig;
import dev.diamond.luafy.script.old.Old_LuaScript;
import dev.diamond.luafy.script.old.LuaTypeConversions;
import dev.diamond.luafy.script.old.LuafyLua;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LuaCommand {
    private static final DynamicCommandExceptionType SCRIPT_NOT_EXIST = new DynamicCommandExceptionType((o) -> Text.literal("Script '" + o + "' does not exist. Run { /lua list } to get a list of all scripts."));
    private static final DynamicCommandExceptionType SANDBOX_STRATEGY_NOT_EXIST = new DynamicCommandExceptionType((o) -> Text.literal("No sandbox strategy with id '" + o + "' was found"));

    public static void registerLuaCommand(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        

        dispatcher.register(
                literal("lua").requires(src -> src.hasPermissionLevel(2))
                        .then(
                                literal("execute")
                                        .then(
                                                argument("script", StringArgumentType.string())
                                                        .then(
                                                                argument("context", NbtCompoundArgumentType.nbtCompound())
                                                                        .executes(LuaCommand::luaCommand_executeWithContext)
                                                        ).executes(LuaCommand::luaCommand_execute)
                                        ) // execute branch
                        ).then(
                                literal("list")
                                        .executes(LuaCommand::luaCommand_list)
                        ).then(
                                literal("sandbox")
                                        .then(
                                                argument("sandbox", StringArgumentType.string())
                                                        .executes(LuaCommand::luaCommand_setSandboxStrategy)
                                        ).executes((ctx) -> luaCommand_setSandboxStrategy(ctx, true))
                                        .then(
                                                literal("list").executes(LuaCommand::luaCommand_listSandboxes)
                                        )
                        )// subcommands
        ); // root

    }

    private static int luaCommand_setSandboxStrategy(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return luaCommand_setSandboxStrategy(ctx, false);
    }
    private static int luaCommand_setSandboxStrategy(CommandContext<ServerCommandSource> ctx, boolean clear) throws CommandSyntaxException {


        if (clear) {
            LuafyConfig.GLOBAL_CONFIG.sandboxStrategy = null;
            LuafyConfig.writeConfig();
            ctx.getSource().sendFeedback(() -> Text.literal("Reset Lua sandbox strategy (using fallback). Please run /reload for this to take effect!"), true);
        } else {

            String arg = StringArgumentType.getString(ctx, "sandbox");
            if (LuafyLua.SANDBOX_STRATEGIES.containsKey(arg)) {
                LuafyConfig.GLOBAL_CONFIG.sandboxStrategy = arg;
                LuafyConfig.writeConfig();
                ctx.getSource().sendFeedback(() -> Text.literal("Set Lua sandbox strategy. Please run /reload for this to take effect!"), true);
            } else {
                throw SANDBOX_STRATEGY_NOT_EXIST.create(arg);
            }
        }

        return 1;
    }

    private static int luaCommand_execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        String arg = StringArgumentType.getString(ctx, "script");
        boolean success = execute(arg, ctx, null);
        return success ? 1 : 0;
    }
    private static int luaCommand_executeWithContext(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        String arg = StringArgumentType.getString(ctx, "script");
        NbtCompound nbtContext = NbtCompoundArgumentType.getNbtCompound(ctx, "context");
        boolean success = execute(arg, ctx, nbtContext);
        return success ? 1 : 0;
    }

    private static boolean execute(String id, CommandContext<ServerCommandSource> ctx, @Nullable NbtCompound nbtContext) throws CommandSyntaxException {
        if (!LuafyLua.LUA_SCRIPTS.containsKey(id)) {
            throw SCRIPT_NOT_EXIST.create(id);
        }
        Old_LuaScript manager = LuafyLua.LUA_SCRIPTS.get(id);
        manager.execute(ctx.getSource(), nbtContext == null ? null : LuaTypeConversions.tableFromNbt(nbtContext));

        return true;
    }

    private static int luaCommand_list(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(() -> Text.literal("Total: " + LuafyLua.LUA_SCRIPTS.size()), false);
        LuafyLua.LUA_SCRIPTS.forEach((key, value) -> ctx.getSource().sendFeedback(() -> Text.literal(key), false));
        return 1;
    }

    private static int luaCommand_listSandboxes(CommandContext<ServerCommandSource> ctx) {
        if (LuafyConfig.GLOBAL_CONFIG.sandboxStrategy != null) ctx.getSource().sendFeedback(() -> Text.literal("Current: " + LuafyConfig.GLOBAL_CONFIG.sandboxStrategy), false);
        ctx.getSource().sendFeedback(() -> Text.literal("Total: " + LuafyLua.SANDBOX_STRATEGIES.size()), false);
        LuafyLua.SANDBOX_STRATEGIES.forEach((key, value) -> ctx.getSource().sendFeedback(() -> Text.literal(key), false));
        return 1;
    }
}
