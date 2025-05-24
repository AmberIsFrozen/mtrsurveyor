package com.lx862.mtrsurveyor;

import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import org.mtr.core.data.*;

import java.util.ArrayList;
import java.util.List;

public class MTRDataSummary {
    private final Long2ObjectArrayMap<List<BasicRouteInfo>> stationToRoutes = new Long2ObjectArrayMap<>();
    private final Data dataInstance;

    public MTRDataSummary(Data dataInstance) {
        this.dataInstance = dataInstance;
        for(Station station : new ArrayList<>(dataInstance.stations)) {
            if(station.savedRails.isEmpty()) continue;

            List<Route> routePassing = new ArrayList<>();
            for(Route route : new ArrayList<>(dataInstance.routes)) {
                for(RoutePlatformData routePlatformData : route.getRoutePlatforms()) {
                    for(Platform platform : station.savedRails) {
                        if(routePlatformData.getPlatform().getId() == platform.getId()) {
                            routePassing.add(route);
                        }
                    }
                }
            }

            List<BasicRouteInfo> basicRouteInfos = new ArrayList<>();
            for(Route route : routePassing) {
                BasicRouteInfo basicRouteInfo = new BasicRouteInfo(route.getName().split("\\|\\|")[0], route.getColor());
                if(basicRouteInfos.contains(basicRouteInfo) || (!Config.getInstance().showHiddenRoute && route.getHidden())) continue;
                basicRouteInfos.add(basicRouteInfo);
            }

            stationToRoutes.put(station.getId(), basicRouteInfos);
        }
    }

    public Data getData() {
        return this.dataInstance;
    }

    public List<BasicRouteInfo> getRoutesInStation(Station station) {
        return stationToRoutes.get(station.getId());
    }

    public record BasicRouteInfo(String name, int color) {
    }
}
