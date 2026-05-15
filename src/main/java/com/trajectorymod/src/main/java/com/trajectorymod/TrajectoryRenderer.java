package com.trajectorymod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemBow;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;
import java.util.ArrayList;
import java.util.List;

public class TrajectoryRenderer {

    private static final int STEPS = 100;
    private static final double GRAVITY = 0.03;
    private static final double DRAG = 0.99;

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return;

        ItemStack held = mc.thePlayer.getHeldItem();
        if (held == null) return;

        float[] color = getColor(held);
        if (color == null) return;

        double speed = getSpeed(held);
        double[] dir = getLookDirection(mc.thePlayer, speed);

        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - 0.1;
        double z = mc.thePlayer.posZ;
        double vx = dir[0], vy = dir[1], vz = dir[2];

        List<double[]> points = new ArrayList<>();
        points.add(new double[]{x, y, z});

        for (int i = 0; i < STEPS; i++) {
            vx *= DRAG; vy *= DRAG; vz *= DRAG;
            vy -= GRAVITY;
            x += vx; y += vy; z += vz;
            points.add(new double[]{x, y, z});
            if (mc.theWorld.getBlockState(new net.minecraft.util.BlockPos(x, y, z))
                .getBlock() != net.minecraft.init.Blocks.air) break;
        }

        Entity cam = mc.getRenderViewEntity();
        double camX = cam.lastTickPosX + (cam.posX - cam.lastTickPosX) * event.partialTicks;
        double camY = cam.lastTickPosY + (cam.posY - cam.lastTickPosY) * event.partialTicks;
        double camZ = cam.lastTickPosZ + (cam.posZ - cam.lastTickPosZ) * event.partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-camX, -camY, -camZ);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();
        GL11.glLineWidth(2.0f);

        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i < points.size(); i++) {
            double[] p = points.get(i);
            float alpha = 1.0f - (float) i / points.size();
            GlStateManager.color(color[0], color[1], color[2], alpha);
            GL11.glVertex3d(p[0], p[1], p[2]);
        }
        GL11.glEnd();

        GL11.glLineWidth(1.0f);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.popMatrix();
    }

    private float[] getColor(ItemStack item) {
        if (item.getItem() instanceof ItemBow)        return new float[]{1f, 0.9f, 0.2f};
        if (item.getItem() instanceof ItemSnowball)   return new float[]{0.5f, 0.85f, 1f};
        if (item.getItem() instanceof ItemEgg)        return new float[]{1f, 0.6f, 0.1f};
        if (item.getItem() instanceof ItemEnderPearl) return new float[]{0.4f, 0.1f, 0.9f};
        return null;
    }

    private double getSpeed(ItemStack item) {
        if (item.getItem() instanceof ItemBow)        return 1.5;
        if (item.getItem() instanceof ItemSnowball)   return 0.4;
        if (item.getItem() instanceof ItemEgg)        return 0.4;
        if (item.getItem() instanceof ItemEnderPearl) return 0.4;
        return 0.4;
    }

    private double[] getLookDirection(Entity entity, double speed) {
        float pitch = entity.rotationPitch;
        float yaw = entity.rotationYaw;
        double cp = Math.cos(Math.toRadians(pitch));
        double sp = Math.sin(Math.toRadians(pitch));
        double cy = Math.cos(Math.toRadians(yaw));
        double sy = Math.sin(Math.toRadians(yaw));
        return new double[]{-sy * cp * speed, -sp * speed, cy * cp * speed};
    }
}
