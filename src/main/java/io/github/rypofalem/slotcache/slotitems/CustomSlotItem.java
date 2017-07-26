package io.github.rypofalem.slotcache.slotitems;

import com.winthier.custom.CustomPlugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class CustomSlotItem extends SlotItem {
    String CustomID;
    int weight;
    int min;
    int max;

    @Override
    public ItemStack getItem() {
        return CustomPlugin.getInstance().getItemManager().spawnItemStack(getCustomID(), 1);
    }
}
