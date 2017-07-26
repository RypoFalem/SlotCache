package io.github.rypofalem.slotcache.slotitems;

import com.winthier.custom.CustomPlugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

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

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", ItemType.CUSTOM.toString());
        map.put("item", getCustomID());
        map.put("weight", weight);
        map.put("min", min);
        map.put("max", max);
        return map;
    }
}
