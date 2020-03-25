package com.mk7a.blockhunt;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.mk7a.blockhunt.Util.sendMessage;

public class BlockHuntCommands implements CommandExecutor {

    private static final String MAIN = "blockHunt";
    private static final String HELP = "help";
    private static final String ADD = "add";
    private static final String REMOVE = "remove";

    private static final int DEFAULT_REWARD = 500;

    private final BlockHuntPlugin plugin;

    protected BlockHuntCommands(BlockHuntPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand(MAIN).setExecutor(this);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {

        if (!command.getName().equalsIgnoreCase(MAIN)) {
            return false;
        }

        boolean helpRequest = args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase(HELP));
        if (helpRequest) {
            sender.sendMessage(Util.color("&6BlockHunt usage&7:" +
                    "   \n /blockhunt add <reward>" +
                    "   \n /blockhunt remove" +
                    "   \n&eWhile looking at target block."));

            return true;
        }

        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (args[0].equalsIgnoreCase(ADD)) {

                Block targetBlock = player.getTargetBlock(100);

                if (targetBlock == null) {
                    sendMessage(player, "No block in sight");
                    return true;
                }


                int rewardAmount = DEFAULT_REWARD;

                if (args.length == 2) {

                    try {
                        rewardAmount = Integer.parseInt(args[1]);
                        sendMessage(player, "Reward amount: $" + rewardAmount);

                    } catch (NumberFormatException e) {
                        sendMessage(player, "Invalid reward amount.");
                    }

                } else {
                    sendMessage(player, "Reward amount not specified. Using default: $500");
                }

                String loc = Util.formatLocation(targetBlock.getLocation());
                plugin.getBlocksFileConfig().set(loc, rewardAmount);
                sendMessage(player, "Added block.");

                return true;

            } else if (args[0].equalsIgnoreCase(REMOVE)) {

                Block targetBlock = player.getTargetBlock(100);

                if (targetBlock == null) {
                    sendMessage(player, "No block in sight.");
                    return true;
                }

                String loc = Util.formatLocation(targetBlock.getLocation());

                if (plugin.getBlocksFileConfig().getInt(loc) != 0) {

                    plugin.getBlocksFileConfig().set(loc, 0);
                    sendMessage(player, "Removed block.");

                } else {

                    sendMessage(player, "Target block is not a prize block.");
                }

                return true;

            }


        } else {
            sender.sendMessage("BlockHunt can only be managed in-game.");
        }


        return false;
    }


}
