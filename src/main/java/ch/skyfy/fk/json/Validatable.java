package ch.skyfy.fk.json;

import ch.skyfy.fk.FKMod;

import java.util.List;

public interface Validatable {
    void validate();

    default void confirmValidate(List<String> errors){
        if (errors.size() != 0) {
            FKMod.LOGGER.fatal("Some .json file are not valid");
            errors.forEach(FKMod.LOGGER::fatal);
            throw new RuntimeException();
        }
    }

}
