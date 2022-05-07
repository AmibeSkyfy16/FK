package ch.skyfy.fk.constants;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
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

    public Text text() {
        return new LiteralText(s).setStyle(Style.EMPTY.withColor(formatting));
    }
}
