package io.github.rypofalem.slotcache;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.ItemStack;

@Data
@NoArgsConstructor
public class SlotItem {
    ItemStack item;
    int weight;
    int min;
    int max;

    public static ItemStack getRandomAmount(SlotItem slotItem){
        int min = slotItem.min;
        int max = slotItem.max;
        int amount;
        if(max - min < 1){
            amount = max;
        }else{
            amount = min + SlotCachePlugin.random.nextInt(max - min);
            amount +=    min + SlotCachePlugin.random.nextInt(max - min);
            amount /= 2;
        }

        ItemStack stack = slotItem.getItem().clone();
        stack.setAmount(amount);
        return stack;
    }
}
