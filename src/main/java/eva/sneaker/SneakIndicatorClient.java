package eva.sneaker;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;


public class SneakIndicatorClient implements ClientModInitializer {

    public static boolean sprindicator = false;
    @Override
    public void onInitializeClient() {
        sprindicator = FabricLoader.getInstance().isModLoaded("sprintindicator");
    }
}