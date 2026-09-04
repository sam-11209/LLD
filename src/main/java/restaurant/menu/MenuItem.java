package restaurant.menu;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * An individual item on the restaurant menu.
 * Fully immutable — all fields are final and no mutating methods exist,
 * making MenuItem instances inherently thread-safe to share across threads.
 */
public final class MenuItem {
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final Category category;

    public MenuItem(String name, String description, BigDecimal price, Category category) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");
        this.price = Objects.requireNonNull(price, "price must not be null");
        this.category = Objects.requireNonNull(category, "category must not be null");
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("price cannot be negative: " + price);
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItem menuItem)) return false;
        return Objects.equals(name, menuItem.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category=" + category +
                '}';
    }
}
