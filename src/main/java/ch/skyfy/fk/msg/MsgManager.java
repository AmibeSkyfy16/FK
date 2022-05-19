package ch.skyfy.fk.msg;

import ch.skyfy.fk.config.Configs;
import ch.skyfy.fk.logic.GameUtils;
import ch.skyfy.fk.logic.persistant.TimelineData;
import ch.skyfy.fk.logic.time.TimeUnit;
import ch.skyfy.fk.logic.time.Timeline;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

@SuppressWarnings("CommentedOutCode")
public class MsgManager {

    private final MinecraftServer server;
    private final List<TimedMsg> timedMsgList;

    public MsgManager(MinecraftServer server) {
        this.server = server;
        timedMsgList = new ArrayList<>();

        // ------------------------ SOME EXAMPLES ------------------------  \\
//        timedMsgList.add(new TimedMsg("EVERY 5 seconds !",
//                new HashMap<>() {{
//                    put(TimeUnit.SECOND, 5);
//                }},
//                Scheduled.REPEAT)
//        );
//        timedMsgList.add(new TimedMsg(" every new 11 seconds !",
//                new HashMap<>() {{
//                    put(TimeUnit.SECOND, 11);
//                }},
//                Scheduled.ONCE)
//        );
//        timedMsgList.add(new TimedMsg(" one time: at day 1, minute 4, second 0 !",
//                new HashMap<>() {{
//                    put(TimeUnit.DAY, 11);
//                    put(TimeUnit.MINUTE, 4);
//                    put(TimeUnit.SECOND, 0);
//                }},
//                Scheduled.ONCE)
//        );

        timedMsgList.add(new TimedMsg(
                "[RAPPEL] -> Votre salle des coffres doit avoir été construite avant le jour " + Configs.FK_CONFIG.data.getDayOfAuthorizationOfTheAssaults(),
                new HashMap<>() {{
                    put(TimeUnit.MINUTE, 10);
                }},
                Scheduled.ONCE
        ));

        Timeline.TimeUpdatedCallback.EVENT.register((oldValue, newValue) -> timedMsgList.forEach(timedMsg -> timedMsg.execute(oldValue, newValue)));
    }


    private enum Scheduled {
        ONCE,
        REPEAT
    }

    private final class TimedMsg {
        private final String str;
        private final Map<TimeUnit, Integer> conditions;
        private final Scheduled scheduled;
        private int lastTime = 0;

        private TimedMsg(String str, Map<TimeUnit, Integer> conditions, Scheduled scheduled) {
            this.scheduled = scheduled;
            this.str = str;
            this.conditions = conditions;

        }

        public void execute(TimelineData oldValue, TimelineData newValue) {
            var it = conditions.entrySet().iterator();
            if (it.hasNext()) {
                if (applyCondition(it, it.next(), oldValue, newValue)) {
                    for (ServerPlayerEntity player : GameUtils.getAllConnectedFKPlayers(server.getPlayerManager().getPlayerList())) {
                        player.sendMessage(Text.of(str), false);
                    }
                    System.out.println(str);
                }
            }
        }

        private boolean applyCondition(Iterator<Map.Entry<TimeUnit, Integer>> it, Map.Entry<TimeUnit, Integer> entry, TimelineData oldValue, TimelineData newValue) {
            var result = switch (entry.getKey()) {
                case DAY -> impl(oldValue.getDay(), newValue.getDay(), entry.getValue());
                case MINUTE -> impl(oldValue.getMinutes(), newValue.getMinutes(), entry.getValue());
                case SECOND -> impl(oldValue.getSeconds(), newValue.getSeconds(), entry.getValue());
            };
            if (!result) return false;
            if (it.hasNext()) {
                applyCondition(it, it.next(), oldValue, newValue);
            }
            return true;
        }

        private boolean impl(int oldValue, int newValue, int targetValue) {
            if (oldValue != newValue) lastTime++;
            return switch (scheduled) {
                case ONCE -> oldValue != newValue && newValue == targetValue;
                case REPEAT -> {
                    if (lastTime >= targetValue) {
                        lastTime = 0;
                        yield true;
                    }
                    yield false;
                }
            };
        }

    }


}
