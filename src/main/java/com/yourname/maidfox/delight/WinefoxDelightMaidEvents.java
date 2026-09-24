package com.yourname.maidfox.delight.compat;

import com.github.tartaricacid.touhoulittlemaid.api.event.InteractMaidEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.yourname.maidfox.delight.WinefoxDelight;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = WinefoxDelight.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WinefoxDelightMaidEvents {
    public static final List<String> FEEDABLE_FOOD_IDS = List.of(
            "cooked_winefox_meat",
            "cooked_cubed_winefox_meat",
            "winefox_meat_patty",
            "winefox_burger",
            "winefox_barbecue_stick",
            "winefox_fried_rice",
            "winefox_stew",
            "winefox_sushi_roll",
            "winefox_sushi",
            "winefox_meat_sauce_pasta",
            "winefox_stew_bowl"
    );
    private static final List<ResourceLocation> FEEDABLE_FOODS = FEEDABLE_FOOD_IDS.stream()
            .map(id -> new ResourceLocation(WinefoxDelight.MODID, id))
            .toList();

    @SubscribeEvent
    public static void onInteractMaid(InteractMaidEvent event) {
        ItemStack stack = event.getStack();
        EntityMaid maid = event.getMaid();
        Level world = event.getWorld();
        Player player = event.getPlayer();

        // Only handle this when the player is sneaking and holding a feedable mod food in the main hand
        if (player.isDiscrete() && isFeedable(stack)) {
            event.setCanceled(true);
            if (world.isClientSide) {
                return;
            }
            FoodProperties food = stack.getItem().getFoodProperties(stack, maid);
            if (food != null) {
                // Every 2 hunger points grant 1 affection point, rounded
                int favorability = Math.round(food.getNutrition() / 2.0f);
                maid.getFavorabilityManager().add(favorability);
            }
            // Play the eating animation and sound
            maid.eat(world, stack);
            // Consume one food item unless in creative mode
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
    }

    private static boolean isFeedable(ItemStack stack) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return key != null && FEEDABLE_FOODS.contains(key);
    }
}

