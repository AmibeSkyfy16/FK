package ch.skyfy.fk.config;

import ch.skyfy.fk.config.data.FKTeam;
import ch.skyfy.fk.json.Validatable;
import de.saibotk.jmaw.ApiResponseException;
import de.saibotk.jmaw.MojangAPI;
import lombok.Getter;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        // Check if players are existing player on the mojang api
        teams.stream().flatMap(fkTeam -> fkTeam.getPlayers().stream()).forEach(playerName -> {
            try {
                new MojangAPI().getUUIDInfo(playerName);
            } catch (ApiResponseException e) {
                if (e.getStatusCode() == 400) {
                    errors.add("Player " + playerName + " does not exist");
                }
                errors.add("An error occurred while checking the player name : " + playerName);
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

        // Check that no bases overlap
        for (FKTeam team : teams) {
            var baseToCheck = team.getBase();
            for (FKTeam fkTeam : teams) {
                if (fkTeam.getBase() == baseToCheck) {
                    System.out.println("skip");
                    continue;
                }
                var x1 = baseToCheck.getCube().getX();
                var z1 = baseToCheck.getCube().getZ();
                var w1 = baseToCheck.getCube().getSize() * 2;
                var h1 = baseToCheck.getCube().getSize() * 2;

                var x2 = fkTeam.getBase().getCube().getX();
                var z2 = fkTeam.getBase().getCube().getZ();
                var w2 = fkTeam.getBase().getCube().getSize() * 2;
                var h2 = fkTeam.getBase().getCube().getSize() * 2;

                var intersect = true;
                if(x1 + (w1 / 2d) < x2 - (w2 / 2d)){
                    intersect = false;
                }
                if(x1 - (w1 / 2d) > x2 + (w2 / 2d)){
                    intersect = false;
                }
                if(z1 + (h1 / 2d) < z2 - (h2 / 2d)){
                    intersect = false;
                }
                if(z1 - (h1 / 2d) > z2 + (h2 / 2d)){
                    intersect = false;
                }

                if(intersect){
                    errors.add("Base " + baseToCheck.getName()  + " intersect " + fkTeam.getBase().getName());
                }

            }
        }

        confirmValidate(errors);
    }
}
