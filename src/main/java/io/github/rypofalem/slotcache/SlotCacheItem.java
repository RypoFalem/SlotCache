package io.github.rypofalem.slotcache;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.item.CustomItem;
import com.winthier.custom.item.ItemContext;
import com.winthier.custom.item.ItemDescription;
import com.winthier.custom.item.UncraftableItem;
import io.github.rypofalem.slotcache.slotitems.RandomHeadItem;
import io.github.rypofalem.slotcache.slotitems.SlotItem;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        ConfigurationSection cacheConfig = SlotCachePlugin.instance().getConfig().getConfigurationSection("caches."+id);
        load();
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
            SlotItem slotItem = SlotItem.of(itemMap);
            if(slotItem == null) continue;
            slotItems.add(slotItem);
            totalWeight += slotItem.getWeight();
        }
    }

    public RandomSlotItemProvider createRandomSlotItemProvider(){
        return new RandomSlotItemProvider(this);
    }


    public List<ItemStack> getItemsWithProbability(){
        List<ItemStack> itemList = new ArrayList<>();
        for(SlotItem slotItem : slotItems){
            ItemStack item = slotItem.getItem().clone();
            List<String> lore;
            if(item.hasItemMeta() && item.getItemMeta().hasLore()){
                lore = item.getItemMeta().getLore();
            }else{
                lore = new ArrayList<>();
            }
            lore.add(String.format("%sProbability: %.2f%s", ChatColor.GREEN.toString(), slotItem.getWeight() * 100.0 / totalWeight, "%"));
            lore.add(String.format("%sAmount: %s", ChatColor.GREEN.toString(), slotItem.getMin() == slotItem.getMax() ?
                    slotItem.getMax() + "" : String.format("%d to %d", slotItem.getMin(), slotItem.getMax())));
            ItemMeta meta = item.getItemMeta();
            if(slotItem instanceof RandomHeadItem) meta.setDisplayName("Random Player Head");
            meta.setLore(lore);
            item.setItemMeta(meta);
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
