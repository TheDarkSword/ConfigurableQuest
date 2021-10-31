package it.thedarksword.configurablequest.mysql;

import it.thedarksword.configurablequest.ConfigurableQuest;
import it.thedarksword.configurablequest.data.QuestPlayer;
import it.thedarksword.configurablequest.data.ReadableOnlyQuestPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@RequiredArgsConstructor
public class MySQLManager {

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    private final String table = "cq_playerdata";
    private final ConfigurableQuest configurableQuest;
    private final MySQL mySQL;

    public void createTable() throws SQLException {
        mySQL.createTable(new String[] {
                "id INT PRIMARY KEY AUTO_INCREMENT",
                "uuid VARCHAR(36) NOT NULL UNIQUE",
                "username VARCHAR(30) NOT NULL UNIQUE",
                "block_walked INT NOT NULL DEFAULT 0",
                "block_broken INT NOT NULL DEFAULT 0",
                "block_placed INT NOT NULL DEFAULT 0",
                "commands_executed INT NOT NULL DEFAULT 0",
                "mobs_killed INT NOT NULL DEFAULT 0",
                "completed INT NOT NULL DEFAULT 0"
        }, table);
    }

    public CompletableFuture<QuestPlayer> loadQuestPlayer(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if(mySQL.rowExists("uuid", player.getUniqueId().toString(), table)) {
                    String query = "SELECT block_walked, block_broken, block_placed, commands_executed, mobs_killed, completed " +
                            "FROM {table} WHERE uuid = '" + player.getUniqueId() + "'";
                    CompositeResult result = mySQL.executeQuery(query, table);
                    QuestPlayer questPlayer;
                    if(result.next()) {
                        questPlayer = new QuestPlayer(configurableQuest, player, result.getInt("block_walked"), result.getInt("block_break"),
                                result.getInt("block_place"), result.getInt("commands_executed"),
                                result.getInt("mob_kills"), result.getInt("completed"));
                    } else {
                        questPlayer = new QuestPlayer(configurableQuest, player);
                    }
                    result.close();
                    return questPlayer;
                } else {
                    mySQL.addRow(new String[]{
                            "uuid",
                            "username"
                    }, new Object[]{
                            player.getUniqueId().toString(),
                            player.getName()
                    }, table);
                    return new QuestPlayer(configurableQuest, player);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }, executor);
    }

    public CompletableFuture<Optional<ReadableOnlyQuestPlayer>> readQuestPlayer(String username) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if(mySQL.rowExists("username", username, table)) {
                    String query = "SELECT block_walked, block_broken, block_placed, commands_executed, mobs_killed FROM {table} WHERE username = '" + username + "'";
                    CompositeResult result = mySQL.executeQuery(query, table);
                    ReadableOnlyQuestPlayer questPlayer;
                    if(result.next()) {
                        questPlayer = new ReadableOnlyQuestPlayer(result.getInt("block_walked"), result.getInt("block_break"),
                                result.getInt("block_place"), result.getInt("commands_executed"), result.getInt("mob_kills"));
                    } else {
                        questPlayer = null;
                    }
                    result.close();
                    return Optional.ofNullable(questPlayer);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        }, executor);
    }

    public CompletableFuture<Void> saveQuestPlayer(QuestPlayer questPlayer) {
        return CompletableFuture.runAsync(() -> {
            try {
                if(mySQL.rowExists("uuid", questPlayer.getPlayer().getUniqueId().toString(), table)) {
                    mySQL.set(
                            new String[] {
                                    "block_walked", "block_broken", "block_placed", "commands_executed", "mobs_killed", "completed"
                            }, new Object[] {
                                    questPlayer.getBlocksWalked(), questPlayer.getBlocksBroken(), questPlayer.getBlocksPlaced(),
                                    questPlayer.getCommandsExecuted(), questPlayer.getMobsKilled(), questPlayer.getCompleted()
                            }, "uuid", questPlayer.getPlayer().getUniqueId().toString(), table
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, executor);
    }

    public void deleteAllRecords() throws SQLException {
        mySQL.executeUpdate("TRUNCATE {table}", table);
    }
}
