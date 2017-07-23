package io.github.rypofalem.slotcache;

import com.winthier.custom.CustomPlugin;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//        /<command> add <cachename> <hand|customItemName> <weight> [amount(min:max)]
//        /<command> <list|ls> <cachename>
        if(args == null || args.length < 1) return false;

        switch (args[0].toLowerCase()){
            case "add" : return addToCache(sender, args);

            default: return false;
        }
        //return true;
    }

    private boolean addToCache(CommandSender sender, String[] args){
        // /<command> add <cachename> <hand|customID> <weight> [amount(min:max)]
        if(args.length < 4) return false;
        String cacheName = args[1];
        ItemStack item;
        int weight;
        int min = 1;
        int max = 1;

        if(args[2].equalsIgnoreCase("hand")){
            if(sender instanceof Player){
                item = ((Player)sender).getEquipment().getItemInMainHand().clone();
                if(item == null || item.getType() == Material.AIR){
                    sender.sendMessage("You must be holding an item in your mainhand!");
                    return true;
                }
                item.setAmount(1);
            } else {
                sender.sendMessage("Only players can use the hand argument");
                return true;
            }
        } else {
            item = CustomPlugin.getInstance().getItemManager().getCustomItem(args[2]).spawnItemStack(1);
            if(item == null){
                sender.sendMessage(String.format("No such custom item with ID: '%s'", args[2]));
                return true;
            }
        }

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

        Map<String, Object> map = new HashMap<>();
        map.put("item", item);
        map.put("weight", weight);
        map.put("min", min);
        map.put("max", max);
        List list = getConfig().getList(Util.getKeyForCacheItems(cacheName), new ArrayList<>());
        try{list.add(map);}
        catch(Exception e) {
            sender.sendMessage("Unable to add map config caches." + cacheName + ". You shouldn't ever see this message.");
            return true;}

        getConfig().set(Util.getKeyForCacheItems(cacheName), list);
        saveConfig();

        return true;
    }
}
