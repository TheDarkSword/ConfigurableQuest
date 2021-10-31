package it.thedarksword.configurablequest.data;

import lombok.Getter;

public enum QuestType {
    BLOCK_WALKED(16),
    BLOCK_BROKEN(8),
    BLOCK_PLACED(4),
    COMMANDS_EXECUTED(2),
    MOBS_KILLED(1);

    @Getter private final int controlBit;

    QuestType(int controlBit) {
        this.controlBit = controlBit;
    }
}
