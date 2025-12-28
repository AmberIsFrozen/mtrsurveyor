package com.lx862.mtrsurveyor;

import com.lx862.mtrsurveyor.config.MTRSurveyorConfig;
import com.lx862.mtrsurveyor.landmark.MTRLandmarkManager;
import com.lx862.mtrsurveyor.mixin.MTRAccessorMixin;
import com.lx862.mtrsurveyor.mixin.MainAccessorMixin;
import com.lx862.mtrsurveyor.util.MTRUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.mtr.core.Main;
import org.mtr.core.simulation.Simulator;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Commands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> rootNode = CommandManager.literal("mtrsurveyor");
        rootNode.requires(ctx -> ctx.hasPermissionLevel(4));

        LiteralArgumentBuilder<ServerCommandSource> forceSyncNode = CommandManager.literal("syncLandmarks");
        forceSyncNode
                .executes(ctx -> {
                    MinecraftServer minecraftServer = ctx.getSource().getServer();
                    Main main = MTRAccessorMixin.getMain();
                    for(Simulator simulator : ((MainAccessorMixin)main).getSimulators()) {
                        Identifier dimensionId = MTRUtil.dimensionToId(simulator.dimension);
                        MTRDataSummary mtrDataSummary = new MTRDataSummary(simulator);
                        MTRLandmarkManager.syncLandmarks(minecraftServer.getWorld(RegistryKey.of(RegistryKeys.WORLD, dimensionId)), mtrDataSummary);
                        ctx.getSource().sendFeedback(() -> Text.literal("Synced MTR landmarks for dimension " + dimensionId + "!").formatted(Formatting.GREEN), true);
                    }
                    return 1;
                })
                .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
            .executes(ctx -> {
                    World targetWorld = DimensionArgumentType.getDimensionArgument(ctx, "dimension");
                    Identifier targetWorldId = targetWorld.getRegistryKey().getValue();
                    Main main = MTRAccessorMixin.getMain();
                    for(Simulator simulator : ((MainAccessorMixin)main).getSimulators()) {
                        Identifier dimensionId = MTRUtil.dimensionToId(simulator.dimension);
                        if(targetWorldId.equals(dimensionId)) {
                            MTRDataSummary mtrDataSummary = new MTRDataSummary(simulator);
                            MTRLandmarkManager.syncLandmarks(targetWorld, mtrDataSummary);
                            ctx.getSource().sendFeedback(() -> Text.literal("Synced MTR landmarks for dimension " + targetWorldId + "!").formatted(Formatting.GREEN), true);
                        }
                    }
                    return 1;
                }
            )
        );

        LiteralArgumentBuilder<ServerCommandSource> configNode = CommandManager.literal("config");

        LiteralArgumentBuilder<ServerCommandSource> autoSyncNode = createBoolConfigNode("enabled", "Mod enabled", MTRSurveyorConfig.INSTANCE.enabled::value, MTRSurveyorConfig.INSTANCE.enabled::setValue);
        LiteralArgumentBuilder<ServerCommandSource> showEmptyStationNode = createBoolConfigNode("showEmptyStation", "Show empty station (No route)", MTRSurveyorConfig.INSTANCE.visibility.showEmptyStation::value, MTRSurveyorConfig.INSTANCE.visibility.showEmptyStation::setValue);
        LiteralArgumentBuilder<ServerCommandSource> showHiddenRouteNode = createBoolConfigNode("showHiddenRoute", "Show hidden route", MTRSurveyorConfig.INSTANCE.visibility.showHiddenRoute::value, MTRSurveyorConfig.INSTANCE.visibility.showHiddenRoute::setValue);
        LiteralArgumentBuilder<ServerCommandSource> showStationLandmarksNode = createBoolConfigNode("showStationLandmarks", "Show station landmarks", MTRSurveyorConfig.INSTANCE.visibility.showStationLandmarks::value, MTRSurveyorConfig.INSTANCE.visibility.showStationLandmarks::setValue);
        LiteralArgumentBuilder<ServerCommandSource> showDepotLandmarksNode = createBoolConfigNode("showDepotLandmarks", "Show depot landmarks", MTRSurveyorConfig.INSTANCE.visibility.showDepotLandmarks::value, MTRSurveyorConfig.INSTANCE.visibility.showDepotLandmarks::setValue);

        configNode.then(autoSyncNode);
        configNode.then(showEmptyStationNode);
        configNode.then(showHiddenRouteNode);
        configNode.then(showStationLandmarksNode);
        configNode.then(showDepotLandmarksNode);

        rootNode.then(forceSyncNode);
        rootNode.then(configNode);
        dispatcher.register(rootNode);
    }

    private static LiteralArgumentBuilder<ServerCommandSource> createBoolConfigNode(String configName, String friendlyName, Supplier<Boolean> getValue, Consumer<Boolean> setValue) {
        LiteralArgumentBuilder<ServerCommandSource> cfgNode = CommandManager.literal(configName);
        cfgNode.then(CommandManager.argument("enabled", BoolArgumentType.bool())
                .executes(ctx -> {
                            boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                            setValue.accept(enabled);
                            saveConfig(MTRSurveyorConfig.INSTANCE, ctx, Text.literal(friendlyName + " set to " + enabled).formatted(Formatting.GREEN));
                            return 1;
                        }
                ))
                .executes(ctx -> { // Query only
                    ctx.getSource().sendFeedback(() -> Text.literal(friendlyName + " is currently set to " + getValue.get()).formatted(Formatting.AQUA), false);
                    return 1;
                });
        return cfgNode;
    }

    private static void saveConfig(MTRSurveyorConfig configInstance, CommandContext<ServerCommandSource> ctx, Text successMessage) {
        ctx.getSource().sendFeedback(() -> successMessage, true);
        configInstance.save();
    }
}
