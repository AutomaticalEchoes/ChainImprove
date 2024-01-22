package com.automaticalechoes.chainimprove;

import com.automaticalechoes.chainimprove.api.ChainBlockItem;
import com.automaticalechoes.chainimprove.common.ChainKnotEntity;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// The value here should match an entry in the META-INF/mods.toml file
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(ChainImprove.MODID)
public class ChainImprove
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "chainimprove";
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    public static final RegistryObject<EntityType<ChainKnotEntity>> CHAIN_KNOT_ENTITY = ENTITY_TYPES.register("chain_knot",
            () -> EntityType.Builder.of(ChainKnotEntity::Create, MobCategory.MISC)
                    .sized(0.375F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE).build("chain_knot"));
    public ChainImprove()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ENTITY_TYPES.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void OnRegistry(RegistryEvent.Register event){
        if(event.getRegistry() == ForgeRegistries.ITEMS){
            ResourceLocation key = ForgeRegistries.ITEMS.getKey(Items.CHAIN);
            ChainBlockItem.CHAIN_OVERRIDE.setRegistryName(key);
            ForgeRegistries.ITEMS.register(ChainBlockItem.CHAIN_OVERRIDE);
        }
    }

}
