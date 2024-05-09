package dev.diamond.luafy.lua.object;

import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerLuaObject extends EntityLuaObject {


    public final ServerPlayerEntity player;

    public PlayerLuaObject(ServerPlayerEntity spe) {
        super(spe);
        this.player = spe;
    }
    
    @Override
    public void create() {
    }
}
