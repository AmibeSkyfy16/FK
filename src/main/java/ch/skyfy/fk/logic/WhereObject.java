package ch.skyfy.fk.logic;

import ch.skyfy.fk.constants.Where;
import lombok.Getter;

import javax.annotation.Nullable;

public class WhereObject {

    @Getter
    private final Where root;

    @Getter
    @Nullable
    private Where nested;

    WhereObject(Where root) {
        this.root = root;
        nested = null;
    }

    public void withNested(Where where){
        nested = where;
    }

}
