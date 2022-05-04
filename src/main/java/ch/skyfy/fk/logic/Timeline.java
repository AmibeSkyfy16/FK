package ch.skyfy.fk.logic;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.ScoreboardManager;
import ch.skyfy.fk.events.TimeOfDayUpdatedCallback;
import ch.skyfy.fk.logic.data.FKGameAllData;
import ch.skyfy.fk.logic.data.TimelineData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Timeline {

    private final AtomicBoolean isStartedRef = new AtomicBoolean(false);

    public final TimelineData timelineData;

    private final MinecraftServer server;


    {
        timelineData = FKGameAllData.FK_GAME_DATA.config.getTimelineData();
    }

    public Timeline(MinecraftServer server) {
        this.server = server;

        TimeOfDayUpdatedCallback.EVENT.register(timeOfDay -> {
            if(GameUtils.isGameStateRUNNING()){
                if(isStartedRef.get()){
                    updateTime(timeOfDay);
                }
            }

            return ActionResult.PASS;
        });
    }

    public void startTimer() {
        isStartedRef.set(true);
    }

    private void updateTime(long timeOfDay){

        // TODO TICK PROBLEM -> second timer

        var previousMinutes = timelineData.getMinutes();

        var remainingTime = timeOfDay % 24_000;

        timelineData.setMinutes((int) (remainingTime / 1200d));
        timelineData.setSeconds((int) (((remainingTime / 1200d) - timelineData.getMinutes()) * 60));

        if(remainingTime == 0)
            timelineData.setDay(timelineData.getDay() + 1);

        // TODO UNCOMMENT
        if(previousMinutes != timelineData.getMinutes())
            saveData();

        // Update player sidebar
        for (var fkPlayer : GameUtils.getAllConnectedFKPlayers(server.getPlayerManager().getPlayerList()))
            ScoreboardManager.getInstance().updateSidebar(fkPlayer, timelineData.getDay(), timelineData.getMinutes(), timelineData.getSeconds());
    }

    private void saveData(){
        try {
            FKGameAllData.FK_GAME_DATA.jsonManager.save(FKGameAllData.FK_GAME_DATA.config);
        } catch (IOException e) {
            FKMod.LOGGER.warn("An error occurred while trying to save game data");
            e.printStackTrace();
        }
    }

    public AtomicBoolean getIsStartedRef() {
        return isStartedRef;
    }
}
