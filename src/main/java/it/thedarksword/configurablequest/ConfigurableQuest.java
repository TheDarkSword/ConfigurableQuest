package it.thedarksword.configurablequest;

import it.thedarksword.configurablequest.commands.ConfigurableQuestCommand;
import it.thedarksword.configurablequest.config.ConfigData;
import it.thedarksword.configurablequest.listeners.PlayerListener;
import it.thedarksword.configurablequest.mysql.MySQL;
import it.thedarksword.configurablequest.mysql.MySQLManager;
import it.thedarksword.configurablequest.data.QuestPlayer;
import it.thedarksword.configurablequest.yaml.Configuration;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ConfigurableQuest extends JavaPlugin {

    private MySQLManager mySQLManager;
    private ConfigData configData;

    private final Map<Integer, QuestPlayer> questPlayers = new ConcurrentHashMap<>();

    private BukkitTask walkingTask;

    @Override
    public void onEnable() {
        Configuration configuration = new Configuration(new File(getDataFolder(), "config.yml"), getResource("config.yml"));

        try {
            configuration.autoload();
        } catch (IOException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        mySQLManager = new MySQLManager(this, new MySQL(configuration.getString("mysql.host"),
                configuration.getInt("mysql.port"), configuration.getString("mysql.database"),
                configuration.getString("mysql.username"), configuration.getString("mysql.password")));

        configData = new ConfigData(configuration);

        walkingTask = getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (QuestPlayer value : questPlayers.values()) {
                value.updateBlocksWalked();
            }
        }, 20, 600);

        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {

    }

    private void registerCommands() {
        getCommand("configurablequest").setExecutor(new ConfigurableQuestCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }
}
