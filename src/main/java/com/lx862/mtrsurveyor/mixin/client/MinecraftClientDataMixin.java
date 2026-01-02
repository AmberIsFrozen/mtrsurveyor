package com.lx862.mtrsurveyor.mixin.client;

import com.lx862.mtrsurveyor.MTRDataSummary;
import com.lx862.mtrsurveyor.config.MTRSurveyorConfig;
import com.lx862.mtrsurveyor.landmark.MTRLandmarkManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import org.mtr.mod.client.MinecraftClientData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftClientData.class, remap = false)
public class MinecraftClientDataMixin {
    @Inject(method = "sync", at = @At("TAIL"))
    public void onSync(CallbackInfo ci) {
        World world = MinecraftClient.getInstance().world;
        if(world == null) return;
        MTRLandmarkManager.SyncOrigin syncOrigin = MTRLandmarkManager.SyncOrigin.ofClient("MTR Data Changed");
        MTRDataSummary mtrDataSummary = MTRDataSummary.of((MinecraftClientData) (Object)this);
        MTRLandmarkManager.syncLandmarks(syncOrigin, world, mtrDataSummary, MTRSurveyorConfig.INSTANCE);
    }
}
