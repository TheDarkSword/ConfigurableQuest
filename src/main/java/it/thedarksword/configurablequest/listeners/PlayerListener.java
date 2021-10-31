package it.thedarksword.configurablequest.listeners;

import it.thedarksword.configurablequest.ConfigurableQuest;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final ConfigurableQuest configurableQuest;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        configurableQuest.getMySQLManager().loadQuestPlayer(event.getPlayer())
                .thenAccept(questPlayer -> configurableQuest.getQuestPlayers().put(event.getPlayer().getEntityId(), questPlayer));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        configurableQuest.getMySQLManager().saveQuestPlayer(configurableQuest.getQuestPlayers().get(event.getPlayer().getEntityId()))
                .thenRun(() -> configurableQuest.getQuestPlayers().remove(event.getPlayer().getEntityId()));
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if(!event.isCancelled())
            configurableQuest.getQuestPlayers().get(event.getPlayer().getEntityId()).blocksBrokenIncrement();
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if(!event.isCancelled())
            configurableQuest.getQuestPlayers().get(event.getPlayer().getEntityId()).blocksPlacedIncrement();
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if(!event.isCancelled())
            configurableQuest.getQuestPlayers().get(event.getPlayer().getEntityId()).commandsExecutedIncrement();
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onKillMob(EntityDeathEvent event) {
        if(event.getEntityType() != EntityType.PLAYER && event.getEntity().getKiller() != null)
            configurableQuest.getQuestPlayers().get(event.getEntity().getKiller().getEntityId()).mobsKilledIncrement();
    }
}
