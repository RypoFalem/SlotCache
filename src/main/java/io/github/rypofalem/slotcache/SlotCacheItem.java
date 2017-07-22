package io.github.rypofalem.slotcache;

import com.winthier.custom.item.CustomItem;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SlotCacheItem implements CustomItem{
    @Getter private final String customId;
    @Getter private final String type;
    private final List<SlotObject> slotObjects;

    public SlotCacheItem(String id){
        customId = "slotcache:" + id;
        type = id;
        slotObjects = new ArrayList<>();
    }

    @Override
    public ItemStack spawnItemStack(int i) {
        return null;
    }

    public List<ItemStack> getItems(){
        return null;
    }

    public List<ItemStack> getItemsWithProbability(){
        return null;
    }
}
