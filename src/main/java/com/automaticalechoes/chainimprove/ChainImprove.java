package com.automaticalechoes.chainimprove;

import com.automaticalechoes.chainimprove.api.ChainBlockItem;
import com.automaticalechoes.chainimprove.common.ChainKnotEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ChainImprove.MODID)
public class ChainImprove
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "chainimprove";
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    public static final RegistryObject<EntityType<ChainKnotEntity>> CHAIN_KNOT_ENTITY = ENTITY_TYPES.register("chain_knot",
            () -> EntityType.Builder.of(ChainKnotEntity::Create, MobCategory.MISC)
                    .sized(0.375F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE).build("chain_knot"));
    public ChainImprove()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::RegistryEventL);
        ENTITY_TYPES.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public  void RegistryEventL(RegisterEvent event){
        if(event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)){
            if(event.getForgeRegistry() instanceof ForgeRegistry forgeRegistry){
                ResourceLocation key = forgeRegistry.getKey(Items.CHAIN);
                ChainBlockItem.CHAIN_OVERRIDE.builtInRegistryHolder().bindValue(ChainBlockItem.CHAIN_OVERRIDE);
                forgeRegistry.register(key, ChainBlockItem.CHAIN_OVERRIDE);
            }

        }
    }


}
