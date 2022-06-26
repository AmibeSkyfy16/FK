package ch.skyfy.fk.sidebar.api.lines;

import ch.skyfy.fk.sidebar.api.Sidebar;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

/**
 * Immutable version of SidebarLine used for comparison of change.
 */
public record ImmutableSidebarLine(int value, Text text) implements SidebarLine {

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public boolean setValue(int value) {
        return false;
    }

    @Override
    public Text getText(ServerPlayNetworkHandler handler) {
        return this.text;
    }

    @Override
    public void setSidebar(@Nullable Sidebar sidebar) {}

    public boolean equals(Object o, ServerPlayNetworkHandler handler) {
        if (this == o) return true;
        if (!(o instanceof SidebarLine that)) return false;
        return this.value == that.getValue() && that.getText(handler).equals(this.text);
    }
}
