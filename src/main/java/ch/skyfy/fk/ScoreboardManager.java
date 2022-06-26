package ch.skyfy.fk;

import ch.skyfy.fk.logic.GameUtils;
import ch.skyfy.fk.logic.persistant.PersistantFKGame;
import ch.skyfy.fk.logic.persistant.TimelineData;
import ch.skyfy.fk.sidebar.api.Sidebar;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager {

    private static class LazyHolder {
        static final ScoreboardManager INSTANCE = new ScoreboardManager();
    }

    public static ScoreboardManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    private final Map<String, Sidebar> sidebarMap;

    private ScoreboardManager() {
        sidebarMap = new HashMap<>();
    }

    public void initialize(TimelineData timelineData) {

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> sidebarMap.remove(handler.getPlayer().getUuidAsString()));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var player = handler.getPlayer();
            if (!sidebarMap.containsKey(player.getUuidAsString())) {
                var sb = new Sidebar(Sidebar.Priority.HIGH);
                sb.setTitle(Text.literal(">> Fallen Kingdoms <<").setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true)));
                sb.addPlayer(player);
                sb.setUpdateRate(20);
                sidebarMap.put(player.getUuidAsString(), sb);
            }

            updateSidebarImpl(player, timelineData.getDay(), timelineData.getMinutes(), timelineData.getSeconds());

            sidebarMap.get(player.getUuidAsString()).show();
        });
    }

    @SuppressWarnings("CommentedOutCode")
    public void updateSidebar(ServerPlayerEntity player, int day, int minutes, int seconds) {

//        if(!sidebarMap.containsKey(player.getUuidAsString())){
//            var sb = new Sidebar(Sidebar.Priority.HIGH);
//            sb.setTitle(new LiteralText(">> Fallen Kingdoms <<").setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true)));
//            sb.addPlayer(player);
//            sb.setUpdateRate(20);
//            sidebarMap.put(player.getUuidAsString(), sb);
//        }
//
        updateSidebarImpl(player, day, minutes, seconds);
//
//        sidebarMap.get(player.getUuidAsString()).show();


    }

    private void updateSidebarImpl(ServerPlayerEntity player, int day, int minutes, int seconds) {

        var sb = sidebarMap.get(player.getUuidAsString());
        if (sb == null) return;

        var list = new ArrayList<Text>();

        list.add(Text.literal("").setStyle(Style.EMPTY));
        list.add(Text.literal("Game Status: " + PersistantFKGame.FK_GAME_DATA.data.getGameState().name()).setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
        list.add(Text.literal("").setStyle(Style.EMPTY));

        var fkTeam = GameUtils.getFKTeamOfPlayerByName(player.getName().getString());
        var isFKPlayer = GameUtils.isFKPlayer(player.getName().getString());

        if (fkTeam != null) {
            if (isFKPlayer)
                list.add(Text.literal("Team name: " + fkTeam.getName()).setStyle(Style.EMPTY.withColor(Formatting.valueOf(fkTeam.getColor()))));
            else
                list.add(Text.literal("You have no team").setStyle(Style.EMPTY.withColor(Formatting.GOLD)));

            list.add(Text.literal("").setStyle(Style.EMPTY));
        }

        list.add(Text.literal("Day: %d".formatted(day)).setStyle(Style.EMPTY.withColor(Formatting.BLUE)));
        list.add(Text.literal("Time: %d:%s".formatted(minutes, seconds)).setStyle(Style.EMPTY.withColor(Formatting.BLUE)));
        list.add(Text.literal("").setStyle(Style.EMPTY));

        list.add(Text.literal("PvP: " + getSentence(GameUtils.isPvPEnabled(day))).setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)));
        list.add(Text.literal("Assault: " + getSentence(GameUtils.areAssaultEnabled(day))).setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)));
        list.add(Text.literal("Nether: " + getSentence(GameUtils.isNetherEnabled(day))).setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)));
        list.add(Text.literal("End: " + getSentence(GameUtils.isEndEnabled(day))).setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)));

        for (int i = list.size() - 1; i >= 0; i--)
            sb.setLine(i, list.get(list.size() - 1 - i));
    }

    private String getSentence(boolean bool) {
        return bool ? "Enabled" : "Disabled";
    }

}
