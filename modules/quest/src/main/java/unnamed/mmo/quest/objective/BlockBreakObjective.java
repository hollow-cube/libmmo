package unnamed.mmo.quest.objective;

import net.minestom.server.instance.block.Block;

public class BlockBreakObjective implements QuestObjective {

    private final int blockId;
    private final int count;
    private int current;

    public BlockBreakObjective(Block block, int count) {
        blockId = block.id();
        this.count = count;
        current = 0;
    }

    @Override
    public void onBlockBreak(Block block) {
        if(block.id() == blockId) {
            current++;
        }
    }

    @Override
    public boolean isObjectiveComplete() {
        return current >= count;
    }

}
