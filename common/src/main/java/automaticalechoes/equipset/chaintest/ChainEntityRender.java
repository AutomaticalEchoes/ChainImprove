package automaticalechoes.equipset.chaintest;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ChainEntityRender extends EntityRenderer<ChainEntity> {
    public ChainEntityRender(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ChainEntity entity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        super.render(entity, f, g, poseStack, multiBufferSource, i);
    }

    @Override
    public ResourceLocation getTextureLocation(ChainEntity entity) {
        return null;
    }
}
