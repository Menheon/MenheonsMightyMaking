package net.menheon.mightymaking.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import net.menheon.mightymaking.entity.custom.EnderGhoulEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.menheon.mightymaking.MightyMaking;
import com.mojang.blaze3d.vertex.PoseStack;

public class EnderGhoulRenderer extends GeoEntityRenderer<EnderGhoulEntity> {

  public EnderGhoulRenderer(EntityRendererProvider.Context renderManager) {
    super(renderManager, new EnderGhoulModel());
    addRenderLayer(new AutoGlowingGeoLayer<>(this));
  }

  @Override
  public ResourceLocation getTextureLocation(EnderGhoulEntity animatable) {
    return new ResourceLocation(MightyMaking.MOD_ID, "textures/entity/ender_ghoul.png");
  }

  @Override
  public void render(EnderGhoulEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
      MultiBufferSource bufferSource, int packedLight) {
    super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
  }
}
