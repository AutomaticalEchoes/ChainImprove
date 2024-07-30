package automaticalechoes.equipset.chaintest.fabric;

import automaticalechoes.equipset.chaintest.ChainEntityRender;
import automaticalechoes.equipset.chaintest.ChainTest;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ChainTestFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ChainTest.init();
        EntityRendererRegistry.register(ChainTest.CHAIN_ENTITY, ChainEntityRender::new);
    }
}