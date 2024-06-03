package dev.diamond.luafy;

import dev.diamond.luafy.command.LuafyCommand;
import dev.diamond.luafy.config.LuafyConfig;
import dev.diamond.luafy.resource.CallbackScriptResourceLoader;
import dev.diamond.luafy.resource.ScriptResourceLoader;
import dev.diamond.luafy.resource.StaticScriptResourceResourceLoader;
import dev.diamond.luafy.script.registry.ByteBufDecoder;
import dev.diamond.luafy.script.registry.callback.ScriptCallbackEvent;
import dev.diamond.luafy.script.registry.callback.ScriptCallbacks;
import dev.diamond.luafy.script.registry.lang.ScriptLanguage;
import dev.diamond.luafy.script.registry.lang.ScriptLanguages;
import dev.diamond.luafy.script.registry.sandbox.SandboxableApi;
import dev.diamond.luafy.script.registry.sandbox.Apis;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Luafy implements ModInitializer {
	public static final String MODID = "luafy";
	public static final String LUAJ_VER = "3.0.8";

    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final ScriptResourceLoader LUA_SCRIPT_RESOURCES = new ScriptResourceLoader();
	public static final CallbackScriptResourceLoader CALLBACK_RESOURCES = new CallbackScriptResourceLoader();
	//public static final SandboxStrategyResourceLoader SANDBOXES = new SandboxStrategyResourceLoader();
	public static final StaticScriptResourceResourceLoader STATIC_RESOURCES = new StaticScriptResourceResourceLoader();

	public static class Registries {
		public static RegistryKey<Registry<ScriptCallbackEvent>> CALLBACK_REGISTRY_KEY;
		public static RegistryKey<Registry<SandboxableApi<?>>> API_REGISTRY_KEY;
		public static RegistryKey<Registry<ScriptLanguage<?>>> SCRIPT_LANG_REGISTRY_KEY;
		public static RegistryKey<Registry<ByteBufDecoder>> BYTEBUF_DECODER_REGISTRY_KEY;

		public static Registry<ScriptCallbackEvent> CALLBACK_REGISTRY;
		public static Registry<SandboxableApi<?>> API_REGISTRY;
		public static Registry<ScriptLanguage<?>> SCRIPT_LANG_REGISTRY;
		public static Registry<ByteBufDecoder> BYTEBUF_DECODER_REGISTRY;
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Initialising Luafy with LuaJ Version {}. Thank you FiguraMC for maintaining LuaJ!", LUAJ_VER);

		CommandRegistrationCallback.EVENT.register(LuafyCommand::registerLuaCommand);

		Registries.CALLBACK_REGISTRY_KEY = RegistryKey.ofRegistry(id("callback_events"));
		Registries.API_REGISTRY_KEY = RegistryKey.ofRegistry(id("sandboxable_apis"));
		Registries.SCRIPT_LANG_REGISTRY_KEY = RegistryKey.ofRegistry(id("script_languages"));
		Registries.BYTEBUF_DECODER_REGISTRY_KEY = RegistryKey.ofRegistry(id("bytebuf_decoders"));

		Registries.CALLBACK_REGISTRY = FabricRegistryBuilder.createSimple(Registries.CALLBACK_REGISTRY_KEY).buildAndRegister();
		Registries.API_REGISTRY = FabricRegistryBuilder.createSimple(Registries.API_REGISTRY_KEY).buildAndRegister();
		Registries.SCRIPT_LANG_REGISTRY = FabricRegistryBuilder.createSimple(Registries.SCRIPT_LANG_REGISTRY_KEY).buildAndRegister();
		Registries.BYTEBUF_DECODER_REGISTRY = FabricRegistryBuilder.createSimple(Registries.BYTEBUF_DECODER_REGISTRY_KEY).buildAndRegister();

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(LUA_SCRIPT_RESOURCES);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(CALLBACK_RESOURCES);
		//ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SANDBOXES);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(STATIC_RESOURCES);

		LuafyConfig.initializeConfig();

		ScriptCallbacks.registerAll();
		ScriptLanguages.registerAll();
		Apis.registerAll();
		ByteBufDecoder.Decoders.registerAll();
	}


	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}


}