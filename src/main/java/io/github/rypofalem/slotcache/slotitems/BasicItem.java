package io.github.rypofalem.slotcache.slotitems;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@AllArgsConstructor
class BasicItem extends SlotItem {
    ItemStack item;
    int weight;
    int min;
    int max;
}
