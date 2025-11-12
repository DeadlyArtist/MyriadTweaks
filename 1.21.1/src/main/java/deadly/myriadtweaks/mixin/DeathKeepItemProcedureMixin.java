package deadly.myriadtweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import keeperofinventories.init.KeeperofinventoriesModItems;
import keeperofinventories.procedures.DeathKeepItemProcedure;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Predicate;

@Mixin(DeathKeepItemProcedure.class)
public class DeathKeepItemProcedureMixin {

    @Redirect(method = "execute(Lnet/neoforged/bus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;contains(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static boolean redirectContainsItem(Inventory instance, ItemStack list, @Local Player player) {
        var curiosInventory = CuriosApi.getCuriosInventory(player);
        var keeperItem = KeeperofinventoriesModItems.KEEPER_OF_INVENTORIES.get();
        if (curiosInventory.isPresent() && curiosInventory.get().findFirstCurio(keeperItem).isPresent()) {
            return true;
        }
        return instance.hasAnyMatching(stack -> stack.is(keeperItem));
    }

    @WrapOperation(method = "execute(Lnet/neoforged/bus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;clearOrCountMatchingItems(Ljava/util/function/Predicate;ILnet/minecraft/world/Container;)I"))
    private static int redirectRemoveItem(Inventory instance, Predicate<ItemStack> stackPredicate, int maxCount, Container inventory, Operation<Integer> original, @Local(ordinal = 0) Player player) {
        var curiosInventory = CuriosApi.getCuriosInventory(player);
        var keeperItem = KeeperofinventoriesModItems.KEEPER_OF_INVENTORIES.get();
        if (curiosInventory.isPresent()) {
            var first = curiosInventory.get().findFirstCurio(keeperItem);
            if (first.isPresent()) {
                first.get().stack().shrink(1);
                return 1;
            }
        }
        return original.call(instance, stackPredicate, maxCount, inventory);
    }
}
