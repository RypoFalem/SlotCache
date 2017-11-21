package io.github.rypofalem.slotcache;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.item.CustomItem;
import io.github.rypofalem.slotcache.slotitems.*;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CommandEx implements CommandExecutor{

    private FileConfiguration getConfig(){
        return SlotCachePlugin.instance().getConfig();
    }

    private void saveConfig(){
        SlotCachePlugin.instance().saveConfig();
    }

    private void reloadConfig(){
        SlotCachePlugin.instance().reloadConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
//        /<command> add <cachename> <rph|hand|customItemName> <weight> [amount(min:max)]
//        /<command> <list|ls> <cachename>
        if(args == null || args.length < 1) return false;

        switch (args[0].toLowerCase()){
            case "add" : return addToCache(sender, args);
            case "reload" : reloadConfig(); return true;
            case "ls":
            case "list":

                return true;
            default: return false;
        }
    }

    private boolean addToCache(CommandSender sender, String[] args){
        // /<command> add <cachename> <rph|hand|customID> <weight> [amount(min:max)]
        if(args.length < 4) return false;
        String cacheName = args[1];
        ItemStack item;
        SlotItem slotItem;
        int weight;
        int min = 1;
        int max = 1;

        //parse weight and amount
        try{
            weight = Integer.parseInt(args[3]);
            if(args.length > 4){
                String[] nums = args[4].split(":");
                min = Integer.parseInt(nums[0]);
                max = Integer.parseInt(nums[1]);
            }
        } catch(Exception e){
            sender.sendMessage("Error parsing integers. Make sure weight is an integer and amount (if included) is in format 'min:max'");
            return true;
        }

        //parse item
        switch(args[2].toLowerCase()){
            case "rph":
                slotItem = new RandomHeadItem(weight, min, max);
                break;

            case "hand":
                if(!(sender instanceof Player)){
                    sender.sendMessage("Only players can use the hand argument");
                    return true;
                }
                item = ((Player)sender).getEquipment().getItemInMainHand().clone();
                if(item == null || item.getType() == Material.AIR){
                    sender.sendMessage("You must be holding an item in your mainhand!");
                    return true;
                }
                item.setAmount(1);
                CustomItem customItem = CustomPlugin.getInstance().getItemManager().getCustomItem(item);
                if(customItem != null){
                    sender.sendMessage("Warning: It looks like the item in your hand is a custom item. " +
                            "Items processed with the hand argument copy the literal state of the item. " +
                            "If you prefer to get the default state of a custom item, delete this entry from the config and use the custom item ID instead of the hand argument.");
                }
                slotItem = new BasicItem(item, weight, min, max);
                break;

            //default expects the argument to be a custom item ID
            default:
                item = CustomPlugin.getInstance().getItemManager().getCustomItem(args[2]).spawnItemStack(1);
                if(item == null){
                    sender.sendMessage(String.format("No such custom item with ID: '%s'", args[2]));
                    return true;
                }
                slotItem = new CustomSlotItem(args[2], weight, min, max);
        }

        List list= getConfig().getList(Util.getKeyForCacheItems(cacheName), new ArrayList<>());
        try{list.add(slotItem.toMap());}
        catch(Exception e) {
            sender.sendMessage("Unable to add map config caches." + cacheName + ". You shouldn't ever see this message.");
            return true;
        }
        getConfig().set(Util.getKeyForCacheItems(cacheName), list);
        saveConfig();
        return true;
    }
}
