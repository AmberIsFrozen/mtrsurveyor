package com.lx862.mtrsurveyor;

import com.lx862.mtrsurveyor.landmark.LandmarkManager;
import com.lx862.mtrsurveyor.mixin.MTRAccessorMixin;
import com.lx862.mtrsurveyor.mixin.MainAccessorMixin;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.mtr.core.Main;
import org.mtr.core.simulation.Simulator;

public class Commands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> rootNode = CommandManager.literal("mtrsurveyor");
        rootNode.requires(ctx -> ctx.hasPermissionLevel(4));

        LiteralArgumentBuilder<ServerCommandSource> clearNode = CommandManager.literal("clear");
        clearNode.then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
            .executes(ctx -> {
                    World world = DimensionArgumentType.getDimensionArgument(ctx, "dimension");
                    LandmarkManager.clearLandmarks(world);
                    ctx.getSource().sendFeedback(() -> Text.literal("Cleared all MTR Surveyor landmarks!").formatted(Formatting.GREEN), true);
                    return 1;
                }
            )
        );

        LiteralArgumentBuilder<ServerCommandSource> syncNode = CommandManager.literal("sync");
        syncNode.then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
            .executes(ctx -> {
                    World world = DimensionArgumentType.getDimensionArgument(ctx, "dimension");
                    Identifier worldId = world.getRegistryKey().getValue();
                    Main main = MTRAccessorMixin.getMain();
                    for(Simulator simulator : ((MainAccessorMixin)main).getSimulators()) {
                        boolean isOurWorld = simulator.dimension.equals(worldId.toString().replace(":", "/"));
                        if(isOurWorld) {
                            MTRDataSummary mtrDataSummary = new MTRDataSummary(simulator);
                            LandmarkManager.syncLandmarks(world, mtrDataSummary);
                            ctx.getSource().sendFeedback(() -> Text.literal("Synced MTR landmarks for world " + worldId + "!").formatted(Formatting.GREEN), true);
                        }
                    }
                    return 1;
                }
            )
        );

        LiteralArgumentBuilder<ServerCommandSource> reloadNode = CommandManager.literal("reload");
        reloadNode.executes(ctx -> {
           Config.load();
           ctx.getSource().sendFeedback(() -> Text.literal("Config reloaded!"), false);
           return 1;
        });

        rootNode.then(clearNode);
        rootNode.then(syncNode);
        rootNode.then(reloadNode);
        dispatcher.register(rootNode);
    }
}
