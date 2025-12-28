package com.lx862.mtrsurveyor.config;

import com.lx862.mtrsurveyor.MTRDataSummary;
import com.lx862.mtrsurveyor.MTRSurveyor;
import com.lx862.mtrsurveyor.landmark.MTRLandmarkManager;
import com.lx862.mtrsurveyor.mixin.MTRAccessorMixin;
import com.lx862.mtrsurveyor.mixin.MainAccessorMixin;
import com.lx862.mtrsurveyor.util.MTRUtil;
import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.Config;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Processor;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.mtr.core.Main;
import org.mtr.core.simulation.Simulator;

import java.nio.file.Path;

@Processor("syncLandmarks")
public class MTRSurveyorConfig extends ReflectiveConfig {
    private static final Path configDirectory = FabricLoader.getInstance().getConfigDir();
    public static final MTRSurveyorConfig INSTANCE = MTRSurveyorConfig.createToml(configDirectory, "", "mtrsurveyor", MTRSurveyorConfig.class);

    @Comment("Change the mod initialization log message to be something more formal... if that's not your thing :>")
    public final TrackedValue<Boolean> formalInitLog = this.value(false);

    @Comment("Whether landmarks should be automatically created & synced when an MTR-related change occurs.")
    public final TrackedValue<Boolean> enabled = this.value(true);

    @Comment("Configuration related to visibility of area landmarks")
    public final Visibility visibility = new Visibility();

    public static class Visibility extends Section {
        @Comment("Whether station landmarks should be added to the map.")
        public final TrackedValue<Boolean> showStationLandmarks = this.value(true);

        @Comment("Whether depot landmarks should be added to the map.")
        public final TrackedValue<Boolean> showDepotLandmarks = this.value(false);

        @Comment("Whether empty stations (i.e. with no routes) should be added to the map.")
        public final TrackedValue<Boolean> showEmptyStation = this.value(false);

        @Comment("Whether MTR route marked as hidden should be appended to the station description.")
        public final TrackedValue<Boolean> showHiddenRoute = this.value(false);
    }

    // Static initialization
    public static void init() {
    }

    public void syncLandmarks(Config.Builder builder) {
        builder.callback(newConfig -> {
            MinecraftServer mcServer = MTRSurveyor.getServerInstance();
            if(mcServer != null && mcServer.isRunning()) {
                Main main = MTRAccessorMixin.getMain();
                for(Simulator simulator : ((MainAccessorMixin)main).getSimulators()) {
                    World world = mcServer.getWorld(RegistryKey.of(RegistryKeys.WORLD, MTRUtil.dimensionToId(simulator.dimension)));
                    MTRDataSummary mtrDataSummary = new MTRDataSummary(simulator);
                    MTRLandmarkManager.syncLandmarks(world, mtrDataSummary);
                }
            }
        });
    }
}
