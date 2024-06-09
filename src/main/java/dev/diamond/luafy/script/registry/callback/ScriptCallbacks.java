package dev.diamond.luafy.script.registry.callback;

import com.google.gson.annotations.SerializedName;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.abstraction.NamedParam;
import dev.diamond.luafy.script.api.obj.entity.EntityScriptObject;
import dev.diamond.luafy.script.api.obj.entity.LivingEntityScriptObject;
import dev.diamond.luafy.script.api.obj.entity.PlayerEntityScriptObject;
import dev.diamond.luafy.script.api.obj.math.Vec3dScriptObject;
import dev.diamond.luafy.script.api.obj.minecraft.WorldScriptObject;
import dev.diamond.luafy.script.api.obj.minecraft.block.BlockStateScriptObject;
import dev.diamond.luafy.script.api.obj.minecraft.item.ItemStackScriptObject;
import dev.diamond.luafy.script.api.obj.util.AdvancementEntryScriptObject;

import java.util.List;

public class ScriptCallbacks {

    // Params
    private static final NamedParam LIVING_THIS =             new NamedParam("this", LivingEntityScriptObject.class);
    private static final NamedParam LIVING_ATTACKER =         new NamedParam("attacker", LivingEntityScriptObject.class);
    private static final NamedParam STACK =                   new NamedParam("stack", ItemStackScriptObject.class);
    private static final NamedParam EFFECT_ID =               new NamedParam("effect", String.class);
    private static final NamedParam PLAYER =                  new NamedParam("player", PlayerEntityScriptObject.class);
    private static final NamedParam USER_PLAYER =             new NamedParam("user", PlayerEntityScriptObject.class);
    private static final NamedParam ENTITY =                  new NamedParam("entity", EntityScriptObject.class);
    private static final NamedParam AMOUNT_NUM =              new NamedParam("amount", Number.class);
    private static final NamedParam AMPLIFIER =               new NamedParam("amplifier", Number.class);
    private static final NamedParam DURATION =                new NamedParam("duration", Number.class);
    private static final NamedParam SOURCE_STR =              new NamedParam("source", String.class);
    private static final NamedParam ADVANCEMENT =             new NamedParam("advancement", AdvancementEntryScriptObject.class);
    private static final NamedParam WORLD =                   new NamedParam("world", WorldScriptObject.class);
    private static final NamedParam POS =                     new NamedParam("pos", Vec3dScriptObject.class);
    private static final NamedParam BLOCK =                   new NamedParam("block", BlockStateScriptObject.class);
    private static final NamedParam EVENT_ID =                new NamedParam("event_id", String.class);
    private static final NamedParam EMITTER_ENTITY =          new NamedParam("emitter_entity", EntityScriptObject.class);
    private static final NamedParam EMITTER_POS =             new NamedParam("emitter_pos", Vec3dScriptObject.class);
    private static final NamedParam AGE =                     new NamedParam("age", Number.class);
    private static final NamedParam IS_MAINHAND =             new NamedParam("is_mainhand", Boolean.class);
    private static final NamedParam STAT =                    new NamedParam("stat", String.class);
    private static final NamedParam WAS =                     new NamedParam("was", Object.class);
    private static final NamedParam NOW =                     new NamedParam("now", Object.class);


