package ch.skyfy.fk.features.data;

import ch.skyfy.fk.json.Defaultable;
import ch.skyfy.fk.json.JsonDataClass;

import java.util.ArrayList;
import java.util.HashMap;

public class VaultConstant {

    public static final JsonDataClass<VaultData, VaultsDefault> DATA = new JsonDataClass<>(
            "data\\vaults.json",
            VaultData.class, VaultsDefault.class);

    public static class VaultsDefault implements Defaultable<VaultData>{

        @Override
        public VaultData getDefault() {
            return new VaultData(new ArrayList<>(), new HashMap<>());
        }
    }

}
