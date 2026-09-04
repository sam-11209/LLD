package restaurant.order;

import java.util.Objects;

/**
 * Concrete Command that transitions an OrderItem to DELIVERED.
 */
public class DeliverCommand implements OrderCommand {
    private final OrderItem orderItem;

    public DeliverCommand(OrderItem orderItem) {
        this.orderItem = Objects.requireNonNull(orderItem, "OrderItem must not be null");
    }

    @Override
    public void execute() {
        orderItem.deliverToCustomer();
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }
}
