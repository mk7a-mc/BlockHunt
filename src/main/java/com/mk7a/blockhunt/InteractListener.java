package com.mk7a.blockhunt;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import static com.mk7a.blockhunt.Util.sendMessage;


public class InteractListener implements Listener {

    private final BlockHuntPlugin plugin;

    protected InteractListener(BlockHuntPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {

            if (event.getClickedBlock() == null) {
                return;
            }

            Player player = event.getPlayer();
            Block clicked = event.getClickedBlock();
            String loc = Util.formatLocation(clicked.getLocation());

            int reward = plugin.getBlocksFileConfig().getInt(loc);

            if (reward != 0) {

                sendMessage(player, "&aBlock Found!");

                if (!hasActiveCooldown(player, loc)) {

                    if (player.hasPermission(Permissions.COLLECT)) {

                        plugin.getEconomy().depositPlayer(player, reward);
                        sendMessage(player, "&a$" + reward + "&7 added to your balance.");
                        startCooldown(player, loc);

                    } else {
                        sendMessage(player, " &cNo permission to collect reward.");
                    }

                } else {
                    sendMessage(player, "Reward cooldown is active.");
                }
            }
        }
    }


    private boolean hasActiveCooldown(Player player, String location) {

        String cooldownString = plugin.getCooldownFileConfig().getString(location + "." + player.getUniqueId().toString());

        boolean hasActiveCooldown = false;

        if (cooldownString != null) {

            long lastCollect = Long.parseLong(cooldownString);

            if (lastCollect != 0) {
                hasActiveCooldown = System.currentTimeMillis() - lastCollect < plugin.getCooldownDuration();
            }
        }

        return hasActiveCooldown;
    }


    private void startCooldown(Player player, String location) {
        plugin.getCooldownFileConfig().set(location + "." + player.getUniqueId().toString(),
                String.valueOf(System.currentTimeMillis()));

    }


}
