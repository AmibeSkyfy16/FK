package ch.skyfy.fk.logic;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.ScoreboardManager;
import ch.skyfy.fk.config.Configs;
import ch.skyfy.fk.logic.persistant.PersistantFKGame;
import ch.skyfy.fk.logic.persistant.TimelineData;
import lombok.Getter;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Timeline {

    @Getter
    private final AtomicBoolean isTimerStartedRef = new AtomicBoolean(false);

    @Getter
    private final TimelineData timelineData;

    {
        timelineData = PersistantFKGame.FK_GAME_DATA.data.getTimelineData();
    }

    public Timeline() {
        ServerTickEvents.END_SERVER_TICK.register(this::updateTimeTest);
    }

    public void startTimer() {
        isTimerStartedRef.set(true);
    }

    private void updateTimeTest(MinecraftServer server) {
        if (!GameUtils.isGameState_RUNNING() || !isTimerStartedRef.get()) return;

        if (timelineData.getTimeOfDay() >= Configs.FK_CONFIG.data.getDayDuration() * 1200) {
            timelineData.setTimeOfDay(0);
            timelineData.setDay(timelineData.getDay() + 1);
        }

        var previousMinutes = timelineData.getMinutes();

        timelineData.setMinutes((int) (timelineData.getTimeOfDay() / 1200d));
        timelineData.setSeconds((int) (((timelineData.getTimeOfDay() / 1200d) - timelineData.getMinutes()) * 60));

        if (previousMinutes != timelineData.getMinutes())
            saveData();

        // Update player sidebar
        for (var fkPlayer : GameUtils.getAllConnectedFKPlayers(server.getPlayerManager().getPlayerList()))
            ScoreboardManager.getInstance().updateSidebar(fkPlayer, timelineData.getDay(), timelineData.getMinutes(), timelineData.getSeconds());

        timelineData.setTimeOfDay(timelineData.getTimeOfDay() + 1);
    }

    private void saveData() {
        try {
            PersistantFKGame.FK_GAME_DATA.jsonManager.save(PersistantFKGame.FK_GAME_DATA.data);
        } catch (IOException e) {
            FKMod.LOGGER.warn("An error occurred while trying to save game data");
            e.printStackTrace();
        }
    }
}
