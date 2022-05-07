package ch.skyfy.fk;

import ch.skyfy.fk.logic.GameUtils;
import ch.skyfy.fk.logic.data.FKGameAllData;
import ch.skyfy.fk.sidebar.api.Sidebar;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;

@SuppressWarnings("ConstantConditions")
public class ScoreboardManager {

    private static class LazyHolder {
        static final ScoreboardManager INSTANCE = new ScoreboardManager();
    }

    public static ScoreboardManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    private final Sidebar generalSidebar;

    private ScoreboardManager() {
        generalSidebar = new Sidebar(Sidebar.Priority.MEDIUM);
        generalSidebar.setTitle(new LiteralText(">> Fallen Kingdoms <<").setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true)));
    }

    public void updateSidebar(ServerPlayerEntity player, int day, int minutes, int seconds) {
        updateSidebarImpl(player, day, minutes, seconds);

        if (generalSidebar.getPlayerHandlerSet().stream().noneMatch(serverPlayNetworkHandler -> serverPlayNetworkHandler.player.equals(player)))
            generalSidebar.addPlayer(player);

        generalSidebar.show();
    }

    private void updateSidebarImpl(ServerPlayerEntity player, int day, int minutes, int seconds) {

        var list = new ArrayList<Text>();

        list.add(new LiteralText("").setStyle(Style.EMPTY));
        list.add(new LiteralText("Game Status: " + FKGameAllData.FK_GAME_DATA.data.getGameState().name()).setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
        list.add(new LiteralText("").setStyle(Style.EMPTY));

        var fkTeam = GameUtils.getFKTeamOfPlayerByName(player.getName().asString());

        if(fkTeam != null){
            list.add(new LiteralText("Team name: " + GameUtils.getFKTeamOfPlayerByName(player.getName().asString()).getName()).setStyle(Style.EMPTY.withColor(Formatting.valueOf(fkTeam.getColor()))));
            list.add(new LiteralText("").setStyle(Style.EMPTY));
        }

        list.add(new LiteralText("Day: %d".formatted(day)).setStyle(Style.EMPTY.withColor(Formatting.BLUE)));
        list.add(new LiteralText("Time: %d:%s".formatted(minutes, seconds)).setStyle(Style.EMPTY.withColor(Formatting.BLUE)));
        list.add(new LiteralText("").setStyle(Style.EMPTY));

        list.add(new LiteralText("PvP: " + getSentence(GameUtils.isPvPEnabled(day))).setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)));
        list.add(new LiteralText("Assault: " + getSentence(GameUtils.areAssaultEnabled(day))).setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)));
        list.add(new LiteralText("Nether: " + getSentence(GameUtils.isNetherEnabled(day))).setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)));
        list.add(new LiteralText("End: " + getSentence(GameUtils.isEndEnabled(day))).setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)));

        for (int i = list.size() - 1; i >= 0; i--)
            generalSidebar.setLine(i, list.get(list.size() - 1 -i));
    }

    private String getSentence(boolean bool){
        return bool ? "Enabled" : "Disabled";
    }

}
