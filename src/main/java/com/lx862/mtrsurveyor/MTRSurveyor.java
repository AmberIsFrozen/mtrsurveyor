package com.lx862.mtrsurveyor;

import com.lx862.mtrsurveyor.landmark.MTRLandmarkComponentTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MTRSurveyor implements ModInitializer {

    public static final String MOD_ID = "mtrsurveyor";
    public static final String MOD_NAME = "MTR: Surveyor Integration";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    private static MinecraftServer serverInstance = null;

    @Override
    public void onInitialize() {
        LOGGER.info("[{}] You get a landmark, you get a landmark, every-nyan gets a landmark! >w<", MOD_NAME);
        MTRLandmarkComponentTypes.init();
        Config.load();

        ServerLifecycleEvents.SERVER_STARTING.register(minecraftServer -> {
            serverInstance = minecraftServer;
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(minecraftServer -> {
            serverInstance = null;
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, c) -> {
            Commands.register(dispatcher);
        });
    }

    public static MinecraftServer getServerInstance() {
        return serverInstance;
    }

    public static Identifier id(String path) {
        Identifier id = Identifier.of(MOD_ID, path);
        if(id == null) throw new InvalidIdentifierException("Invalid path: " + path);
        return id;
    }
}
