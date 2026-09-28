package com.yourname.maidfox.guillotine;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;

/**
 * In-game tuning for the guillotine seat. All changes apply to an already bound maid immediately.
 * Offsets and rotations are machine-relative (x = along FACING, y = up, z = lateral =
 * {@code facing.getClockWise()}), so the same numbers give the same pose for every facing.
 * <pre>
 * /guillotinedebug pos &lt;x&gt; &lt;y&gt; &lt;z&gt;   machine-local position offsets, blocks (x=along FACING, y=up, z=lateral)
 * /guillotinedebug rotx &lt;delta&gt;        add pitch degrees   (about FACING, nose up/down)
 * /guillotinedebug roty &lt;delta&gt;        add yaw degrees     (about up, turn left/right)
 * /guillotinedebug rotz &lt;delta&gt;        add roll degrees    (about the lateral axis, shoulder over shoulder)
 * /guillotinedebug rot &lt;x&gt; &lt;y&gt; &lt;z&gt;     set all three rotations at once
 * /guillotinedebug show | reset
 * </pre>
 * The delta commands take an optional value that defaults to 5 degrees, so the command can simply be
 * repeated to nudge the pose.
 */
public final class GuillotineDebugCommand {
    private GuillotineDebugCommand() { }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("guillotinedebug")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("pos")
                        .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                                .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                                .executes(ctx -> {
                                                    GuillotineDebug.setPos(
                                                            DoubleArgumentType.getDouble(ctx, "x"),
                                                            DoubleArgumentType.getDouble(ctx, "y"),
                                                            DoubleArgumentType.getDouble(ctx, "z"));
                                                    return report(ctx.getSource());
                                                })))))
                .then(axis("x"))
                .then(axis("y"))
                .then(axis("z"))
                .then(bubbleAxis("x"))
                .then(bubbleAxis("y"))
                .then(bubbleAxis("z"))
                .then(Commands.literal("bubble")
                        .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                                .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                                .executes(ctx -> {
                                                    GuillotineDebug.setBubble(
                                                            DoubleArgumentType.getDouble(ctx, "x"),
                                                            DoubleArgumentType.getDouble(ctx, "y"),
                                                            DoubleArgumentType.getDouble(ctx, "z"));
                                                    return report(ctx.getSource());
                                                })))))
                .then(Commands.literal("rot")
                        .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                                .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                                .executes(ctx -> {
                                                    GuillotineDebug.setRot(
                                                            DoubleArgumentType.getDouble(ctx, "x"),
                                                            DoubleArgumentType.getDouble(ctx, "y"),
                                                            DoubleArgumentType.getDouble(ctx, "z"));
                                                    return report(ctx.getSource());
                                                })))))
                .then(Commands.literal("reset").executes(ctx -> {
                    GuillotineDebug.reset();
                    return report(ctx.getSource());
                }))
                .then(Commands.literal("show").executes(ctx -> report(ctx.getSource()))));
    }

    /** {@code rotx|roty|rotz [delta]} - adds degrees around one axis (default 5). */
    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> axis(String name) {
        return Commands.literal("rot" + name)
                .executes(ctx -> apply(ctx.getSource(), name, 5.0))
                .then(Commands.argument("delta", DoubleArgumentType.doubleArg())
                        .executes(ctx -> apply(ctx.getSource(), name, DoubleArgumentType.getDouble(ctx, "delta"))));
    }

    /** {@code bubx|buby|bubz [blocks]} - adds a bubble offset on one machine-local axis (default 0.05). */
    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> bubbleAxis(String name) {
        return Commands.literal("bub" + name)
                .executes(ctx -> bubble(ctx.getSource(), name, 0.05))
                .then(Commands.argument("blocks", DoubleArgumentType.doubleArg())
                        .executes(ctx -> bubble(ctx.getSource(), name, DoubleArgumentType.getDouble(ctx, "blocks"))));
    }

    private static int bubble(CommandSourceStack source, String axis, double blocks) {
        GuillotineDebug.addBubble(axis, blocks);
        return report(source);
    }

    private static int apply(CommandSourceStack source, String axis, double degrees) {
        GuillotineDebug.addRot(axis, degrees);
        return report(source);
    }

    private static int report(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal(GuillotineDebug.describe()), false);
        return 1;
    }
}
