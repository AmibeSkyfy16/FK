package ch.skyfy.fk.features.data;

import ch.skyfy.fk.json.Validatable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class Vaults implements Validatable {

    @Getter
    private final List<Vault> vaults;

    public Vaults( List<Vault> vaults) {
        this.vaults = vaults;
    }

    @Override
    public void validate() {
        var errors = new ArrayList<String>();

        confirmValidate(errors);
    }

}
