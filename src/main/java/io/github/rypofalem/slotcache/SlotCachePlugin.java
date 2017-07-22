package io.github.rypofalem.slotcache;

import com.winthier.custom.event.CustomRegisterEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class SlotCachePlugin extends JavaPlugin implements Listener, CommandExecutor {
    public static final Random random = new Random(System.currentTimeMillis());

    public void onEnable(){
        Bukkit.getPluginManager().registerEvents(this, this);
        this.getCommand("slotcache").setExecutor(this);

        print("\n\n\n***********************************");
        saveDefaultConfig();
        print( getConfig().getConfigurationSection("caches").getValues(true).toString());
        Map<String, Object> cacheMap = getConfig().getConfigurationSection("caches").getValues(true);
        for(String cacheName : cacheMap.keySet()){
            print(cacheName);
            if(!(cacheMap.get(cacheName) instanceof List)) continue;
            for(Object itemWeightMapObject: (List<?>) cacheMap.get(cacheName)){
                if(!(itemWeightMapObject instanceof Map)) continue;
                Map<?, ?> itemWeightMap = (Map<?,?>) itemWeightMapObject;
                print(itemWeightMap.get("item"));
                print(itemWeightMap.get("weight"));
            }
        }
        print("***********************************\n\n\n");
    }

    private void print(Object object){
        if(object == null ) return;
        if(object instanceof Integer) object = object.toString();
        if(object instanceof String){
            System.out.print((String) object);
        }
    }

    @EventHandler
    public void onCustomRegister(CustomRegisterEvent event){
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        // /slotcache add <cachename> <hand|customItemName> <weight> [number]
        String cachename = "test";
        ItemStack stack = new ItemStack(Material.APPLE);
        return true;
    }
}
