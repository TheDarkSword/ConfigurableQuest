package it.thedarksword.configurablequest.commands;

import it.thedarksword.configurablequest.ConfigurableQuest;
import it.thedarksword.configurablequest.data.ReadableOnlyQuestPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class ConfigurableQuestCommand implements CommandExecutor {

    private final ConfigurableQuest configurableQuest;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("cq.info")){
            sender.sendMessage(configurableQuest.getConfigData().getMessage().INSUFFICIENT_PERMISSIONS);
            return true;
        }
        if(args.length == 0){
            sender.sendMessage(configurableQuest.getConfigData().getMessage().INCORRECT_COMMAND);
            return true;
        }
        sender.sendMessage(configurableQuest.getConfigData().getMessage().DATA_COLLECTING);
        configurableQuest.getMySQLManager().readQuestPlayer(args[0]).thenAccept(optional -> {
           if(!optional.isPresent()) {
               sender.sendMessage(configurableQuest.getConfigData().getMessage().PLAYER_NOT_FOUND);
               return;
           }
           ReadableOnlyQuestPlayer questPlayer = optional.get();
           sender.sendMessage(ChatColor.GREEN + args[0] + ChatColor.GRAY + " statistics: ");
           sender.sendMessage(ChatColor.GRAY + "- Blocks Walked: " + ChatColor.GREEN + questPlayer.getBlocksWalked());
           sender.sendMessage(ChatColor.GRAY + "- Blocks Broken: " + ChatColor.GREEN + questPlayer.getBlocksBroken());
           sender.sendMessage(ChatColor.GRAY + "- Blocks Placed: " + ChatColor.GREEN + questPlayer.getBlocksPlaced());
           sender.sendMessage(ChatColor.GRAY + "- Commands Executed: " + ChatColor.GREEN + questPlayer.getCommandsExecuted());
           sender.sendMessage(ChatColor.GRAY + "- Mobs Killed: " + ChatColor.GREEN + questPlayer.getMobsKilled());
        });
        return true;
    }
}
