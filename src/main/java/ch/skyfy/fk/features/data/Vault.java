package ch.skyfy.fk.features.data;

import lombok.Getter;
import lombok.Setter;

public class Vault {

    @Getter @Setter
    private boolean valid;

    @Getter @Setter
    private String teamId;

    @Getter @Setter
    private BlockPos[] blockPos;

    public Vault(boolean valid, String teamId, BlockPos[] blockPos) {
        this.valid = valid;
        this.teamId = teamId;
        this.blockPos = blockPos;
    }
}
