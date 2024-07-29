package com.automaticalechoes.chainimprove;

import com.automaticalechoes.chainimprove.api.ChainBlockItem;
import com.automaticalechoes.chainimprove.api.ChainEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ChainImprove.MODID)
public class ChainImprove
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "chainimprove";
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    public static final RegistryObject<EntityType<ChainEntity>> CHAIN_ENTITY = ENTITY_TYPE_DEFERRED_REGISTER.register("chianed", () -> EntityType.Builder.of(ChainEntity::new, MobCategory.MISC).build("chain_entity"));
    public ChainImprove()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::OnRegistry);
        ENTITY_TYPE_DEFERRED_REGISTER.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void OnRegistry(RegisterEvent event){
        if(event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)){
            ResourceLocation key = ForgeRegistries.ITEMS.getKey(Items.CHAIN);
            ChainBlockItem.CHAIN_OVERRIDE.builtInRegistryHolder().bindValue(ChainBlockItem.CHAIN_OVERRIDE);
            ForgeRegistries.ITEMS.register(key, ChainBlockItem.CHAIN_OVERRIDE);
        }
    }


}
