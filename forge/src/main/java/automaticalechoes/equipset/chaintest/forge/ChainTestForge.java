package automaticalechoes.equipset.chaintest.forge;

import dev.architectury.platform.forge.EventBuses;
import automaticalechoes.equipset.chaintest.ChainTest;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ChainTest.MOD_ID)
public class ChainTestForge {
    public ChainTestForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(ChainTest.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        ChainTest.init();
    }
}