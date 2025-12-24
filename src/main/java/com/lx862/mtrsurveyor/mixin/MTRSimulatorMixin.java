package com.lx862.mtrsurveyor.mixin;

import com.lx862.mtrsurveyor.MTRDataSummary;
import com.lx862.mtrsurveyor.config.MTRSurveyorConfig;
import com.lx862.mtrsurveyor.landmark.MTRLandmarkManager;
import com.lx862.mtrsurveyor.MTRSurveyor;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.mtr.core.data.Data;
import org.mtr.core.simulation.Simulator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Simulator.class, remap = false)
public class MTRSimulatorMixin extends Data {
    @Shadow @Final public String dimension;

    @Override
    public void sync() {
        super.sync();
        if(MTRSurveyorConfig.INSTANCE.enableAutoSync.value()) {
            // dimension is in format e.g. minecraft/overworld
            String[] dimSplit = dimension.split("/");
            String dimensionNamespace = dimSplit[0];
            String dimensionPath = dimSplit[1];
            Identifier dimensionId = Identifier.of(dimensionNamespace, dimensionPath);
            MinecraftServer server = MTRSurveyor.getServerInstance();
            MTRDataSummary dataSummary = new MTRDataSummary(this);
            server.execute(() -> {
                World world = server.getWorld(RegistryKey.of(RegistryKeys.WORLD, dimensionId));
                MTRLandmarkManager.syncLandmarks(world, dataSummary);
            });
        }
    }
}
