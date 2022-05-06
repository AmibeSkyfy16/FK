package ch.skyfy.fk.json;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.exception.FKModException;

import java.util.List;

public interface Validatable {

    /**
     * A typo or a mistake can happen quickly.
     * If this is what happened when the user was setting up the files it could have bad repercussions on the game
     *
     * This should not happen at all
     */
    void validate();

    default void confirmValidate(List<String> errors){
        if (errors.size() != 0) {
            FKMod.LOGGER.fatal("Some .json file are not valid\n\r\n\r");
            errors.forEach(FKMod.LOGGER::fatal);
            FKMod.LOGGER.fatal("FKMod WILL NOW CRASH THE SERVER\n\r\n\r");
            throw new FKModException();
        }
    }

}
