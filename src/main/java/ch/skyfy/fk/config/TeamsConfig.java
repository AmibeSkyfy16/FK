package ch.skyfy.fk.config;

import ch.skyfy.fk.config.data.FKTeam;
import ch.skyfy.fk.json.Validatable;
import ch.skyfy.fk.utils.MathUtils;
import ch.skyfy.fk.utils.ValidateUtils;
import de.saibotk.jmaw.ApiResponseException;
import de.saibotk.jmaw.MojangAPI;
import lombok.Getter;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class TeamsConfig implements Validatable {

    @Getter
    private final List<FKTeam> teams;

    public TeamsConfig(List<FKTeam> teams) {
        this.teams = teams;
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();

        validateNonNull(errors);
        validatePrimitivesType(errors);

        var teamNames = teams.stream().map(FKTeam::getPlayers).toList();
        if (!teamNames.stream().filter(name -> Collections.frequency(teamNames, name) > 1).toList().isEmpty())
            errors.add("There are two or more teams with the same name !");

        var teamColors = teams.stream().map(FKTeam::getColor).toList();
        if (!teamColors.stream().filter(name -> Collections.frequency(teamColors, name) > 1).toList().isEmpty())
            errors.add("There are two or more teams with the same color !");

        // Check if a team name is empty
        teams.forEach(fkTeam -> {
            if (fkTeam.getName().isEmpty()) errors.add("A team name cannot be empty !");
        });

        // Check if a base name is empty
        teams.stream().map(FKTeam::getBase).forEach(base -> {
            if (base.getName().isEmpty()) errors.add("A base name cannot be empty !");
        });

        // Check if players are existing player on the mojang api
        teams.stream().flatMap(fkTeam -> fkTeam.getPlayers().stream()).forEach(playerName -> {
            try {
                new MojangAPI().getUUIDInfo(playerName);
            } catch (ApiResponseException e) {
                if (e.getStatusCode() == 400) {
                    errors.add("Player " + playerName + " does not exist");
                    errors.add("An error occurred while checking the player name : " + playerName);
                }
            }
        });

        // Check if colors are ok
        teams.stream().map(FKTeam::getColor).forEach(color -> {
            try {
                Formatting.valueOf(color);
            } catch (IllegalArgumentException e) {
                errors.add("color " + color + " is not a valid colors ! pls refer to the documentation !");
            }
        });

        /*
         * A base with coordinates close to another one could break the game.
         *
         * this must not happen
         *
         * Here, each base is checked with all the others to see if they overlap, or if one is inside another
         *
         * Also check for the waiting room
         */
        for (var team : teams) {
            var base1 = team.getBase();

            // Check is the dimensionName field is correct
            if (!Identifier.isValid(base1.getSpawnLocation().getDimensionName())) {
                errors.add("dimensionName " + base1.getSpawnLocation().getDimensionName() + " is not a valid dimension name");
            }

            for (var fkTeam : teams) {
                var base2 = fkTeam.getBase();
                if (base2 == base1) continue;

                if (MathUtils.intersect(base1.getCube(), base2.getCube())) {
                    errors.add("Base " + base1.getName() + " intersect base " + base2.getName());
                }
                if (MathUtils.intersect(base1.getProximityCube(), base2.getProximityCube())) {
                    errors.add("Base proximity " + base1.getName() + " intersect base proximity " + base2.getName());
                }
                if (MathUtils.isInside(base1.getProximityCube(), base2.getProximityCube())) {
                    errors.add("Base " + base2.getName() + " is inside base " + base1.getName());
                }
                if (MathUtils.isInside(base1.getCube(), base2.getCube())) {
                    errors.add("Base proximity " + base2.getName() + " is inside base proximity " + base1.getName());
                }

                var waitingRoom = Configs.FK_CONFIG.data.getWaitingRoom();
                if (MathUtils.intersect(base1.getCube(), waitingRoom.getCube())) {
                    errors.add("Base " + base1.getName() + " intersect waiting room ");
                }
                if (MathUtils.isInside(base1.getCube(), waitingRoom.getCube())) {
                    errors.add("Base " + base1.getName() + " is inside waiting room ");
                }
            }
        }

        // Check if the base is inside the world border
        for (var team : teams){
            var base = team.getBase();
            Configs.WORLD_CONFIG.data.getWorldBorderData().getSpawns().entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().equals(base.getSpawnLocation().getDimensionName()))
                    .findFirst()
                    .ifPresent(entry -> {
                        if (!MathUtils.isInside(entry.getValue(), base.getProximityCube()))
                            errors.add("the base " + base.getName() + " is not inside the world border !");
                    });
        }

        confirmValidate(errors);
    }

    @Override
    public void validatePrimitivesType(List<String> errors) {
        // check for negative value
        teams.stream().map(FKTeam::getBase).forEach(base -> {
            ValidateUtils.checkForNegativeValueInCubeClass(base.getCube(), errors);
            ValidateUtils.checkForNegativeValueInCubeClass(base.getProximityCube(), errors);
        });
    }
}
