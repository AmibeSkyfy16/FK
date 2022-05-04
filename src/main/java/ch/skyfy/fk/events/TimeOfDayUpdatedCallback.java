package ch.skyfy.fk.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface TimeOfDayUpdatedCallback {

    Event<TimeOfDayUpdatedCallback> EVENT = EventFactory.createArrayBacked(TimeOfDayUpdatedCallback.class,
            (listeners) -> (timeOfDay) -> {
                for (TimeOfDayUpdatedCallback listener : listeners) {
                    ActionResult result = listener.onTimeOfDayUpdated(timeOfDay);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult onTimeOfDayUpdated(long timeOfDay);

}
