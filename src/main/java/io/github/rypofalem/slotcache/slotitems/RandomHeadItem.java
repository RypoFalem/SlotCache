package io.github.rypofalem.slotcache.slotitems;

import com.winthier.rph.RPHAPI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public class RandomHeadItem extends SlotItem{
    int weight;
    int min;
    int max;

    @Override
    public ItemStack getItem() {
        return RPHAPI.getRandomPlayerHead();
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", ItemType.RANDOMHEAD.toString());
        map.put("weight", weight);
        map.put("min", min);
        map.put("max", max);
        return map;
    }
}