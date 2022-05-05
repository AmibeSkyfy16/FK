package ch.skyfy.fk.config;

import ch.skyfy.fk.config.data.SpawnLocation;
import ch.skyfy.fk.config.data.WaitingRoom;
import ch.skyfy.fk.json.Validatable;
import lombok.Getter;

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

        // TODO Implement validation
        // verify dayOfAuthorizationOfTheAssaults
        if (dayOfAuthorizationOfThePvP < 0) {
            errors.add("La propriété dayOfAuthorizationOfThePvP contient une valeur trop petite !");
        }

        confirmValidate(errors);
    }
}
