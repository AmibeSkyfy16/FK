package ch.skyfy.fk.config;

import ch.skyfy.fk.config.data.Cube;
import ch.skyfy.fk.config.data.FKTeam;
import ch.skyfy.fk.json.Validatable;
import ch.skyfy.fk.utils.MathUtils;
import ch.skyfy.fk.utils.ValidateUtils;
import de.saibotk.jmaw.ApiResponseException;
import de.saibotk.jmaw.MojangAPI;
import lombok.Getter;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

        // check for negative value
        teams.stream().map(FKTeam::getBase).forEach(base -> {
            ValidateUtils.checkForNegativeValueInCubeClass(base.getCube(), errors);
            ValidateUtils.checkForNegativeValueInCubeClass(base.getProximityCube(), errors);
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
            for (var fkTeam : teams) {
                var base2 = fkTeam.getBase();
                if (base2 == base1) continue;

                if(MathUtils.intersect(base1.getCube(), base2.getCube())){
                    errors.add("Base " + base1.getName() + " intersect base " + base2.getName());
                }
                if(MathUtils.intersect(base1.getProximityCube(), base2.getProximityCube())){
                    errors.add("Base proximity " + base1.getName() + " intersect base proximity " + base2.getName());
                }
                if(MathUtils.isInside(base1.getProximityCube(), base2.getProximityCube())){
                    errors.add("Base " + base2.getName() + " is inside base " + base1.getName());
                }
                if(MathUtils.isInside(base1.getCube(), base2.getCube())){
                    errors.add("Base proximity " + base2.getName() + " is inside base proximity " + base1.getName());
                }

                var waitingRoom = Configs.FK_CONFIG.config.getWaitingRoom();

            }
        }

        // Check if the base is inside the world border
        var worldBorderCube = Configs.WORLD_CONFIG.config.getWorldBorderData().getCube();
        for (var team : teams) {
            if(!MathUtils.isInside(worldBorderCube, team.getBase().getProximityCube())){
                errors.add("the base " + team.getBase().getName() +" is not inside the world border !");
            }
        }

        confirmValidate(errors);
    }
}
