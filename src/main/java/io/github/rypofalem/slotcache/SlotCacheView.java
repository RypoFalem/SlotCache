package io.github.rypofalem.slotcache;

import com.winthier.custom.inventory.CustomInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SlotCacheView implements CustomInventory{
    private final UUID player;
    private final List<ItemStack> items;
    private final Inventory inventory;
    private final SlotCacheItem cache;
    private boolean isShuffling = true;
    private boolean hasPicked = false;
    private final int[] choosableSlots = {12, 13, 14};

    SlotCacheView(Player player, SlotCacheItem cache){
        items = cache.getItems();
        Collections.shuffle(this.items);
        this.player = player.getUniqueId();
        this.cache = cache;
        inventory = Bukkit.createInventory(player, 54, "Pick ONE item from the center!");
        for(int slot = 3; slot < 6; slot++){
            inventory.setItem(slot, new ItemStack(Material.GLOWSTONE));
            inventory.setItem(slot + 18, new ItemStack(Material.GLOWSTONE));
        }
        inventory.setItem(10, new ItemStack(Material.GLOWSTONE));
        inventory.setItem(15, new ItemStack(Material.GLOWSTONE));
        List<ItemStack> probabilities = cache.getItemsWithProbability();
        int start = Math.max(53 - probabilities.size(), 27);
        for(int slot = start; slot >= 27; slot--){
            inventory.setItem(slot, probabilities.get(slot % probabilities.size()));
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        new BukkitRunnable(){
            int offset = 0;
            int spins = items.size() + SlotCachePlugin.random.nextInt(items.size());
            @Override
            public void run() {
                Bukkit.getPlayer(player).playSound(Bukkit.getPlayer(player).getLocation(), Sound.BLOCK_WOOD_PRESSUREPLATE_CLICK_ON, SoundCategory.MASTER, .25f, 3);
                for(int i = 0; i < 9; i++){
                    int slot = i;
                    for(int num : new int[] {3,4,5}){
                        if(slot == num) slot += 9;
                    }
                    getInventory().setItem(slot, items.get((i + offset) % items.size()));
                }
                offset++;
                if(offset == spins){
                    this.cancel();
                    isShuffling = false;
                }
            }
        };
    }

    @Override @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(hasPicked){
            event.getPlayer().getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
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
        if(isShuffling || hasPicked || item == null || item.getType() == Material.AIR){
            event.setCancelled(true);
            return;
        }
        for(int slot : choosableSlots){
            if(event.getSlot() == slot){
                Bukkit.getPlayer(player).getWorld().dropItem(Bukkit.getPlayer(player).getEyeLocation(), item);
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
