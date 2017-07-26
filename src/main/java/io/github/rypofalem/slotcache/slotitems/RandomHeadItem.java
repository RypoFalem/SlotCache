package io.github.rypofalem.slotcache.slotitems;

import com.winthier.rph.RPHAPI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

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
}