package dev.diamond.luafy.commmand;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.lua.LuaScriptManager;
import dev.diamond.luafy.lua.LuafyLua;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.luaj.vm2.LuaValue;

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
                                                        .executes(LuaCommand::luaCommand_execute)
                                        ) // execute branch
                        ).then(
                                literal("list")
                                        .executes(LuaCommand::luaCommand_list)
                        ) // subcommands
        ); // root

    }

    private static int luaCommand_execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        String arg = StringArgumentType.getString(ctx, "script");
        if (!LuafyLua.LUA_SCRIPTS.containsKey(arg)) {
            throw SCRIPT_NOT_EXIST.create(arg);
        }
        LuaScriptManager manager = LuafyLua.LUA_SCRIPTS.get(arg);
        manager.execute(ctx.getSource());
        return 1;
    }

    private static int luaCommand_list(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(() -> Text.literal("Total: " + LuafyLua.LUA_SCRIPTS.size()), false);
        LuafyLua.LUA_SCRIPTS.forEach((key, value) -> ctx.getSource().sendFeedback(() -> Text.literal(key), false));
        return 1;
    }
}
