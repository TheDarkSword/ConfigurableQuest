package it.thedarksword.configurablequest.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ReadableOnlyQuestPlayer {

    private final int blocksWalked;
    private final int blocksBroken;
    private final int blocksPlaced;
    private final int commandsExecuted;
    private final int mobsKilled;
}
