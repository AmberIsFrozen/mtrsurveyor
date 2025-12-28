package com.lx862.mtrsurveyor.util;

import net.minecraft.item.ItemStack;
import org.mtr.core.data.TransportMode;
import org.mtr.mod.Items;

import java.util.Locale;

public class MTRUtil {
    public static String getTransportModeName(TransportMode transportMode) {
        String str = transportMode.toString();
        return str.toLowerCase(Locale.ROOT);
    }

    public static ItemStack getItemStackForTransportMode(TransportMode transportMode, boolean isDepot) {
        final ItemStack stack;
        switch(transportMode) {
            case TRAIN -> {
                stack = new ItemStack(Items.RAILWAY_DASHBOARD.get().data);
            }
            case BOAT -> {
                stack = new ItemStack(Items.BOAT_DASHBOARD.get().data);
            }
            case CABLE_CAR -> {
                stack = new ItemStack(Items.CABLE_CAR_DASHBOARD.get().data);
            }
            case AIRPLANE -> {
                stack = new ItemStack(Items.AIRPLANE_DASHBOARD.get().data);
            }
            default -> throw new IllegalArgumentException("Unknown transport mode " + transportMode);
        }
        stack.getOrCreateNbt().putInt("CustomModelData", isDepot ? 101 : 100);
        return stack;
    }
}
