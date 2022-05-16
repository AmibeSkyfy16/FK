package ch.skyfy.fk.features.vault.persistant;

import ch.skyfy.fk.data.BlockPos;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the location of a vault for a given team
 *
 * @see PersistantVaultData
 * @see PersistantVault
 */
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
