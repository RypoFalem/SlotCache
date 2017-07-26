package io.github.rypofalem.slotcache;

import io.github.rypofalem.slotcache.slotitems.SlotItem;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class RandomSlotItemProvider {
    private int totalWeight;
    private Map<SlotItem, Integer> weightedChoices = new HashMap<>();

    public RandomSlotItemProvider(SlotCacheItem cache){
        for(SlotItem item : cache.getSlotItems()){
            weightedChoices.put(item, item.getWeight());
        }
        totalWeight = cache.getTotalWeight();
    }

    public ItemStack getWeightedItem(){
        if(weightedChoices == null) return null;

        int rand = SlotCachePlugin.getRandom().nextInt(totalWeight);
        for(SlotItem key : weightedChoices.keySet()){
            if(weightedChoices.get(key) <= 0) continue;
            rand -= weightedChoices.get(key);
            if(rand < 0){
                return SlotItem.getRandomAmount(key);
            }
        }
        return null;
    }
}
