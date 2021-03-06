package ch.skyfy.fk.features.vault.persistant;

import ch.skyfy.fk.json.Validatable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class contains data saved and loaded through a file called vault.json
 * @see PersistantVault
 */
@SuppressWarnings("ClassCanBeRecord")
public class PersistantVaultData implements Validatable {

    @Getter
    private final List<Vault> vaults;

    @Getter
    private final Map<String, String> eliminatedTeams;

    public PersistantVaultData(List<Vault> vaults, Map<String, String> eliminatedTeams) {
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
