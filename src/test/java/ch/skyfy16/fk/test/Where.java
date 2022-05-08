package ch.skyfy16.fk.test;

import javax.annotation.Nullable;

public enum Where {
    INSIDE_HIS_OWN_BASE,
    INSIDE_AN_ENEMY_BASE,
    CLOSE_TO_HIS_OWN_BASE,
    CLOSE_TO_AN_ENEMY_BASE,
    INSIDE_THE_VAULT_OF_AN_ENEMY_BASE,
    INSIDE_THE_VAULT_OF_HIS_OWN_BASE,
    IN_THE_WILD;

    @Nullable private Where nested;

    Where() {
        nested = null;
    }

    public void withNested(Where where){
        nested = where;
    }

}
