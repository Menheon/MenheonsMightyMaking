package net.menheon.mightymaking.entity.client;

import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import net.menheon.mightymaking.entity.custom.EnderGhoulEntity;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.constant.DataTickets;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import net.menheon.mightymaking.MightyMaking;
import net.minecraft.util.Mth;

public class EnderGhoulModel extends GeoModel<EnderGhoulEntity> {

  @Override
  public ResourceLocation getModelResource(EnderGhoulEntity animatable) {
    return new ResourceLocation(MightyMaking.MOD_ID, "geo/ender_ghoul.geo.json");
  }

  @Override
  public ResourceLocation getTextureResource(EnderGhoulEntity animatable) {
    return new ResourceLocation(MightyMaking.MOD_ID, "textures/entity/ender_ghoul.png");
  }

  @Override
  public ResourceLocation getAnimationResource(EnderGhoulEntity animatable) {
    return new ResourceLocation(MightyMaking.MOD_ID, "animations/ender_ghoul.animation.json");
  }

  @Override
  public void setCustomAnimations(EnderGhoulEntity animatable, long instanceId,
      AnimationState<EnderGhoulEntity> animationState) {
    CoreGeoBone head = getAnimationProcessor().getBone("head");
    CoreGeoBone jaw = getAnimationProcessor().getBone("jaw");

    if (head != null) {
      EntityModelData entityModelData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
      head.setRotX(entityModelData.headPitch() * Mth.DEG_TO_RAD);
      head.setRotY(entityModelData.netHeadYaw() * Mth.DEG_TO_RAD);
    }

    if (animatable.isAggressive()) {
      jaw.setRotX(-0.50F);
    }
  }
}
