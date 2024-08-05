package automaticalechoes.equipset.chaintest;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.logging.Logger;

public class ChainTest
{
	public static final Logger log = Logger.getLogger("ITEST");
	public static final String MOD_ID = "chaintest";
	public static final EntityType<ChainEntity> CHAIN_ENTITY = EntityType.Builder.of(ChainEntity::new, MobCategory.MISC)
			.sized(1.0f, 1.0f)
			.build("chain_entity");
	public static void init() {
		Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(MOD_ID, "chain_entity"), CHAIN_ENTITY);
	}
}
