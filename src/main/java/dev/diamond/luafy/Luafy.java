package dev.diamond.luafy;

import dev.diamond.luafy.autodocs.Autodoc;
import dev.diamond.luafy.autodocs.AutodocPrinter;
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
import dev.diamond.luafy.script.registry.objects.ScriptObjectFactory;
import dev.diamond.luafy.script.registry.objects.ScriptObjectRegistry;
import dev.diamond.luafy.script.registry.sandbox.Apis;
import dev.diamond.luafy.script.registry.sandbox.SandboxableApi;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

public class Luafy implements ModInitializer {
	public static final String MODID = "luafy";
	public static final String LUAJ_VER = "3.0.8";

    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final ScriptResourceLoader LUA_SCRIPT_RESOURCES = new ScriptResourceLoader();
	public static final CallbackScriptResourceLoader CALLBACK_RESOURCES = new CallbackScriptResourceLoader();
	//public static final SandboxStrategyResourceLoader SANDBOXES = new SandboxStrategyResourceLoader();
	public static final StaticScriptResourceResourceLoader STATIC_RESOURCES = new StaticScriptResourceResourceLoader();

	public static class Registries {
		public static RegistryKey<Registry<ScriptCallbackEvent>> 		CALLBACK_REGISTRY_KEY;
		public static RegistryKey<Registry<SandboxableApi<?>>> 			API_REGISTRY_KEY;
		public static RegistryKey<Registry<ScriptLanguage<?>>> 			SCRIPT_LANG_REGISTRY_KEY;
		public static RegistryKey<Registry<ByteBufDecoder>> 			BYTEBUF_DECODER_REGISTRY_KEY;
		public static RegistryKey<Registry<ScriptObjectFactory<?>>> 	SCRIPT_OBJECT_TYPES_REGISTRY_KEY;

		public static Registry<ScriptCallbackEvent> 	EVENT_CALLBACKS;
		public static Registry<SandboxableApi<?>> 		APIS;
		public static Registry<ScriptLanguage<?>> 		SCRIPT_LANGUAGES;
		public static Registry<ByteBufDecoder> 			BYTEBUF_DECODERS;
		public static Registry<ScriptObjectFactory<?>> 	SCRIPT_OBJECTS;

		private static <T> Registry<T> create(RegistryKey<Registry<T>> key) {
			return FabricRegistryBuilder.createSimple(key).buildAndRegister();
		}
		private static <T> RegistryKey<Registry<T>> of(String path) {
			return RegistryKey.ofRegistry(id(path));
		}


		static {
			CALLBACK_REGISTRY_KEY = 			of("callback_events");
			API_REGISTRY_KEY = 					of("sandboxable_apis");
			SCRIPT_LANG_REGISTRY_KEY = 			of("script_languages");
			BYTEBUF_DECODER_REGISTRY_KEY = 		of("bytebuf_decoders");
			SCRIPT_OBJECT_TYPES_REGISTRY_KEY = 	of("script_object_types");


			EVENT_CALLBACKS = 		create(CALLBACK_REGISTRY_KEY);
			APIS = 					create(API_REGISTRY_KEY);
			SCRIPT_LANGUAGES =		create(SCRIPT_LANG_REGISTRY_KEY);
			BYTEBUF_DECODERS =		create(BYTEBUF_DECODER_REGISTRY_KEY);
			SCRIPT_OBJECTS = 		create(SCRIPT_OBJECT_TYPES_REGISTRY_KEY);

		}
	}

	public static class Collections {
		public static Collection<Autodoc<?, ?>> AUTODOCS;


		static {
			AUTODOCS = new ArrayList<>();
		}
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Initialising Luafy with LuaJ Version {}. Thank you FiguraMC for maintaining LuaJ!", LUAJ_VER);

		CommandRegistrationCallback.EVENT.register(LuafyCommand::registerLuaCommand);

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(LUA_SCRIPT_RESOURCES);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(CALLBACK_RESOURCES);
		//ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SANDBOXES);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(STATIC_RESOURCES);

		LuafyConfig.initializeConfig();

		// registries
		ScriptCallbacks.registerAll();
		ScriptLanguages.registerAll();
		Apis.registerAll();
		ScriptObjectRegistry.registerAll();
		ByteBufDecoder.Decoders.registerAll();

		// collections
		AutodocPrinter.addAll();

		// make docs
		if (FabricLoaderImpl.INSTANCE.isDevelopmentEnvironment()) AutodocPrinter.printDocs(MODID);
	}


	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}


}