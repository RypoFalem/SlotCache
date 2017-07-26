package io.github.rypofalem.slotcache.slotitems;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class BasicItem extends SlotItem {
    ItemStack item;
    int weight;
    int min;
    int max;

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", ItemType.BASIC.toString());
        map.put("item", item);
        map.put("weight", weight);
        map.put("min", min);
        map.put("max", max);
        return map;
    }
}
