package ch.skyfy.fk.logic;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.ScoreboardManager;
import ch.skyfy.fk.logic.data.FKGameAllData;
import ch.skyfy.fk.logic.data.TimelineData;
import lombok.Getter;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Timeline {

    @Getter
    private final AtomicBoolean isTimerStartedRef = new AtomicBoolean(false);

    @Getter
    private final AtomicInteger timeOfDay = new AtomicInteger(0);

    @Getter
    private final TimelineData timelineData;

    {
        timelineData = FKGameAllData.FK_GAME_DATA.config.getTimelineData();
    }

    public Timeline() {
        ServerTickEvents.END_SERVER_TICK.register(this::updateTimeTest);
    }

    public void startTimer() {
        isTimerStartedRef.set(true);
    }

    private void updateTimeTest(MinecraftServer server) {
        if (!GameUtils.isGameState_RUNNING() || !isTimerStartedRef.get()) return;

        if (timeOfDay.get() >= 24000) {
            timeOfDay.set(0);
            timelineData.setDay(timelineData.getDay() + 1);
        }

        var previousMinutes = timelineData.getMinutes();

        timelineData.setMinutes((int) (timeOfDay.get() / 1200d));
        timelineData.setSeconds((int) (((timeOfDay.get() / 1200d) - timelineData.getMinutes()) * 60));

        if (previousMinutes != timelineData.getMinutes())
            saveData();

        // Update player sidebar
        for (var fkPlayer : GameUtils.getAllConnectedFKPlayers(server.getPlayerManager().getPlayerList()))
            ScoreboardManager.getInstance().updateSidebar(fkPlayer, timelineData.getDay(), timelineData.getMinutes(), timelineData.getSeconds());

        timeOfDay.getAndIncrement();
    }

    private void saveData() {
        try {
            FKGameAllData.FK_GAME_DATA.jsonManager.save(FKGameAllData.FK_GAME_DATA.config);
        } catch (IOException e) {
            FKMod.LOGGER.warn("An error occurred while trying to save game data");
            e.printStackTrace();
        }
    }
}
