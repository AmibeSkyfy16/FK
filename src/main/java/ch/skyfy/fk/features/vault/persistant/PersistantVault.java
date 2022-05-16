package ch.skyfy.fk.features.vault.persistant;

import ch.skyfy.fk.features.vault.VaultFeature;
import ch.skyfy.fk.json.Defaultable;
import ch.skyfy.fk.json.JsonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class contain static field
 * representing data that are saved in a json file.
 *
 * this class is loaded by reflection in a static block inside VaultFeature class.
 *
 * @see VaultFeature
 */
public class PersistantVault {

    public static final JsonData<PersistantVaultData, PersistantVaultsDefault> DATA = new JsonData<>("data\\vaults.json", PersistantVaultData.class, PersistantVaultsDefault.class);

    public static class PersistantVaultsDefault implements Defaultable<PersistantVaultData>{

        @Override
        public PersistantVaultData getDefault() {
            return new PersistantVaultData(new ArrayList<>(), new HashMap<>());
        }
    }

}
