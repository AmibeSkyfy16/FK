package ch.skyfy.fk.constants;

import net.minecraft.util.Formatting;

public class WhereMsg extends MsgBase{

    public static WhereMsg IN_YOUR_OWN_BASE = new WhereMsg("You are in your own base", Formatting.DARK_PURPLE);
    public static WhereMsg CLOSE_TO_YOUR_OWN_BASE = new WhereMsg("You are close to your own base", Formatting.DARK_PURPLE);
    public static WhereMsg IN_AN_ENEMY_BASE = new WhereMsg("You are in an enemy base", Formatting.DARK_PURPLE);
    public static WhereMsg CLOSE_TO_AN_ENEMY_BASE = new WhereMsg("You are close to an enemy base", Formatting.DARK_PURPLE);
    public static WhereMsg IN_THE_WILD = new WhereMsg("You are in the wild", Formatting.DARK_PURPLE);
    public static WhereMsg UNKNOWN_LOCATION = new WhereMsg("Hummm this is strange, unknown location", Formatting.DARK_RED);

    protected WhereMsg(String text, Formatting formatting) {
        super(text, formatting);
    }
}
