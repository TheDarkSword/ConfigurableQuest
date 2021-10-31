package it.thedarksword.configurablequest.data;

import it.thedarksword.configurablequest.ConfigurableQuest;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

@Getter
public class QuestPlayer {

    private final ConfigurableQuest configurableQuest;
    private final Player player;
    private int blocksWalked;
    private int blocksBroken;
    private int blocksPlaced;
    private int commandsExecuted;
    private int mobsKilled;
    private int completed;

    public QuestPlayer(ConfigurableQuest configurableQuest, Player player) {
        this(configurableQuest, player, 0, 0, 0, 0, 0, 0);
    }

    public QuestPlayer(ConfigurableQuest configurableQuest, Player player, int blocksWalked, int blocksBroken, int blocksPlaced, int commandsExecuted, int mobsKilled, int completed) {
        this.configurableQuest = configurableQuest;
        this.player = player;
        this.blocksWalked = blocksWalked;
        this.blocksBroken = blocksBroken;
        this.blocksPlaced = blocksPlaced;
        this.commandsExecuted = commandsExecuted;
        this.mobsKilled = mobsKilled;
        this.completed = completed;
    }

    public void updateBlocksWalked() {
        blocksWalked = player.getStatistic(Statistic.WALK_ONE_CM) / 100;
        if((completed & 16) != 16) winCheck(QuestType.BLOCK_WALKED, blocksWalked);
    }

    public void blocksBrokenIncrement() {
        blocksBroken++;
        if((completed & 8) != 8) winCheck(QuestType.BLOCK_BROKEN, blocksBroken);
    }

    public void blocksPlacedIncrement() {
        blocksPlaced++;
        if((completed & 4) != 4) winCheck(QuestType.BLOCK_PLACED, blocksPlaced);
    }

    public void commandsExecutedIncrement() {
        commandsExecuted++;
        if((completed & 2) != 2) winCheck(QuestType.COMMANDS_EXECUTED, commandsExecuted);
    }

    public void mobsKilledIncrement() {
        mobsKilled++;
        if((completed & 1) != 1) winCheck(QuestType.MOBS_KILLED, mobsKilled);
    }

    private void winCheck(QuestType questType, int var) {
        Quest quest = configurableQuest.getConfigData().getQuests().getQuests().get(questType);
        if(var >= quest.getObjective()) {
            if(!quest.getBroadcastMessage().isEmpty()){
                Bukkit.getServer().broadcastMessage(quest.getBroadcastMessage());
            }
            if(!quest.getMessage().isEmpty()){
                player.sendMessage(quest.getMessage());
            }
            for(String command : quest.getCommands()) {
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("@player", player.getName()));
            }
            completed |= questType.getControlBit();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestPlayer)) return false;
        QuestPlayer that = (QuestPlayer) o;
        return getPlayer().getEntityId() == that.getPlayer().getEntityId();
    }

    @Override
    public int hashCode() {
        return player.getEntityId();
    }
}
