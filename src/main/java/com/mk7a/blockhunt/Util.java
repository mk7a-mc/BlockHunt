package com.mk7a.blockhunt;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class Util {

    protected static String formatLocation(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        String world = location.getWorld().toString();
        return String.format("[x~%d|y~%d|z~%d|world~%s]", x, y, z, world);
    }

    protected static String color(String i) {
        return ChatColor.translateAlternateColorCodes('&', i);
    }

    protected static void sendMessage(Player player, String message) {
        player.sendMessage(BlockHuntPlugin.prefix + color(message));
    }
}
