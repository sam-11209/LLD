package restaurant.order;

import restaurant.menu.MenuItem;
import java.util.Objects;

/**
 * One ordered instance of a MenuItem, tracking its own lifecycle Status.
 *
 * Concurrency Fix (Section 9.5):
 * The status field is declared volatile. OrderCommands (such as kitchen dispatch, delivery,
 * or cancellation) may execute on worker or background threads different from the thread
 * reading the item's status for billing or kitchen display.
 * The volatile keyword guarantees memory visibility across threads, and reference writes to
 * enums are atomic in Java, preventing stale reads without unnecessary synchronization overhead.
 */
public class OrderItem {
    private final MenuItem item;
    private volatile Status status;

    public OrderItem(MenuItem item) {
        this.item = Objects.requireNonNull(item, "MenuItem must not be null");
        this.status = Status.PENDING;
    }

    public MenuItem getItem() {
        return item;
    }

    public Status getStatus() {
        return status;
    }

    /**
     * Transition from PENDING -> SENT_TO_KITCHEN.
     */
    public synchronized void sendToKitchen() {
        if (this.status == Status.PENDING) {
            this.status = Status.SENT_TO_KITCHEN;
        }
    }

    /**
     * Transition from SENT_TO_KITCHEN -> DELIVERED.
     */
    public synchronized void deliverToCustomer() {
        if (this.status == Status.SENT_TO_KITCHEN) {
            this.status = Status.DELIVERED;
        }
    }

    /**
     * Cancels the item if it has not yet been delivered to the customer.
     */
    public synchronized void cancel() {
        if (this.status != Status.DELIVERED) {
            this.status = Status.CANCELED;
        }
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "item=" + item.getName() +
                ", status=" + status +
                '}';
    }
}
