package com.lx862.mtrsurveyor.landmark;

import com.lx862.mtrsurveyor.Config;
import com.lx862.mtrsurveyor.MTRDataSummary;
import com.lx862.mtrsurveyor.MTRSurveyor;
import com.lx862.mtrsurveyor.Util;
import folk.sisby.surveyor.WorldSummary;
import folk.sisby.surveyor.landmark.Landmark;
import folk.sisby.surveyor.landmark.WorldLandmarks;
import folk.sisby.surveyor.landmark.component.LandmarkComponentMap;
import folk.sisby.surveyor.landmark.component.LandmarkComponentTypes;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.mtr.core.data.*;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.mtr.mod.data.IGui;

import java.util.*;

public class MTRLandmarkManager {

    public static void syncLandmarks(World world, MTRDataSummary dataSummary) {
        long startMs = System.currentTimeMillis();
        WorldSummary worldSummary = WorldSummary.of(world);
        WorldLandmarks landmarks = worldSummary.landmarks();
        if(landmarks == null) return;

        MTRSurveyor.LOGGER.info("[{}] Syncing landmarks", MTRSurveyor.MOD_NAME);
        Long2ObjectOpenHashMap<AreaBase<?, ?>> mtrAreas = new Long2ObjectOpenHashMap<>();

        Map<UUID, Map<Identifier, Landmark>> changed = landmarks.removeAllForBatch(new HashMap<>(), landmark -> {
            Identifier landmarkId = landmark.id();
            return landmarkId.getNamespace().equals(MTRSurveyor.MOD_ID);
        });

        if(Config.getInstance().addStationLandmarks) {
            for(AreaBase<?, ?> area : new ArrayList<>(dataSummary.getData().stations)) {
                mtrAreas.put(area.getId(), area);
            }
        }

        if(Config.getInstance().addDepotLandmarks) {
            for(AreaBase<?, ?> area : new ArrayList<>(dataSummary.getData().depots)) {
                mtrAreas.put(area.getId(), area);
            }
        }

        for(AreaBase<?, ?> area : mtrAreas.values()) {
            if(shouldBeFilteredOut(area, dataSummary)) continue;

            Landmark landmark = createLandmark(area, dataSummary);
            landmarks.putForBatch(changed, landmark);
        }

        landmarks.handleChanged(world, changed, world.isClient(), null);
        MTRSurveyor.LOGGER.debug("[{}] Took {}ms to sync.", MTRSurveyor.MOD_NAME, (System.currentTimeMillis() - startMs));
    }

    public static void clearLandmarks(World world) {
        WorldSummary worldSummary = WorldSummary.of(world);
        WorldLandmarks landmarks = worldSummary.landmarks();
        if(landmarks == null) return;

        MTRSurveyor.LOGGER.info("[{}] Clearing landmarks for {}", MTRSurveyor.MOD_NAME, world.getRegistryKey().getValue().toString());

        landmarks.removeAll(world, landmark -> {
            Identifier landmarkId = landmark.id();
            return landmarkId.getNamespace().equals(MTRSurveyor.MOD_ID);
        });
    }

    private static Landmark createLandmark(AreaBase<?, ?> areaBase, MTRDataSummary mtrDataSummary) {
        if(areaBase instanceof Station station) {
            return createStationLandmark(station, mtrDataSummary);
        } else if(areaBase instanceof Depot depot) {
            return createDepotLandmark(depot, mtrDataSummary);
        }
        throw new IllegalArgumentException("Unknown AreaBase.");
    }

    private static Landmark createStationLandmark(Station station, MTRDataSummary mtrDataSummary) {
        List<MTRDataSummary.BasicRouteInfo> routesInStation = mtrDataSummary.getRoutesInStation(station);

        return Landmark.create(WorldLandmarks.GLOBAL, MTRSurveyor.id("stations/" + station.getHexId().toLowerCase()), builder -> {
            fillLandmarkComponent(station, builder);

            List<Text> lores = new ArrayList<>();
            lores.add(Text.literal("Fare zone: " + station.getZone1()));

            if(routesInStation != null && !routesInStation.isEmpty()) {
                lores.add(Text.literal("Route passing: "));
                for(MTRDataSummary.BasicRouteInfo route : routesInStation) {
                    lores.add(Text.literal("- " + IGui.formatStationName(route.name())));
                }
            }

            builder.add(LandmarkComponentTypes.NAME, Text.literal(IGui.formatStationName(station.getName())));
            builder.add(LandmarkComponentTypes.LORE, lores);
            return builder;
        });
    }

    private static Landmark createDepotLandmark(Depot depot, MTRDataSummary mtrDataSummary) {
        return Landmark.create(WorldLandmarks.GLOBAL, MTRSurveyor.id("depots/" + depot.getHexId().toLowerCase()), builder -> {
            fillLandmarkComponent(depot, builder);
            builder.add(LandmarkComponentTypes.NAME, Text.literal(IGui.formatStationName(depot.getName())));
            return builder;
        });
    }

    private static void fillLandmarkComponent(AreaBase<?, ?> areaBase, LandmarkComponentMap.Builder builder) {
        builder.add(LandmarkComponentTypes.COLOR, areaBase.getColor());
        builder.add(LandmarkComponentTypes.POS, new BlockPos((int)areaBase.getCenter().getX(), (int)areaBase.getCenter().getY(), (int)areaBase.getCenter().getZ()));
        builder.add(LandmarkComponentTypes.BOX, new BlockBox((int)areaBase.getMinX(), -64, (int)areaBase.getMinZ(), (int)areaBase.getMaxX(), 255, (int)areaBase.getMaxZ()));
        builder.add(LandmarkComponentTypes.STACK, Util.getItemStackForTransportMode(areaBase.getTransportMode(), areaBase instanceof Depot));
    }

    private static boolean shouldBeFilteredOut(AreaBase<?, ?> areaBase, MTRDataSummary dataSummary) {
        Config config = Config.getInstance();
        if(areaBase instanceof Station station) {
            List<MTRDataSummary.BasicRouteInfo> routes = dataSummary.getRoutesInStation(station);
            return !config.showStationWithNoRoute && (routes == null || routes.isEmpty());
        }

        return false;
    }
}