    // Events
    public static final ScriptCallbackEvent TICK =                      new ScriptCallbackEvent(Luafy.id("tick"),                   "Runs every server tick."                                   );
    public static final ScriptCallbackEvent LOAD =                      new ScriptCallbackEvent(Luafy.id("load"),                   "Runs when the datapack is loaded."                         );
    public static final ScriptCallbackEvent ON_DAY_START =              new ScriptCallbackEvent(Luafy.id("daybreak"),               "Runs when an in-game day begins."                          );
    public static final ScriptCallbackEvent ON_NIGHT_START =            new ScriptCallbackEvent(Luafy.id("nightfall"),              "Runs when an in-game night begins."                        );
    public static final ScriptCallbackEvent ON_ENTITY_DIES =            new ScriptCallbackEvent(Luafy.id("entity_dies"),            "Runs when any entity dies.",                               LIVING_ATTACKER, LIVING_THIS);
    public static final ScriptCallbackEvent ON_ENTITY_HURTS =           new ScriptCallbackEvent(Luafy.id("entity_hurts"),           "Runs when any entity takes damage.",                       LIVING_ATTACKER, LIVING_THIS, AMOUNT_NUM, SOURCE_STR);
    public static final ScriptCallbackEvent ON_ADVANCEMENT_OBTAINED =   new ScriptCallbackEvent(Luafy.id("advancement_obtained"),   "Runs when any player makes any advancement.",              ADVANCEMENT, PLAYER);
    public static final ScriptCallbackEvent ON_ITEM_USED =              new ScriptCallbackEvent(Luafy.id("item_used"),              "Runs when any player right-clicks holding any item.",      STACK, USER_PLAYER);
    public static final ScriptCallbackEvent ON_ITEM_USED_ON_BLOCK =     new ScriptCallbackEvent(Luafy.id("item_used_on_block"),     "Runs when any player right-clicks any item on a block.",   STACK, WORLD, POS, BLOCK, USER_PLAYER);
    public static final ScriptCallbackEvent ON_ITEM_USED_ON_ENTITY =    new ScriptCallbackEvent(Luafy.id("item_used_on_entity"),    "Runs when any player right-clicks any item on any entity.",STACK, ENTITY, USER_PLAYER);
    public static final ScriptCallbackEvent ON_STAT_CHANGED =           new ScriptCallbackEvent(Luafy.id("stat_changes"),           "Runs when any player's statistics update.",                PLAYER, STAT, WAS, NOW);
    public static final ScriptCallbackEvent ON_GAME_EVENT =             new ScriptCallbackEvent(Luafy.id("game_event_emits"),       "Runs when any game event is emitted.",                     EVENT_ID, EMITTER_ENTITY, EMITTER_POS);
    public static final ScriptCallbackEvent HAND_SWINGS =               new ScriptCallbackEvent(Luafy.id("hand_swings"),            "Runs when any player's arm/hand swings.",                  PLAYER, IS_MAINHAND);
    public static final ScriptCallbackEvent TRY_ATTACK =                new ScriptCallbackEvent(Luafy.id("attack_tried"),           "Runs when any entity tries to attack.",                    LIVING_ATTACKER, LIVING_THIS);
    public static final ScriptCallbackEvent EFFECT_APPLIED =            new ScriptCallbackEvent(Luafy.id("effect_applied"),         "Runs when any entity receives a status effect.",           LIVING_THIS, EFFECT_ID, DURATION, AMPLIFIER);
    public static final ScriptCallbackEvent EFFECT_LOST =               new ScriptCallbackEvent(Luafy.id("effect_lost"),            "Runs when any entity loses a status effect.",              LIVING_THIS, EFFECT_ID);
    public static final ScriptCallbackEvent REGENERATES_HEALTH =        new ScriptCallbackEvent(Luafy.id("health_regenerated"),     "Runs when any entities health increases.",                 LIVING_THIS, AMOUNT_NUM);
    public static final ScriptCallbackEvent EATS =                      new ScriptCallbackEvent(Luafy.id("eats"),                   "Runs when any entity eats.",                               LIVING_THIS, STACK);
    public static final ScriptCallbackEvent EXHAUST =                   new ScriptCallbackEvent(Luafy.id("exhuasts"),               "Runs when any player loses hunger.",                       PLAYER, AMOUNT_NUM);
    public static final ScriptCallbackEvent SPAWN =                     new ScriptCallbackEvent(Luafy.id("spawns"),                 "Runs when any entity spawns.",                             ENTITY);
    public static final ScriptCallbackEvent TICK_ENTITY =               new ScriptCallbackEvent(Luafy.id("entity_ticks"),           "Runs when any entity ticks.",                              AGE);
    public static final ScriptCallbackEvent CONNECTS_TO_SERVER =        new ScriptCallbackEvent(Luafy.id("connects_to_server"),     "Runs when any player connects to the server.",             PLAYER);
    public static final ScriptCallbackEvent DISCONNECTS_FROM_SERVER =   new ScriptCallbackEvent(Luafy.id("disconnects_from_server"),"Runs when any player disconnects from the server.",        PLAYER);
    public static final ScriptCallbackEvent SERVER_CLOSES =             new ScriptCallbackEvent(Luafy.id("server_closes"),          "Runs when the server shuts down."                          );

    public static void registerAll() {
        TICK.register();
        LOAD.register();

        ON_DAY_START.register();
        ON_NIGHT_START.register();
        ON_ENTITY_DIES.register();
        ON_ENTITY_HURTS.register();
        ON_ADVANCEMENT_OBTAINED.register();
        ON_ITEM_USED.register();
        ON_ITEM_USED_ON_BLOCK.register();
        ON_ITEM_USED_ON_ENTITY.register();
        ON_STAT_CHANGED.register();
        ON_GAME_EVENT.register();
        HAND_SWINGS.register();
        TRY_ATTACK.register();

        EFFECT_APPLIED.register();
        EFFECT_LOST.register();
        REGENERATES_HEALTH.register();
        EATS.register();
        EXHAUST.register();
        SPAWN.register();
        TICK_ENTITY.register();
        CONNECTS_TO_SERVER.register();
        DISCONNECTS_FROM_SERVER.register();
        SERVER_CLOSES.register();
    }

    public static class CallbackScriptBean {

        @SerializedName("events")
        public List<CallbackEventBean> eventCallbacks;

        public static class CallbackEventBean {
            @SerializedName("event")
            public String id;

            @SerializedName("scripts")
            public List<String> scriptIds;

            @SerializedName("use_own_thread")
            public boolean ownThread = false;
        }
    }

}
