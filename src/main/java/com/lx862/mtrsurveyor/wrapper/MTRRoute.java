package com.lx862.mtrsurveyor.wrapper;

import java.util.List;

public interface MTRRoute {
    String getName();

    int getColor();

    boolean isHidden();

    List<MTRRoutePlatform> getRoutePlatforms();
}
