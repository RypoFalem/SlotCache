package io.github.rypofalem.slotcache.slotitems;

import io.github.rypofalem.slotcache.SlotCachePlugin;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public abstract class SlotItem {

    public static ItemStack getRandomAmount(SlotItem slotItem){
        int min = slotItem.getMin();
        int max = slotItem.getMax();
        int amount;
        if(max - min < 1){
            amount = Math.max(1, max);
        }else{
            amount = min + SlotCachePlugin.getRandom().nextInt(max - min);
            amount +=    min + SlotCachePlugin.getRandom().nextInt(max - min);
            amount /= 2;
        }

        ItemStack stack = slotItem.getItem().clone();
        stack.setAmount(amount);
        return stack;
    }

    public static SlotItem of(Map itemMap){
        try {
                ItemType type = ItemType.valueOf((String)itemMap.get("type"));
                switch(type){
                    case BASIC: return new BasicItem(
                            ((ItemStack) itemMap.get("item")).clone(),
                            (Integer) itemMap.get("weight"),
                            (Integer) itemMap.get("min"),
                            (Integer) itemMap.get("max"));
                    case CUSTOM: return new CustomSlotItem(
                            (String) itemMap.get("item"),
                            (Integer) itemMap.get("weight"),
                            (Integer) itemMap.get("min"),
                            (Integer) itemMap.get("max"));
                    case RANDOMHEAD: return new RandomHeadItem(
                            (Integer) itemMap.get("weight"),
                            (Integer) itemMap.get("min"),
                            (Integer) itemMap.get("max"));
                    default: throw new IllegalArgumentException("Invalid type");
                }
        } catch (Exception e){
            System.out.print("Error loading config. We will skip this item:\n\n" + itemMap.toString() + "\n");
            e.printStackTrace();
            return null;
        }
    }

    public abstract ItemStack getItem();
    public abstract int getMin();
    public abstract int getMax();
    public abstract int getWeight();
}
