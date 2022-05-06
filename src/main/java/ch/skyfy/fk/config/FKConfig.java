package ch.skyfy.fk.config;

import ch.skyfy.fk.config.data.SpawnLocation;
import ch.skyfy.fk.config.data.WaitingRoom;
import ch.skyfy.fk.json.Validatable;
import ch.skyfy.fk.utils.MathUtils;
import ch.skyfy.fk.utils.ValidateUtils;
import lombok.Getter;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

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
    private final WaitingRoom waitingRoom;
    @Getter
    private final SpawnLocation worldSpawn;

    public FKConfig(int dayOfAuthorizationOfTheAssaults, int dayOfAuthorizationOfTheEntryInTheNether, int dayOfAuthorizationOfTheEntryInTheEnd, int dayOfAuthorizationOfThePvP, WaitingRoom waitingRoom, SpawnLocation worldSpawn) {
        this.dayOfAuthorizationOfTheAssaults = dayOfAuthorizationOfTheAssaults;
        this.dayOfAuthorizationOfTheEntryInTheNether = dayOfAuthorizationOfTheEntryInTheNether;
        this.dayOfAuthorizationOfTheEntryInTheEnd = dayOfAuthorizationOfTheEntryInTheEnd;
        this.dayOfAuthorizationOfThePvP = dayOfAuthorizationOfThePvP;
        this.waitingRoom = waitingRoom;
        this.worldSpawn = worldSpawn;
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();

        if (dayOfAuthorizationOfTheAssaults < 0)
            errors.add("dayOfAuthorizationOfTheAssaults value is currently " + dayOfAuthorizationOfTheAssaults + " it should not be smaller than 0");
        if (dayOfAuthorizationOfTheEntryInTheNether < 0)
            errors.add("dayOfAuthorizationOfTheEntryInTheNether value is currently " + dayOfAuthorizationOfTheEntryInTheNether + " it should not be smaller than 0");
        if (dayOfAuthorizationOfTheEntryInTheEnd < 0)
            errors.add("dayOfAuthorizationOfTheEntryInTheEnd value is currently " + dayOfAuthorizationOfTheEntryInTheEnd + " it should not be smaller than 0");
        if (dayOfAuthorizationOfThePvP < 0)
            errors.add("dayOfAuthorizationOfThePvP value is currently " + dayOfAuthorizationOfThePvP + " it should not be smaller than 0");

        ValidateUtils.checkForNegativeValueInCubeClass(waitingRoom.getCube(), errors);

        if (!Identifier.isValid(waitingRoom.getSpawnLocation().getDimensionName())) {
            errors.add("dimensionName " + waitingRoom.getSpawnLocation().getDimensionName() + " is not a valid dimension name");
        }

//         Check if the waiting room is inside the world border
        var worldBorderCube = Configs.WORLD_CONFIG.config.getWorldBorderData().getCube();
        if(!MathUtils.isInside(worldBorderCube, waitingRoom.getCube())){
            errors.add("the waiting room is not inside the world border !");
        }

        confirmValidate(errors);
    }
}
