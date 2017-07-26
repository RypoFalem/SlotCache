package io.github.rypofalem.slotcache;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {
    public static String getKeyForCacheItems(String cacheID){
        return String.format("caches.%s.items", cacheID);
    }

    public static String getDescriptiveButton(ItemStack stack){
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
        return JSONValue.toJSONString(textParts);
    }
}
