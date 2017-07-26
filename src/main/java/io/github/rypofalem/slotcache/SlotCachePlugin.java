package io.github.rypofalem.slotcache;

import com.winthier.custom.event.CustomRegisterEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@Getter
public class SlotCachePlugin extends JavaPlugin implements Listener{
    @Getter static final Random random = new Random(System.currentTimeMillis());
    @Getter static SlotCachePlugin instance;
    List<SlotCacheItem> caches;

    public SlotCachePlugin() {
        instance = this;
    }

    public void onEnable(){
        Bukkit.getPluginManager().registerEvents(this, this);
        this.getCommand("slotcache").setExecutor(new CommandEx());
        saveDefaultConfig();
    }

    public static SlotCachePlugin instance(){ return instance;}

    @EventHandler
    public void onCustomRegister(CustomRegisterEvent event){
        caches = new ArrayList<>();
        for(String id : getConfig().getConfigurationSection("caches").getKeys(false)){
            SlotCacheItem slotCacheItem = new SlotCacheItem(id);
            event.addItem(slotCacheItem);
            caches.add(slotCacheItem);
        }
    }
}