package ch.skyfy.fk.sidebar.interfaces;



import ch.skyfy.fk.sidebar.api.Sidebar;

import java.util.Set;

@SuppressWarnings("unused")
public interface SidebarHolder {
    void addSidebar(Sidebar sidebar);
    void removeSidebar(Sidebar sidebar);
    void clearSidebars();
    Set<Sidebar> getSidebarSet();
    Sidebar getCurrentSidebar();
    void updateCurrentSidebar(Sidebar candidate);
}
