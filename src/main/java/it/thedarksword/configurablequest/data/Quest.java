package it.thedarksword.configurablequest.data;

import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.List;

@Getter
public class Quest {

    private final int objective;
    private final String broadcastMessage;
    private final String message;
    private final List<String> commands;

    public Quest(int objective, String broadcastMessage, String message, List<String> commands) {
        this.objective = objective;
        this.broadcastMessage = getTranslated(broadcastMessage);
        this.message = getTranslated(message);
        this.commands = commands;
    }

    private String getTranslated(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
