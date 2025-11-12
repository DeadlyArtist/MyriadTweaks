package deadly.myriadtweaks.light;

import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.entity.EntityLightSourceManager;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import top.theillusivec4.curios.api.CuriosApi;

public class ModLightsEntry implements DynamicLightsInitializer {
    @Override
    public void onInitializeDynamicLights(ItemLightSourceManager itemLightSourceManager) {
        DynamicLightHandlers.registerDynamicLightHandler(EntityType.PLAYER, entity -> getLuminance(itemLightSourceManager, entity));
    }

    public int getLuminance(ItemLightSourceManager itemLightSourceManager, Player player) {
        var mc = CuriosApi.getCuriosInventory(player);
        if (!mc.isPresent()) return 0;
        var curios = mc.get();

        final int[] brightestLight = {0};
        curios.findCurios(stack -> {
            var light = itemLightSourceManager.getLuminance(stack);
            if (light > brightestLight[0]) brightestLight[0] = light;
            return false;
        });
        return brightestLight[0];
    }
}
