package ch.skyfy.fk.config;

import ch.skyfy.fk.data.WaitingRoom;
import ch.skyfy.fk.json.Validatable;
import ch.skyfy.fk.utils.ValidateUtils;
import lombok.Getter;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class FKConfig implements Validatable {
    @Getter
    private final int dayOfAuthorizationOfTheAssaults;
    @Getter
    private final int dayOfAuthorizationOfTheEntryInTheNether;
    @Getter
    private final int dayOfAuthorizationOfTheEntryInTheEnd;
    @Getter
    private final int dayOfAuthorizationOfThePvP;
    @Getter
    private final int dayDuration;
    @Getter
    private final boolean shouldTeleportPlayersToTheirOwnBaseWhenGameIsStarted;
    @Getter
    private final boolean allowEnderPearlAssault;
    @Getter
    private final WaitingRoom waitingRoom;

    public FKConfig(int dayOfAuthorizationOfTheAssaults,
                    int dayOfAuthorizationOfTheEntryInTheNether,
                    int dayOfAuthorizationOfTheEntryInTheEnd,
                    int dayOfAuthorizationOfThePvP,
                    int dayDuration,
                    boolean allowEnderPearlAssault,
                    boolean shouldTeleportPlayersToTheirOwnBaseWhenGameIsStarted, WaitingRoom waitingRoom) {

        this.dayOfAuthorizationOfTheAssaults = dayOfAuthorizationOfTheAssaults;
        this.dayOfAuthorizationOfTheEntryInTheNether = dayOfAuthorizationOfTheEntryInTheNether;
        this.dayOfAuthorizationOfTheEntryInTheEnd = dayOfAuthorizationOfTheEntryInTheEnd;
        this.dayOfAuthorizationOfThePvP = dayOfAuthorizationOfThePvP;
        this.dayDuration = dayDuration;
        this.allowEnderPearlAssault = allowEnderPearlAssault;
        this.shouldTeleportPlayersToTheirOwnBaseWhenGameIsStarted = shouldTeleportPlayersToTheirOwnBaseWhenGameIsStarted;
        this.waitingRoom = waitingRoom;
    }

    @SuppressWarnings("CommentedOutCode")
    @Override
    public void validate() {
        var errors = new ArrayList<String>();

        validateNonNull(errors);
        validatePrimitivesType(errors);

        if (!Identifier.isValid(waitingRoom.getSpawnLocation().getDimensionName()))
            errors.add("dimensionName " + waitingRoom.getSpawnLocation().getDimensionName() + " is not a valid dimension name");

//        Configs.WORLD_CONFIG.data.getWorldBorderData().getSpawns().entrySet()
//                .stream()
//                .filter(entry -> entry.getKey().equals(waitingRoom.getSpawnLocation().getDimensionName()))
//                .findFirst()
//                .ifPresent(entry -> {
//                    if (!MathUtils.isInside(entry.getValue(), waitingRoom.getCube()))
//                        errors.add("the waiting room is not inside the world border !");
//                });

        confirmValidate(errors);
    }

    @Override
    public void validatePrimitivesType(List<String> errors) {
        if (dayOfAuthorizationOfTheAssaults < 0)
            errors.add("dayOfAuthorizationOfTheAssaults value is currently " + dayOfAuthorizationOfTheAssaults + " it should not be smaller than 0");
        if (dayOfAuthorizationOfTheEntryInTheNether < 0)
            errors.add("dayOfAuthorizationOfTheEntryInTheNether value is currently " + dayOfAuthorizationOfTheEntryInTheNether + " it should not be smaller than 0");
        if (dayOfAuthorizationOfTheEntryInTheEnd < 0)
            errors.add("dayOfAuthorizationOfTheEntryInTheEnd value is currently " + dayOfAuthorizationOfTheEntryInTheEnd + " it should not be smaller than 0");
        if (dayOfAuthorizationOfThePvP < 0)
            errors.add("dayOfAuthorizationOfThePvP value is currently " + dayOfAuthorizationOfThePvP + " it should not be smaller than 0");
        if(dayDuration < 0)
            errors.add("dayDuration value is currently " + dayDuration + " it should not be smaller than 0");

        ValidateUtils.checkForNegativeValueInCubeClass(waitingRoom.getCube(), errors);
    }

}
