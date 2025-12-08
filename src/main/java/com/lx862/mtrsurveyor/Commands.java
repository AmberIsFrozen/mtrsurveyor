package com.lx862.mtrsurveyor;

import com.lx862.mtrsurveyor.landmark.MTRLandmarkManager;
import com.lx862.mtrsurveyor.mixin.MTRAccessorMixin;
import com.lx862.mtrsurveyor.mixin.MainAccessorMixin;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.mtr.core.Main;
import org.mtr.core.simulation.Simulator;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Commands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> rootNode = CommandManager.literal("mtrsurveyor");
        rootNode.requires(ctx -> ctx.hasPermissionLevel(4));

        LiteralArgumentBuilder<ServerCommandSource> clearNode = CommandManager.literal("clearLandmarks");
        clearNode.then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
            .executes(ctx -> {
                    World world = DimensionArgumentType.getDimensionArgument(ctx, "dimension");
                    MTRLandmarkManager.clearLandmarks(world);
                    ctx.getSource().sendFeedback(() -> Text.literal("Cleared all MTR Surveyor landmarks!").formatted(Formatting.GREEN), true);

                    if(Config.getInstance().enableAutoSync) {
                        final String disableSyncCommand = "/mtrsurveyor config autoSync false";

                        ctx.getSource().sendFeedback(() -> {
                            Text cmdText = Text.literal("[here]")
                                    .formatted(Formatting.YELLOW)
                                    .formatted(Formatting.UNDERLINE)
                                    .styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(disableSyncCommand))))
                                    .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, disableSyncCommand)));
                            return Text.literal("To prevent landmarks from showing up again, click ").append(cmdText).append(Text.literal(" to disable landmark syncing."));
                        }, false);
                    }

                    return 1;
                }
            )
        );

        LiteralArgumentBuilder<ServerCommandSource> syncNode = CommandManager.literal("syncLandmarks");
        syncNode.then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
            .executes(ctx -> {
                    World world = DimensionArgumentType.getDimensionArgument(ctx, "dimension");
                    Identifier worldId = world.getRegistryKey().getValue();
                    Main main = MTRAccessorMixin.getMain();
                    for(Simulator simulator : ((MainAccessorMixin)main).getSimulators()) {
                        boolean isOurWorld = simulator.dimension.equals(worldId.toString().replace(":", "/"));
                        if(isOurWorld) {
                            MTRDataSummary mtrDataSummary = new MTRDataSummary(simulator);
                            MTRLandmarkManager.syncLandmarks(world, mtrDataSummary);
                            ctx.getSource().sendFeedback(() -> Text.literal("Synced MTR landmarks for world " + worldId + "!").formatted(Formatting.GREEN), true);
                        }
                    }
                    return 1;
                }
            )
        );

        LiteralArgumentBuilder<ServerCommandSource> configNode = CommandManager.literal("config");

        LiteralArgumentBuilder<ServerCommandSource> reloadNode = CommandManager.literal("reload");
        reloadNode.executes(ctx -> {
           Config.load();
           ctx.getSource().sendFeedback(() -> Text.literal("Config reloaded!"), true);
           return 1;
        });



        LiteralArgumentBuilder<ServerCommandSource> autoSyncNode = createBoolConfigNode("autoSync", "Auto-sync landmarks", () -> Config.getInstance().enableAutoSync, (bl) -> Config.getInstance().enableAutoSync = bl);
        LiteralArgumentBuilder<ServerCommandSource> showStationWithNoRouteNode = createBoolConfigNode("showStationWithNoRoute", "Show empty station (No route)", () -> Config.getInstance().showStationWithNoRoute, (bl) -> Config.getInstance().showStationWithNoRoute = bl);
        LiteralArgumentBuilder<ServerCommandSource> showHiddenRouteNode = createBoolConfigNode("showHiddenRoute", "Show hidden route", () -> Config.getInstance().showHiddenRoute, (bl) -> Config.getInstance().showHiddenRoute = bl);
        LiteralArgumentBuilder<ServerCommandSource> addStationLandmarks = createBoolConfigNode("addStationLandmarks", "Show station landmarks", () -> Config.getInstance().addStationLandmarks, (bl) -> Config.getInstance().addStationLandmarks = bl);
        LiteralArgumentBuilder<ServerCommandSource> addDepotLandmarks = createBoolConfigNode("addDepotLandmarks", "Show depot landmarks", () -> Config.getInstance().addDepotLandmarks, (bl) -> Config.getInstance().addDepotLandmarks = bl);

        configNode.then(autoSyncNode);
        configNode.then(showStationWithNoRouteNode);
        configNode.then(showHiddenRouteNode);
        configNode.then(addStationLandmarks);
        configNode.then(addDepotLandmarks);

        configNode.then(reloadNode);

        rootNode.then(clearNode);
        rootNode.then(syncNode);
        rootNode.then(configNode);
        dispatcher.register(rootNode);
    }

    private static LiteralArgumentBuilder<ServerCommandSource> createBoolConfigNode(String configName, String friendlyName, Supplier<Boolean> getValue, Consumer<Boolean> setValue) {
        LiteralArgumentBuilder<ServerCommandSource> cfgNode = CommandManager.literal(configName);
        cfgNode.then(CommandManager.argument("enabled", BoolArgumentType.bool())
                .executes(ctx -> {
                            boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                            Config configInstance = Config.getInstance();
                            setValue.accept(enabled);
                            saveConfig(configInstance, ctx, Text.literal(friendlyName + " set to " + enabled).formatted(Formatting.GREEN));
                            return 1;
                        }
                ))
                .executes(ctx -> { // Query only
                    ctx.getSource().sendFeedback(() -> Text.literal(friendlyName + " is currently set to " + getValue.get()).formatted(Formatting.AQUA), false);
                    return 1;
                });
        return cfgNode;
    }

    private static void saveConfig(Config configInstance, CommandContext<ServerCommandSource> ctx, Text successMessage) {
        try {
            ctx.getSource().sendFeedback(() -> successMessage, true);
            Config.write(configInstance);
        } catch (IOException e) {
            ctx.getSource().sendError(Text.literal("Failed to save config file!").formatted(Formatting.RED));
            MTRSurveyor.LOGGER.error("Failed to save config for " + MTRSurveyor.MOD_ID + "!", e);
        }
    }
}
