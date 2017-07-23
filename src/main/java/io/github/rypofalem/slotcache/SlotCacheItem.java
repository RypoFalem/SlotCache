package io.github.rypofalem.slotcache;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.inventory.InventoryManager;
import com.winthier.custom.item.CustomItem;
import com.winthier.custom.item.ItemContext;
import com.winthier.custom.item.ItemDescription;
import com.winthier.custom.item.UncraftableItem;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SlotCacheItem implements CustomItem, UncraftableItem{
    @Getter private final String customId;
    @Getter private final String type;
    private final ItemStack template;
    private final ItemDescription description;
    @Getter private final List<SlotItem> slotItems;
    @Getter private int totalWeight = 0;

    public SlotCacheItem(String id){
        customId = "slotcache:" + id;
        type = id;
        template = new ItemStack(Material.ENDER_CHEST);
        slotItems = new ArrayList<>();
        load();
        ConfigurationSection cacheConfig = SlotCachePlugin.instance().getConfig().getConfigurationSection("caches."+id);
        description = new ItemDescription();
        description.setCategory("Loot");
        description.setDisplayName(cacheConfig.getString("displayName", "Slot Cache"));
        description.setDescription(cacheConfig.getString("description", "It's loot! Sweet loot! Beautiful, wonderful, fabulous loot!"));
        description.setUsage("Right click to start the slot machine. When the machine stops, choose 1 of the 3 selected items.");
        description.apply(template);
    }

    @Override
    public ItemStack spawnItemStack(int i) {
        ItemStack item = template.clone();
        item.setAmount(i);
        return item;
    }

    public void load(){
        slotItems.clear();
        List<Map<?,?>> items = SlotCachePlugin.instance().getConfig().getMapList("caches."+type+".items");
        for(Map itemMap: items){
            try {
                SlotItem slotItem = new SlotItem();
                slotItem.item = ((ItemStack) itemMap.get("item")).clone();
                slotItem.weight = (Integer) itemMap.get("weight");
                slotItem.min = (Integer) itemMap.get("min");
                slotItem.max = (Integer) itemMap.get("max");
                slotItems.add(slotItem);
                totalWeight += slotItem.weight;
            } catch (Exception e){
                System.out.print("Error loading config for " + type +"." + itemMap.toString());
                e.printStackTrace();
            }
        }
    }

    public RandomSlotItemProvider createRandomSlotItemProvider(){
        return new RandomSlotItemProvider(this);
    }


    public List<ItemStack> getItemsWithProbability(){
        List<ItemStack> itemList = new ArrayList<>();
        for(SlotItem slotItem : slotItems){
            ItemStack item = slotItem.getItem().clone();
            ItemDescription description = new ItemDescription();
            description.getStats().put("Probability", String.format("%.2f%s", slotItem.weight * 100.0 / totalWeight, "%"));
            description.getStats().put("Amount", slotItem.getMin() == slotItem.getMax() ?
                    slotItem.getMax() + "" : String.format("%d to %d", slotItem.getMin(), slotItem.getMax()));
            description.apply(item);
            itemList.add(item);
        }

        return itemList;
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent e, ItemContext context){
        e.setCancelled(true);
        if(context.getPosition() != ItemContext.Position.HAND) return;
        CustomPlugin.getInstance().getInventoryManager().openInventory(e.getPlayer(), new SlotCacheView(e.getPlayer(), this));
        if(e.getItem().getAmount() > 1){
            e.getItem().setAmount(e.getItem().getAmount() - 1 );

        }else{
            e.getItem().setType(Material.AIR);
        }
        e.getPlayer().getEquipment().setItemInMainHand(e.getItem());

    }
}
