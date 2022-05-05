package ch.skyfy.fk.config;

import ch.skyfy.fk.config.data.Base;
import ch.skyfy.fk.config.data.Cube;
import ch.skyfy.fk.config.data.FKTeam;
import ch.skyfy.fk.json.Validatable;
import lombok.Getter;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.util.Formatting;
import org.shanerx.mojang.Mojang;

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

        Mojang api = new Mojang().connect();

        for (var team : teams) {
            for (String player : team.getPlayers()) {
                var uuid = api.getUUIDOfUsername(player);
                System.out.println("uuid: " + uuid);
            }
        }

        throw new RuntimeException("ITS NORMAL");
//        confirmValidate(errors);
    }
}
