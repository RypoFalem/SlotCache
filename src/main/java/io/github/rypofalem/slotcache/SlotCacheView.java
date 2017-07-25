package io.github.rypofalem.slotcache;

import com.winthier.custom.inventory.CustomInventory;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONValue;

import java.util.*;

public class SlotCacheView implements CustomInventory{
    private final UUID player;
    private final Inventory inventory;
    private final SlotCacheItem cache;
    private final RandomSlotItemProvider provider;
    private boolean isShuffling = true;
    private boolean hasPicked = false;
    private final int[] choosableSlots = {12, 13, 14};
    private final ItemStack[] rewards = new ItemStack[3];
    private BukkitRunnable spinTask;

    SlotCacheView(Player player, SlotCacheItem cache){
        this.player = player.getUniqueId();
        this.cache = cache;
        this.provider = cache.createRandomSlotItemProvider();
        inventory = Bukkit.createInventory(player, 54, "Pick ONE item from the center!");
        for(int slot = 3; slot < 6; slot++){
            inventory.setItem(slot, new ItemStack(Material.GLOWSTONE));
            inventory.setItem(slot + 18, new ItemStack(Material.GLOWSTONE));
        }
        inventory.setItem(11, new ItemStack(Material.GLOWSTONE));
        inventory.setItem(15, new ItemStack(Material.GLOWSTONE));
        List<ItemStack> probabilities = cache.getItemsWithProbability();
        int slot = Math.max(54 - probabilities.size(), 27);
        for(ItemStack item : probabilities){
            if(slot > 53) break;
            inventory.setItem(slot, item);
            slot++;
        }
        rewards [0] = new ItemStack(Material.DIRT);//provider.getWeightedItem();
        rewards [1] = new ItemStack(Material.DIRT);//provider.getWeightedItem();
        rewards [2] = new ItemStack(Material.DIRT); //provider.getWeightedItem();
    }

    private void reward(ItemStack stack){
        Bukkit.getPlayer(player).getWorld().dropItem(Bukkit.getPlayer(player).getEyeLocation(), stack);
        Map<String, Object> map = new HashMap<>();
        String name = stack.hasItemMeta() && stack.getItemMeta().hasDisplayName() ?
                stack.getItemMeta().getDisplayName() : stack.getType().name().toLowerCase();
        name = String.format("%s[%s%s]", ChatColor.GREEN, name, ChatColor.GREEN);
        map.put("text",name);
        if(stack.hasItemMeta() && stack.getItemMeta().hasLore()){
            Map<String, Object> hoverEvent = new HashMap<>();
            hoverEvent.put("action", "show_text");
            StringBuilder allLines = new StringBuilder();
            for(String line : stack.getItemMeta().getLore()){
                allLines.append(line).append("\n");
            }
            hoverEvent.put("value", allLines.toString());
            map.put("hoverEvent", hoverEvent);
        }else{
            Map<String, Object> hoverEvent = new HashMap<>();
            hoverEvent.put("action", "show_text");
            hoverEvent.put("value", "A normal " + stack.getType().name().toLowerCase());
            map.put("hoverEvent", hoverEvent);
        }
        List<Object> textParts = new ArrayList<>();
        textParts.add(String.format("%sYou win %dx ", ChatColor.DARK_AQUA, stack.getAmount()));
        textParts.add(map);

        String cmd = String.format("minecraft:tellraw %s %s", Bukkit.getPlayer(player).getName(), JSONValue.toJSONString(textParts));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        hasPicked = true;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        spinTask = new BukkitRunnable(){
            List<ItemStack> items = initItems();
            int count = 0;
            final int max = 50;
            int spins = 0;

            @Override
            public void run() {
                count++;
                if(count > 42 && count % 2 != 0) return;
                if(count > 47 && count % 4 != 0) return;
                Bukkit.getPlayer(player).playSound(Bukkit.getPlayer(player).getLocation(), Sound.BLOCK_WOOD_PRESSUREPLATE_CLICK_ON, SoundCategory.MASTER, .25f, 3);
                for(int i = 0; i < 9; i++){
                    int slot = i;
                    for(int num : new int[] {3,4,5}){
                        if(slot == num) slot += 9;
                    }
                    getInventory().setItem(slot, items.get(i).clone());
                }
                items.remove(0);
                spins++;
                if(spins >= max - 6 && spins < max - 3){
                    items.add(rewards[max - spins - 1 - 3]);
                } else{
                    items.add(provider.getWeightedItem());
                }
                if(spins >= max){
                    this.cancel();
                    isShuffling = false;
                }
            }

            List<ItemStack> initItems(){
                List<ItemStack> list = new ArrayList<>();
                for(int i = 0; i < 9; i++){
                    list.add(provider.getWeightedItem());
                }
                return list;
            }

        };
        spinTask.runTaskTimer(SlotCachePlugin.instance(), 1, 2);
    }

    @Override @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(spinTask != null) spinTask.cancel();
        if(isShuffling){
            //if they exit while spinning, reward a random item with no chance to pick
            reward(provider.getWeightedItem());
        }else if(!hasPicked){
            //the player already had a chance to pick, now we make that choice (from the availbe 3 choices) for them
            reward(inventory.getItem(choosableSlots[0]));
        }
    }

    @Override @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        if(isShuffling || hasPicked){
            event.setCancelled(true);
        }
        //do nothing, selection takes place onInventoryClick()
    }

    @Override @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if(isShuffling || hasPicked){
            event.setCancelled(true);
            return;
        }
        for(int slot : choosableSlots){
            if(event.getSlot() == slot){
                reward(item);
                hasPicked = true;
                return;
            }
        }
        event.setCancelled(true);
    }

    @Override @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }
}
