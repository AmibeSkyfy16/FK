package ch.skyfy.fk;

import ch.skyfy.fk.logic.GameUtils;
import ch.skyfy.fk.sidebar.api.Sidebar;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

public class ScoreboardManager {
//sdsd
    private static class LazyHolder {
        static final ScoreboardManager INSTANCE = new ScoreboardManager();
    }

    public static ScoreboardManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    private final Sidebar generalSidebar;

    private ScoreboardManager() {
        generalSidebar = new Sidebar(Sidebar.Priority.MEDIUM);
        generalSidebar.setTitle(new LiteralText("<< Fallen Kingdoms >>").setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true)));
    }

    public void updateSidebar(ServerPlayerEntity player, int day, int minutes, int seconds) {
        updateSidebar(day, minutes, seconds);

        if (generalSidebar.getPlayerHandlerSet().stream().noneMatch(serverPlayNetworkHandler -> serverPlayNetworkHandler.player.equals(player)))
            generalSidebar.addPlayer(player);

        generalSidebar.show();
    }

    private void updateSidebar(int day, int minutes, int seconds) {
        generalSidebar.setLine(11, new LiteralText("Day: %d".formatted(day)).setStyle(Style.EMPTY.withColor(Formatting.BLUE)));
        generalSidebar.setLine(10, new LiteralText("Time: %d:%s".formatted(minutes, seconds)).setStyle(Style.EMPTY.withColor(Formatting.BLUE)));

        generalSidebar.setLine(9, new LiteralText("PvP: " + getSentence(GameUtils.isPvPEnabled(day))).setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
        generalSidebar.setLine(6, new LiteralText("Assault: " + getSentence(GameUtils.areAssaultEnabled(day))).setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
        generalSidebar.setLine(8, new LiteralText("Nether: " + getSentence(GameUtils.isNetherEnabled(day))).setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
        generalSidebar.setLine(7, new LiteralText("End: " + getSentence(GameUtils.isEndEnabled(day))).setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
    }

    private String getSentence(boolean bool){
        return bool ? "Enabled" : "Disabled";
    }

}
