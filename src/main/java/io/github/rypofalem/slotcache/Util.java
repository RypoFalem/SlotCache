package io.github.rypofalem.slotcache;

public class Util {
    public static String getKeyForCacheItems(String cacheID){
        return String.format("caches.%s.items", cacheID);
    }
}
