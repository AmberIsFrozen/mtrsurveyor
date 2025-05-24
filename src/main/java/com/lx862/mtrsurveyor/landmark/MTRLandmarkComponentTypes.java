package com.lx862.mtrsurveyor.landmark;

import com.lx862.mtrsurveyor.MTRSurveyor;
import com.mojang.serialization.Codec;
import folk.sisby.surveyor.landmark.component.LandmarkComponentType;
import folk.sisby.surveyor.landmark.component.LandmarkComponentTypes;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MTRLandmarkComponentTypes {
    public static final LandmarkComponentType<Long> FARE_ZONE_1 = LandmarkComponentTypes.register(MTRSurveyor.id("fare_zone_1"), Codec.LONG, (fareZone) -> {
        return Text.literal(String.valueOf(fareZone)).formatted(Formatting.YELLOW);
    });

    public static final LandmarkComponentType<Long> FARE_ZONE_2 = LandmarkComponentTypes.register(MTRSurveyor.id("fare_zone_2"), Codec.LONG, (fareZone) -> {
        return Text.literal(String.valueOf(fareZone)).formatted(Formatting.YELLOW);
    });

    public static final LandmarkComponentType<Long> FARE_ZONE_3 = LandmarkComponentTypes.register(MTRSurveyor.id("fare_zone_3"), Codec.LONG, (fareZone) -> {
        return Text.literal(String.valueOf(fareZone)).formatted(Formatting.YELLOW);
    });

    public static final LandmarkComponentType<String> TRANSPORT_TYPE = LandmarkComponentTypes.register(MTRSurveyor.id("transport_mode"), Codec.STRING, (transportMode) -> {
        return Text.literal(transportMode).formatted(Formatting.YELLOW);
    });

    public static void init() {
        // static initialization
    }
}
