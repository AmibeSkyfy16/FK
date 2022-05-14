package ch.skyfy.fk.features.data;

import ch.skyfy.fk.json.Defaultable;
import ch.skyfy.fk.json.JsonDataClass;

import java.util.ArrayList;
import java.util.HashMap;

public class PersistantVault {

    public static final JsonDataClass<PersistantVaultData, PersistantVaultsDefault> DATA = new JsonDataClass<>(
            "data\\vaults.json",
            PersistantVaultData.class, PersistantVaultsDefault.class);

    public static class PersistantVaultsDefault implements Defaultable<PersistantVaultData>{

        @Override
        public PersistantVaultData getDefault() {
            return new PersistantVaultData(new ArrayList<>(), new HashMap<>());
        }
    }

}
