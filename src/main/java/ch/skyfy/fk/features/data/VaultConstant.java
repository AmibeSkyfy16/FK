package ch.skyfy.fk.features.data;

import ch.skyfy.fk.json.JsonDataClass;

import java.util.ArrayList;

public class VaultConstant {

    public static final JsonDataClass<Vaults> VAULTS = new JsonDataClass<>(
            "data\\vaults.json",
            Vaults.class, new Vaults(new ArrayList<>()));

}
