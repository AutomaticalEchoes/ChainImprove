package com.automaticalechoes.chainimprove.client;


import com.automaticalechoes.chainimprove.ChainImprove;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@Mod.EventBusSubscriber(modid = ChainImprove.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public  class ClientModEvents {
//    @SubscribeEvent
//    public static void onClientSetup(FMLClientSetupEvent event)
//    {
//        event.enqueueWork(() -> {
//        });
//
//    }

    @SubscribeEvent
    public static void RegisterRenders(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(ChainImprove.CHAIN_KNOT_ENTITY.get(), ChainKnotEntityRenderer::new);
    }



}
