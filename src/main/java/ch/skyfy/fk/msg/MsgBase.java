package ch.skyfy.fk.msg;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public abstract class MsgBase {

    protected String s;
    protected final Formatting formatting;

    protected MsgBase(String text, Formatting formatting) {
        this.s = text;
        this.formatting = formatting;
    }

    public MsgBase formatted(Object... args) {
        s = s.formatted(args);
        return this;
    }

    public void send(PlayerEntity player) {
        player.sendMessage(text(), false);
    }

    public <T> T send(PlayerEntity player, T defaultReturnValue) {
        send(player);
        return defaultReturnValue;
    }

    public void broadcast(PlayerManager playerManager) {
        playerManager.broadcast(text(), MessageType.CHAT);
    }

    public Text text() {
        return Text.literal(s).setStyle(Style.EMPTY.withColor(formatting));
    }
}
