package com.lx862.mtrsurveyor.config;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class MTRSurveyorConfig extends ReflectiveConfig {
    private static final Path configDirectory = FabricLoader.getInstance().getConfigDir();
    public static final MTRSurveyorConfig INSTANCE = MTRSurveyorConfig.createToml(configDirectory, "", "mtrsurveyor", MTRSurveyorConfig.class);

    @Comment("Change the mod initialization log message to be something more formal... if that's not your thing :>")
    public final TrackedValue<Boolean> formalInitLog = this.value(false);

    @Comment("Whether landmarks should be automatically created & synced when an MTR-related change occurs.")
    public final TrackedValue<Boolean> enableAutoSync = this.value(true);

    @Comment("Whether station landmarks should be added to the map.")
    public final TrackedValue<Boolean> addStationLandmarks = this.value(true);

    @Comment("Whether depot landmarks should be added to the map.")
    public final TrackedValue<Boolean> addDepotLandmarks = this.value(false);

    @Comment("Configuration related to filtering out landmarks")
    public final Filter filter = new Filter();

    public static class Filter extends Section {
        @Comment("Whether empty stations (i.e. with no routes) should be added to the map.")
        public final TrackedValue<Boolean> showStationWithNoRoute = this.value(false);

        @Comment("Whether hidden routes in MTR should be appended to the station description.")
        public final TrackedValue<Boolean> showHiddenRoute = this.value(false);
    }

    // Static initialization
    public static void init() {
    }
}
