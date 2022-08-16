package unnamed.mmo.quest.objective;

import unnamed.mmo.quest.QuestProgress;

public class ProgressObjective implements QuestObjective {

    private final QuestProgress progressFlag;
    private boolean achieved;

    public ProgressObjective(QuestProgress progress) {
        this.progressFlag = progress;
    }


    @Override
    public void onProgressComplete(QuestProgress progress) {
        if(progress == progressFlag) {
            achieved = true;
        }
    }

    @Override
    public boolean isObjectiveComplete() {
        return achieved;
    }
}
