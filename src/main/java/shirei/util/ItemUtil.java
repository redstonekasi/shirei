package shirei.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public class ItemUtil {

    private static MinecraftClient mc = MinecraftClient.getInstance();

    public static void giveItem(ItemStack item) {
        // Set item in hand if empty or find next empty slot
        if(mc.player.getMainHandStack().isEmpty())
            setItemInHand(item);
        else {
            int nextEmptySlot = mc.player.inventory.getEmptySlot();

            if(nextEmptySlot < 9)
                mc.interactionManager.clickCreativeStack(item, 36 + nextEmptySlot);
            else
                setItemInHand(item);
        }
    }

    public static void setItemInHand(ItemStack item) {
        mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
    }

}