package ch.skyfy.fk.config;

import ch.skyfy.fk.config.data.Base;
import ch.skyfy.fk.config.data.Cube;
import ch.skyfy.fk.config.data.FKTeam;
import ch.skyfy.fk.json.Validatable;
import lombok.Getter;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeamsConfig implements Validatable {

    @Getter
    private final List<FKTeam> teams;

    public TeamsConfig(List<FKTeam> teams) {
        this.teams = teams;
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();


        confirmValidate(errors);
    }
}
