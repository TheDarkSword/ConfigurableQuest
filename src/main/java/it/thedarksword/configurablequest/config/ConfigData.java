package it.thedarksword.configurablequest.config;

import com.google.common.collect.Maps;
import it.thedarksword.configurablequest.data.Quest;
import it.thedarksword.configurablequest.data.QuestType;
import it.thedarksword.configurablequest.yaml.Configuration;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.EnumMap;
import java.util.Map;

@Getter
public class ConfigData {

    private final Quests quests;
    private final Message message;

    public ConfigData(Configuration config) {
        quests = new Quests(config);
        message = new Message(config);
    }

    public static class Quests {
        @Getter
        private final Map<QuestType, Quest> quests = Maps.newEnumMap(QuestType.class);

        private Quests(Configuration config) {
            String path = "quests.";
            quests.put(QuestType.BLOCK_WALKED, new Quest(config.getInt(path + "blocks.walk.objective"), config.getString(path + "blocks.walk.broadcast-message"),
                    config.getString(path + "blocks.walk.message"), config.getStringList(path + "blocks.walk.commands")));
            quests.put(QuestType.BLOCK_BROKEN, new Quest(config.getInt(path + "blocks.break.objective"), config.getString(path + "blocks.break.broadcast-message"),
                    config.getString(path + "blocks.break.message"), config.getStringList(path + "blocks.break.commands")));
            quests.put(QuestType.BLOCK_PLACED, new Quest(config.getInt(path + "blocks.place.objective"), config.getString(path + "blocks.place.broadcast-message"),
                    config.getString(path + "blocks.place.message"), config.getStringList(path + "blocks.place.commands")));
            quests.put(QuestType.COMMANDS_EXECUTED, new Quest(config.getInt(path + "command-execute.objective"), config.getString(path + "command-execute.broadcast-message"),
                    config.getString(path + "command-execute.message"), config.getStringList(path + "command-execute.commands")));
            quests.put(QuestType.MOBS_KILLED, new Quest(config.getInt(path + "mobs-kill.objective"), config.getString(path + "mobs-kill.broadcast-message"),
                    config.getString(path + "mobs-kill.message"), config.getStringList(path + "mobs-kill.commands")));
        }
    }

    public static class Message {

        public final String INSUFFICIENT_PERMISSIONS;
        public final String INCORRECT_COMMAND;
        public final String PLAYER_NOT_FOUND;
        public final String DATA_COLLECTING;

        private Message(Configuration config) {
            String path = "messages.";
            INSUFFICIENT_PERMISSIONS = getTranslated(config.getString(path + "insufficient-permissions"));
            INCORRECT_COMMAND = getTranslated(config.getString(path + "incorrect-command"));
            PLAYER_NOT_FOUND = getTranslated(config.getString(path + "player-not-found"));
            DATA_COLLECTING = getTranslated(config.getString(path + "data-collecting"));
        }

        private String getTranslated(String string) {
            return ChatColor.translateAlternateColorCodes('&', string);
        }
    }
}
