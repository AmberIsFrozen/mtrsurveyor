package com.lx862.mtrsurveyor.wrapper.impl;

import com.lx862.mtrsurveyor.wrapper.MTRRoutePlatform;
import org.mtr.core.data.RoutePlatformData;

public class MTRRoutePlatformDataImpl implements MTRRoutePlatform {
    private final RoutePlatformData instance;

    public MTRRoutePlatformDataImpl(RoutePlatformData data) {
        this.instance = data;
    }

    public long getPlatformId() {
        return instance.getPlatform().getId();
    }
}
