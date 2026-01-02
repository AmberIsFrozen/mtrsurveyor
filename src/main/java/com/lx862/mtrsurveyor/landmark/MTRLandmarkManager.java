package com.lx862.mtrsurveyor.landmark;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.lx862.mtrsurveyor.*;
import com.lx862.mtrsurveyor.config.MTRSurveyorConfig;
import com.lx862.mtrsurveyor.util.MTRUtil;
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

    public static void syncLandmarks(SyncOrigin syncOrigin, World world, MTRDataSummary dataSummary, MTRSurveyorConfig config) {
        WorldLandmarks landmarks = WorldLandmarks.of(world);
        if(landmarks == null) return;
        long startMs = System.currentTimeMillis();

        if(config.debugLog.value()) {
            MTRSurveyor.LOGGER.info("[{}] Syncing {} landmarks for world {} ({})", MTRSurveyor.MOD_NAME, syncOrigin.sourceName(), world.getRegistryKey().getValue(), syncOrigin.reason());
        }
        Long2ObjectOpenHashMap<AreaBase<?, ?>> mtrAreas = new Long2ObjectOpenHashMap<>();

        Table<UUID, Identifier, Landmark> changed = landmarks.removeAllForBatch(Tables.synchronizedTable(HashBasedTable.create()), landmark -> landmark.id().getNamespace().equals(MTRSurveyor.MOD_ID));

        if(config.enabled.value()) {
            if(config.visibility.showStationLandmarks.value()) {
                for(AreaBase<?, ?> area : new ArrayList<>(dataSummary.getData().stations)) {
                    mtrAreas.put(area.getId(), area);
                }
            }

            if(config.visibility.showDepotLandmarks.value()) {
                for(AreaBase<?, ?> area : new ArrayList<>(dataSummary.getData().depots)) {
                    mtrAreas.put(area.getId(), area);
                }
            }

            for(AreaBase<?, ?> area : mtrAreas.values()) {
                if(shouldBeFilteredOut(area, dataSummary, config.visibility.showEmptyStation.value())) continue;

                Landmark landmark = createLandmark(area, dataSummary);
                landmarks.putForBatch(changed, landmark);
            }
        }

        landmarks.handleChanged(changed, world.isClient(), null);

        if(config.debugLog.value()) {
            MTRSurveyor.LOGGER.info("[{}] Took {}ms to sync.", MTRSurveyor.MOD_NAME, (System.currentTimeMillis() - startMs));
        }
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

        return Landmark.create(WorldLandmarks.GLOBAL, MTRSurveyor.id(MTRUtil.getTransportModeName(station.getTransportMode()) + "_station/" + station.getHexId().toLowerCase()), builder -> {
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
        return Landmark.create(WorldLandmarks.GLOBAL, MTRSurveyor.id(MTRUtil.getTransportModeName(depot.getTransportMode()) + "_depot/" + depot.getHexId().toLowerCase()), builder -> {
            fillLandmarkComponent(depot, builder);
            builder.add(LandmarkComponentTypes.NAME, Text.literal(IGui.formatStationName(depot.getName())));
            return builder;
        });
    }

    private static void fillLandmarkComponent(AreaBase<?, ?> areaBase, LandmarkComponentMap.Builder builder) {
        builder.add(LandmarkComponentTypes.COLOR, areaBase.getColor());
        builder.add(LandmarkComponentTypes.POS, new BlockPos((int)areaBase.getCenter().getX(), (int)areaBase.getCenter().getY(), (int)areaBase.getCenter().getZ()));
        builder.add(LandmarkComponentTypes.BOX, new BlockBox((int)areaBase.getMinX(), -64, (int)areaBase.getMinZ(), (int)areaBase.getMaxX(), 255, (int)areaBase.getMaxZ()));
        builder.add(LandmarkComponentTypes.STACK, MTRUtil.getItemStackForTransportMode(areaBase.getTransportMode(), areaBase instanceof Depot));
    }

    private static boolean shouldBeFilteredOut(AreaBase<?, ?> areaBase, MTRDataSummary dataSummary, boolean showEmptyStation) {
        if(areaBase instanceof Station station) {
            List<MTRDataSummary.BasicRouteInfo> routes = dataSummary.getRoutesInStation(station);
            return !showEmptyStation && (routes == null || routes.isEmpty());
        }

        return false;
    }

    public record SyncOrigin(String sourceName, String reason) {
        public static SyncOrigin ofServer(String reason) {
            return new SyncOrigin("server", reason);
        }

        public static SyncOrigin ofClient(String reason) {
            return new SyncOrigin("client", reason);
        }
    }
}
