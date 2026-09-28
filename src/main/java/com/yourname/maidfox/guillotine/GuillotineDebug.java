package com.yourname.maidfox.guillotine;

/**
 * Live tuning values for the guillotine seat, driven by the /guillotinedebug command so the pose can
 * be adjusted in game instead of by editing constants.
 * <p>
 * Offsets and rotations are machine-relative, so the same numbers give the same pose for every FACING:
 * x = along FACING (the machine's back-to-front axis), y = world-up, z = lateral
 * ({@code facing.getClockWise()}, the machine's left-to-right axis). Rotations are degrees applied to
 * the whole maid in the seat frame: x = pitch about the FACING axis, y = yaw about up (already carried
 * by the maid yaw), z = roll about the lateral axis.
 */
public final class GuillotineDebug {
    // Baseline tuned in game on 2026-09-28 with /guillotinedebug and then baked in as defaults.
    // Second calibration on 2026-09-28: pos y lowered from 0.650 to 0.550 (in-game pos y = -0.100 on the old baseline).
    // The debug offsets below are added on top of these, so tuning keeps working from a zero start.
    public static final double DEFAULT_POS_X = 0.350;
    public static final double DEFAULT_POS_Y = 0.550;
    public static final double DEFAULT_POS_Z = 0.000;
    public static final double DEFAULT_ROT_X = 0.000;
    public static final double DEFAULT_ROT_Y = 0.000;
    public static final double DEFAULT_ROT_Z = 90.000;
    public static final double DEFAULT_BUBBLE_X = -1.200;
    public static final double DEFAULT_BUBBLE_Y = -1.200;
    public static final double DEFAULT_BUBBLE_Z = 0.000;

    private static double posX;
    private static double posY;
    private static double posZ;
    private static double rotX;
    private static double rotY;
    private static double rotZ;
    private static double bubX;
    private static double bubY;
    private static double bubZ;

    private GuillotineDebug() { }

    public static double posX() { return posX; }
    public static double posY() { return posY; }
    public static double posZ() { return posZ; }
    public static double rotX() { return rotX; }
    public static double rotY() { return rotY; }
    public static double rotZ() { return rotZ; }
    public static double bubX() { return bubX; }
    public static double bubY() { return bubY; }
    public static double bubZ() { return bubZ; }

    public static void setPos(double x, double y, double z) { posX = x; posY = y; posZ = z; }
    public static void setRot(double x, double y, double z) { rotX = x; rotY = y; rotZ = z; }
    public static void addRot(String axis, double degrees) {
        if ("x".equals(axis)) rotX += degrees;
        else if ("y".equals(axis)) rotY += degrees;
        else rotZ += degrees;
    }
    public static void setBubble(double x, double y, double z) { bubX = x; bubY = y; bubZ = z; }
    public static void addBubble(String axis, double blocks) {
        if ("x".equals(axis)) bubX += blocks;
        else if ("y".equals(axis)) bubY += blocks;
        else bubZ += blocks;
    }
    public static void reset() {
        posX = posY = posZ = rotX = rotY = rotZ = 0.0;
        bubX = bubY = bubZ = 0.0;
    }

    /** True when anything differs from the stock placement. */
    public static boolean isActive() {
        return posX != 0.0 || posY != 0.0 || posZ != 0.0
                || rotX != 0.0 || rotY != 0.0 || rotZ != 0.0
                || bubX != 0.0 || bubY != 0.0 || bubZ != 0.0;
    }

    /** One line summary, ready to be copied back into the source as constants. */
    public static String describe() {
        return String.format("guillotine debug (x=along FACING, y=up, z=lateral): "
                        + "pos(%.3f, %.3f, %.3f) rot(%.3f, %.3f, %.3f) bubble(%.3f, %.3f, %.3f) blocks/degrees",
                posX, posY, posZ, rotX, rotY, rotZ, bubX, bubY, bubZ);
    }
}
