package com.yourname.maidfox.guillotine.client;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.ChatBubbleRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.EntityGraphics;
import com.github.tartaricacid.touhoulittlemaid.client.resource.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MaidConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.yourname.maidfox.guillotine.GuillotineRestraint;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import org.joml.Quaternionf;

import java.lang.reflect.Field;

/** Wraps TLM's shared render entry, so Bedrock, internal Gecko and YSM all see the pose. */
public class GuillotineMaidRenderer extends EntityMaidRenderer {
    private static final double GECKO_FORWARD_RENDER_OFFSET = 0.20;
    private final ChatBubbleRenderer uprightBubbleRenderer;

    public GuillotineMaidRenderer(EntityRendererProvider.Context context) {
        super(context);
        ChatBubbleRenderer redirected = null;
        try {
            Field field = EntityMaidRenderer.class.getDeclaredField("chatBubbleRenderer2");
            field.setAccessible(true);
            ChatBubbleRenderer original = (ChatBubbleRenderer) field.get(this);
            // TLM draws its bubble before the model. Keep the bubble upright when the maid lies down.
            field.set(this, new ChatBubbleRenderer(this) {
                @Override
                public void render(EntityGraphics graphics) {
                    if (!(graphics.getMaid().getVehicle() instanceof GuillotineRestraint)) original.render(graphics);
                }
            });
            redirected = original;
        } catch (ReflectiveOperationException | SecurityException ignored) {
            // If TLM changes the private field, retain its normal bubble renderer.
        }
        uprightBubbleRenderer = redirected;
    }

    @Override
    public void render(Mob mob, float yaw, float partialTick, PoseStack pose,
                       MultiBufferSource buffers, int light) {
        if (mob instanceof EntityMaid maid && mob.getVehicle() instanceof GuillotineRestraint seat) {
            // yBodyRot is a client-side field that is not synced, and a NoAI rider never runs the vanilla
            // body-yaw follow, so the renderer would keep a stale body yaw and the seat rotation would look
            // wrong. Mirror the synced yaw onto the body/head fields before anything is rendered.
            float syncedYaw = maid.getYRot();
            maid.yRotO = syncedYaw;
            maid.yHeadRot = syncedYaw;
            maid.yHeadRotO = syncedYaw;
            maid.yBodyRot = syncedYaw;
            maid.yBodyRotO = syncedYaw;
            Direction facing = seat.getFacing();
            Direction lateral = facing.getClockWise();
            if (uprightBubbleRenderer != null && MaidConfig.GLOBAL_MAID_SHOW_CHAT_BUBBLE.get()
                    && maid.getConfigManager().isChatBubbleShow()) {
                pose.pushPose();
                float offsetY = maid.getNameTagOffsetY();
                if (maid.isMaidInSittingPose()) offsetY -= .25F;
                pose.translate(0, offsetY, 0);
                // Bubble offsets are machine-relative (blocks), tuned in game with /guillotinedebug bub?:
                // X runs along FACING, Z along facing.getClockWise(), Y stays world-up. For FACING = EAST
                // this equals the previous world-axis placement.
                float bubbleForward = seat.getBubbleX();
                float bubbleLateral = seat.getBubbleZ();
                pose.translate(facing.getStepX() * bubbleForward + lateral.getStepX() * bubbleLateral,
                        seat.getBubbleY(),
                        facing.getStepZ() * bubbleForward + lateral.getStepZ() * bubbleLateral);
                pose.mulPose(this.entityRenderDispatcher.cameraOrientation());
                pose.scale(-.025F, -.025F, .025F);
                uprightBubbleRenderer.render(new EntityGraphics(buffers, pose, maid, light, partialTick));
                pose.popPose();
            }
            pose.pushPose();
            // Debug rotations synced from the seat entity: pitch (xRot) and roll (synced data). The yaw
            // needs nothing here because the maid yaw is already pinned to the seat yaw. Both angles are
            // machine-relative, so they are conjugated by the FACING alignment: X turns about the FACING
            // axis, Z about the lateral axis facing.getClockWise(). For FACING = EAST the alignment is the
            // identity, which reproduces the previous world-axis behaviour exactly.
            if (seat.getXRot() != 0.0F || seat.getRoll() != 0.0F) {
                Quaternionf alignment = facingAlignment(facing);
                pose.mulPose(alignment);
                if (seat.getXRot() != 0.0F) pose.mulPose(Axis.XP.rotationDegrees(seat.getXRot()));
                if (seat.getRoll() != 0.0F) pose.mulPose(Axis.ZP.rotationDegrees(seat.getRoll()));
                pose.mulPose(new Quaternionf(alignment).conjugate());
            }
            boolean geckoModel = !maid.isYsmModel() && CustomPackLoader.MAID_MODELS
                    .getInfo(maid.getModelId()).map(MaidModelInfo::isGeckoModel).orElse(false);
            if (geckoModel) {
                Direction towardStock = Direction.fromYRot(seat.getYRot());
                pose.translate(towardStock.getStepX() * GECKO_FORWARD_RENDER_OFFSET, 0,
                        towardStock.getStepZ() * GECKO_FORWARD_RENDER_OFFSET);
            }
            // Baseline is the plain riding pose, i.e. the maid sits upright on the machine. The
            // fork lying-pose chain (translate 0/-.25, Y180, translate 0/.50, X-78, Y180,
            // translate 0/-.50) is intentionally not applied; use the debug rotations to build any
            // pose from this clean baseline. The chain is still in git history if it is needed again.
            try { super.render(mob, yaw, partialTick, pose, buffers, light); }
            finally { pose.popPose(); }
        } else {
            super.render(mob, yaw, partialTick, pose, buffers, light);
        }
    }

    /**
     * Yaw rotation that carries the world +X axis onto {@code facing} and the world +Z axis onto
     * {@code facing.getClockWise()}: the machine's local frame expressed in the world frame. It is the
     * identity for EAST, so conjugating a local-axis rotation with it keeps the EAST result unchanged.
     */
    private static Quaternionf facingAlignment(Direction facing) {
        float yaw = (float) Math.toDegrees(Math.atan2(-facing.getStepZ(), facing.getStepX()));
        return Axis.YP.rotationDegrees(yaw);
    }
}
