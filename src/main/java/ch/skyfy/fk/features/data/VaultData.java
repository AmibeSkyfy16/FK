package ch.skyfy.fk.features.data;

import ch.skyfy.fk.config.data.FKTeam;
import ch.skyfy.fk.json.Validatable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ClassCanBeRecord")
public class VaultData implements Validatable {

    @Getter
    private final List<Vault> vaults;

    @Getter
    private final Map<FKTeam, FKTeam> eliminatedTeams;

    public VaultData(List<Vault> vaults, Map<FKTeam, FKTeam> eliminatedTeams) {
        this.vaults = vaults;
        this.eliminatedTeams = eliminatedTeams;
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();

        confirmValidate(errors);
    }

    @Override
    public void validatePrimitivesType(List<String> errors) {
        // Nothing to do here
    }

}
