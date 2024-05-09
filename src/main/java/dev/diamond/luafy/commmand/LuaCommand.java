package dev.diamond.luafy.commmand;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.diamond.luafy.lua.LuaScript;
import dev.diamond.luafy.lua.LuafyLua;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LuaCommand {
    private static final DynamicCommandExceptionType SCRIPT_NOT_EXIST = new DynamicCommandExceptionType((o) -> Text.literal("Script '" + o + "' does not exist. Run { /lua list } to get a list of all scripts."));

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
                        ) // subcommands
        ); // root

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

    private static boolean execute(String id, CommandContext<ServerCommandSource> ctx, NbtCompound nbtContext) throws CommandSyntaxException {
        if (!LuafyLua.LUA_SCRIPTS.containsKey(id)) {
            throw SCRIPT_NOT_EXIST.create(id);
        }
        LuaScript manager = LuafyLua.LUA_SCRIPTS.get(id);
        manager.execute(ctx.getSource(), nbtContext);

        return true;
    }

    private static int luaCommand_list(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(() -> Text.literal("Total: " + LuafyLua.LUA_SCRIPTS.size()), false);
        LuafyLua.LUA_SCRIPTS.forEach((key, value) -> ctx.getSource().sendFeedback(() -> Text.literal(key), false));
        return 1;
    }
}
