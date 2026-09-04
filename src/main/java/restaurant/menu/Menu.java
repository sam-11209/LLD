package restaurant.menu;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores the restaurant's available MenuItems, keyed by item name for fast O(1) lookup.
 *
 * Concurrency Fix (Section 9.4):
 * Backed by ConcurrentHashMap instead of a standard HashMap. This ensures that new menu
 * items can be added dynamically while concurrent threads (servers taking orders, kitchen
 * viewing items) are reading from the menu without causing race conditions or
 * ConcurrentModificationExceptions.
 */
public class Menu {
    private final Map<String, MenuItem> menuItems = new ConcurrentHashMap<>();

    public void addItem(MenuItem item) {
        Objects.requireNonNull(item, "MenuItem must not be null");
        menuItems.put(item.getName(), item);
    }

    public MenuItem getItem(String name) {
        Objects.requireNonNull(name, "Item name must not be null");
        return menuItems.get(name);
    }

    public Map<String, MenuItem> getMenuItems() {
        return Collections.unmodifiableMap(menuItems);
    }
}
