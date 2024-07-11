package com.automaticalechoes.chainimprove;

import com.automaticalechoes.chainimprove.api.ChainBlockItem;
import net.minecraft.resources.ResourceLocation;
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
    public ChainImprove()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::OnRegistry);
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
