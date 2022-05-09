package ch.skyfy.fk.features.data;

import ch.skyfy.fk.json.Defaultable;
import ch.skyfy.fk.json.JsonDataClass;

import java.util.ArrayList;

public class VaultConstant {

    public static final JsonDataClass<Vaults, VaultsDefault> VAULTS = new JsonDataClass<>(
            "data\\vaults.json",
            Vaults.class, VaultsDefault.class);

    private static class VaultsDefault implements Defaultable<Vaults>{

        @Override
        public Vaults getDefault() {
            return new Vaults(new ArrayList<>());
        }
    }

}
