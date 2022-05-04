package ch.skyfy.fk.config;

import ch.skyfy.fk.config.data.Base;
import ch.skyfy.fk.config.data.Cube;
import ch.skyfy.fk.config.data.FKTeam;
import ch.skyfy.fk.json.Validable;
import lombok.Getter;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeamsConfig implements Validable {

    @Getter
    private final List<FKTeam> teams;

    public TeamsConfig() {
        teams = new ArrayList<>();
        teams.add(new FKTeam("The_Green_Team", Formatting.GREEN.name(), List.of("Skyfy16"), new Base("The_HADDA_BASE", new Cube((short) 9, 30, 500, 20, -34, 20))));
        teams.add(new FKTeam("The_Red_Team", Formatting.RED.name(), List.of("AmibeSkyfy16"), new Base("The_DRIDROU_BASE", new Cube((short) 9, 30, 500, -20, -34, -20))));
    }

    @Override
    public List<String> validate() {
        return Collections.emptyList();
    }
}
