package ch.skyfy.fk.json;

import ch.skyfy.fk.FKMod;
import ch.skyfy.fk.exceptions.FKModException;
import ch.skyfy.fk.utils.ValidateUtils;

import java.util.List;

public interface Validatable {

    /**
     * A typo or a mistake can happen quickly.
     * If this is what happened when the user was setting up the files it could have bad repercussions on the game
     *
     * This should not happen at all
     */
    void validate();

    /**
     * Check that the primitive type values are correct.
     * Ex: dayOfAuthorizationOfThePvP cannot be under 0
     */
    void validatePrimitivesType(List<String> errors);

    /**
     * Performs a thorough check of all fields in a class to make sure nothing is null
     * This method must be called at the very beginning of validate
     */
    default void validateNonNull(List<String> errors){
        try {
            ValidateUtils.traverse(this, errors);
        } catch (NullPointerException e) {
            errors.forEach(FKMod.LOGGER::fatal);
            FKMod.LOGGER.fatal("""
                    Une valeur a été NULL lors du chargement des fichiers de configurations.
                     Il se peut qu'il manque un champ dans un des fichiers .json.
                     Ou alors vous avez mis à jour le mod FKMod et cela pose des problèmes.
                     Faite un backup de vos config, puis supprimer tous les fichiers de config, puis redémarrer le serveur
                    """);
            throw new FKModException(e);
        }
    }

    default void confirmValidate(List<String> errors){
        if (errors.size() != 0) {
            FKMod.LOGGER.fatal("Some .json file are not valid\n\r\n\r");
            errors.forEach(FKMod.LOGGER::fatal);
            FKMod.LOGGER.fatal("FKMod WILL NOW CRASH THE SERVER\n\r\n\r");
            throw new FKModException();
        }
    }

}
