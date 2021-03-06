package io.github.rypofalem.slotcache;

import com.winthier.custom.inventory.CustomInventory;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SlotCacheView implements CustomInventory{
    private final UUID player;
    private final Inventory inventory;
    private final SlotCacheItem cache;
    private final RandomSlotItemProvider provider;
    private boolean isShuffling = true;
    private boolean hasPicked = false;
    private final int[] choosableSlots = {12, 13, 14}; // slots "in the middle" that can be clicked
    private final ItemStack[] rewards = new ItemStack[3];
    private BukkitRunnable spinTask;

    SlotCacheView(Player player, SlotCacheItem cache){
        this.player = player.getUniqueId();
        this.cache = cache;
        this.provider = cache.createRandomSlotItemProvider();
        inventory = Bukkit.createInventory(player, 54, "Pick ONE item from the center!");

        //add glowstone borders
        for(int slot = 3; slot < 6; slot++){
            inventory.setItem(slot, new ItemStack(Material.GLOWSTONE));
            inventory.setItem(slot + 18, new ItemStack(Material.GLOWSTONE));
        }
        inventory.setItem(11, new ItemStack(Material.GLOWSTONE));
        inventory.setItem(15, new ItemStack(Material.GLOWSTONE));

        //display items with probabilities
        List<ItemStack> probabilities = cache.getItemsWithProbability();
        int slot = Math.max(54 - probabilities.size(), 27);
        for(ItemStack item : probabilities){
            if(slot > 53) break;
            inventory.setItem(slot, item);
            slot++;
        }

        //predetermine the items that will be availible for choice
        rewards [0] = provider.getWeightedItem();
        rewards [1] = provider.getWeightedItem();
        rewards [2] = provider.getWeightedItem();
    }

    //give selected (or force selected) item to player, displaying a message in chat describing the item
    private void reward(ItemStack stack){
        Bukkit.getPlayer(player).getWorld().dropItem(Bukkit.getPlayer(player).getEyeLocation(), stack);
        String cmd = String.format("minecraft:tellraw %s %s", Bukkit.getPlayer(player).getName(), Util.getDescriptiveButton(stack));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        hasPicked = true;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {

        //task to create movement of items through the chest
        spinTask = new BukkitRunnable(){
            List<ItemStack> items = initItems();
            int count = 0; //how many times the items have attempted to shift
            final int max = 50;
            int spins = 0; //how many times the items have shifted, excluding the skipped ticks

            @Override
            public void run() {
                count++;
                //skip some ticks so it looks like the wheel is slowing down gradually before it stops
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

                //add new items to the edge of the wheel. If the itemslot is predetermined to be picked, make sure it's the predetermined items
                if(spins >= max - 6 && spins < max - 3){
                    items.add(rewards[max - spins - 1 - 3]);
                } else{
                    items.add(provider.getWeightedItem());
                }

                //end spin
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
        if(isShuffling || !hasPicked) reward(rewards[0]);
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
                break; //break so event is cancelled
            }
        }
        event.setCancelled(true);
    }

    @Override @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }
}
