package ch.skyfy16.fk.test;

import ch.skyfy.fk.config.data.FKTeam;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class GameUtils {

    @FunctionalInterface
    public interface WhereIsThePlayer<T> {
        T impl(Where where);

    }

    public static <T> T whereIsThePlayer(List<FKTeam> fkTeams, String playerName, Vec3d blockPos, WhereIsThePlayer<T> whereIsThePlayer) {

        Where where = null;

        for (var team : fkTeams) {
            var baseCube = team.getBase().getCube();

            // Is this base the base of the player who break the block ?
            var isBaseOfPlayer = team.getPlayers().stream().anyMatch(playerName::equals);

            // If player is inside a base
            if (MathUtils.isAPosInsideCube(baseCube, blockPos)) {

                // And this base is not his own
                if (!isBaseOfPlayer)
                    where = Where.INSIDE_AN_ENEMY_BASE;
                else
                    where = Where.INSIDE_HIS_OWN_BASE;

            } else {
                if (MathUtils.isAPosInsideCube(team.getBase().getProximityCube(), blockPos)) {
                    if (isBaseOfPlayer) where = Where.CLOSE_TO_HIS_OWN_BASE;
                    else where = Where.CLOSE_TO_AN_ENEMY_BASE;
                }
            }

            if (where == null) where = Where.IN_THE_WILD;
        }

        return whereIsThePlayer.impl(where);
    }

}
