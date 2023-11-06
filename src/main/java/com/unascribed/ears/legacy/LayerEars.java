package com.unascribed.ears.legacy;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.util.Objects;

import btw.community.ears.mod.EarsMod;
import btw.community.ears.mod.mixin.RenderPlayerAccessor;
import btw.community.ears.mod.mojapi.ProfileUtils;
import btw.community.ears.mod.mojapi.UserProfile;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.main.EarsCommon;
import com.unascribed.ears.main.render.EarsRenderDelegate;
import com.unascribed.ears.main.util.Decider;
import com.unascribed.ears.normal.EarsFeaturesStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemArmor;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModelBiped;
import net.minecraft.src.ModelBox;
import net.minecraft.src.ModelRenderer;
import net.minecraft.src.OpenGlHelper;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.RenderPlayer;
import net.minecraft.src.Tessellator;
import org.lwjgl.opengl.GL11;


public class LayerEars {
    private RenderPlayer render;
    private float tickDelta;

    public void doRenderLayer(RenderPlayer render, EntityPlayer entity, float limbDistance, float partialTicks) {
        this.render = render;
        this.tickDelta = partialTicks;
        delegate.render(entity);
    }

    public void renderRightArm(RenderPlayer render, EntityPlayer entity) {
        this.render = render;
        this.tickDelta = 0;
        delegate.render(entity, EarsRenderDelegate.BodyPart.RIGHT_ARM);
    }

    private final UnmanagedEarsRenderDelegate<EntityPlayer, ModelRenderer> delegate = new UnmanagedEarsRenderDelegate<EntityPlayer, ModelRenderer>() {

        @Override
        protected boolean isVisible(ModelRenderer modelPart) {
            return modelPart.showModel;
        }

        @Override
        public boolean isSlim() {
            boolean isSlim = false;
            UserProfile profile = ProfileUtils.getUserProfile(peer.username).orElse(null);
            if (Objects.nonNull(profile)) {
                isSlim = profile.isSlim();
            }
            return isSlim;
        }

        @Override
        protected EarsFeatures getEarsFeatures() {
            if (EarsMod.EARS_SKIN_FEATURES.containsKey(peer.skinUrl)) {
                EarsFeatures feat = EarsMod.EARS_SKIN_FEATURES.get(peer.skinUrl);
                UserProfile profile = ProfileUtils.getUserProfile(peer.username).orElse(null);
                if (Objects.nonNull(profile)) {
                    EarsFeaturesStorage.INSTANCE.put(peer.username, profile.getUuid(), feat);
                    if (!peer.isInvisible()) {
                        return feat;
                    }
                }
            }
            return EarsFeatures.DISABLED;
        }

        @Override
        protected void doBindSkin() {
            RenderEngine engine = Minecraft.getMinecraft().renderEngine;
            int id = engine.getTextureForDownloadableImage(peer.skinUrl, peer.getTexture());
            if (id < 0) return;
            glBindTexture(GL_TEXTURE_2D, id);
            engine.resetBoundTexture();
        }

        @Override
        protected void doAnchorTo(BodyPart part, ModelRenderer modelPart) {
            modelPart.postRender(1/16f);
            ModelBox cuboid = (ModelBox)modelPart.cubeList.get(0);
            glScalef(1/16f, 1/16f, 1/16f);
            glTranslatef(cuboid.posX1, cuboid.posY2, cuboid.posZ1);
        }

        @Override
        protected Decider<BodyPart, ModelRenderer> decideModelPart(Decider<BodyPart, ModelRenderer> d) {
            ModelBiped model = ((RenderPlayerAccessor) render).getModelBipedMain();
            return d.map(BodyPart.HEAD, model.bipedHead)
                    .map(BodyPart.LEFT_ARM, model.bipedLeftArm)
                    .map(BodyPart.LEFT_LEG, model.bipedLeftLeg)
                    .map(BodyPart.RIGHT_ARM, model.bipedRightArm)
                    .map(BodyPart.RIGHT_LEG, model.bipedRightLeg)
                    .map(BodyPart.TORSO, model.bipedBody);
        }

        @Override
        protected void beginQuad() {
            Tessellator.instance.startDrawing(GL_QUADS);
        }

        @Override
        protected void addVertex(float x, float y, int z, float r, float g, float b, float a, float u, float v, float nX, float nY, float nZ) {
            Tessellator.instance.setColorRGBA_F(r, g, b, a);
            Tessellator.instance.setNormal(nX, nY, nZ);
            Tessellator.instance.addVertexWithUV(x, y, z, u, v);
        }

        @Override
        public void setEmissive(boolean emissive) {
            super.setEmissive(emissive);
            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            if (emissive) {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
            } else {
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        }

        @Override
        protected void drawQuad() {
            Tessellator.instance.draw();
        }

        @Override
        protected String getSkinUrl() {
            return peer.skinUrl;
        }

        @Override
        protected int uploadImage(BufferedImage img) {
            return Minecraft.getMinecraft().renderEngine.allocateAndSetupTexture(img);
        }

        @Override
        public float getTime() {
            return peer.ticksExisted+tickDelta;
        }

        @Override
        public boolean isFlying() {
            return peer.capabilities.isFlying;
        }

        @Override
        public boolean isGliding() {
            return false;
        }

        @Override
        public boolean isJacketEnabled() {
            return true;
        }

        @Override
        public boolean isWearingBoots() {
            ItemStack feet = peer.inventory.armorItemInSlot(0);
            return feet != null && feet.getItem() instanceof ItemArmor;
        }

        @Override
        public boolean isWearingChestplate() {
            ItemStack chest = peer.inventory.armorItemInSlot(2);
            return chest != null && chest.getItem() instanceof ItemArmor;
        }

        @Override
        public boolean isWearingElytra() {
            return false;
        }

        @Override
        public boolean needsSecondaryLayersDrawn() {
            return !peer.isInvisible();
        }

        @Override
        public float getHorizontalSpeed() {
            return EarsCommon.lerpDelta(peer.prevDistanceWalkedModified, peer.distanceWalkedModified, tickDelta);
        }

        @Override
        public float getLimbSwing() {
            return EarsCommon.lerpDelta(peer.prevLimbYaw, peer.limbYaw, tickDelta);
        }

        @Override
        public float getStride() {
            return EarsCommon.lerpDelta(peer.prevCameraYaw, peer.cameraYaw, tickDelta);
        }

        @Override
        public float getBodyYaw() {
            return EarsCommon.lerpDelta(peer.prevRenderYawOffset, peer.renderYawOffset, tickDelta);
        }

        @Override
        public double getCapeX() {
            return EarsCommon.lerpDelta(peer.field_71091_bM, peer.field_71094_bP, tickDelta);
        }

        @Override
        public double getCapeY() {
            return EarsCommon.lerpDelta(peer.field_71096_bN, peer.field_71095_bQ, tickDelta);
        }

        @Override
        public double getCapeZ() {
            return EarsCommon.lerpDelta(peer.field_71097_bO, peer.field_71085_bR, tickDelta);
        }

        @Override
        public double getX() {
            return EarsCommon.lerpDelta(peer.prevPosX, peer.posX, tickDelta);
        }

        @Override
        public double getY() {
            return EarsCommon.lerpDelta(peer.prevPosY, peer.posY, tickDelta);
        }

        @Override
        public double getZ() {
            return EarsCommon.lerpDelta(peer.prevPosZ, peer.posZ, tickDelta);
        }
    };
}
