package restaurant.order;

import java.util.Objects;

/**
 * Concrete Command that transitions an OrderItem to CANCELED.
 */
public class CancelCommand implements OrderCommand {
    private final OrderItem orderItem;

    public CancelCommand(OrderItem orderItem) {
        this.orderItem = Objects.requireNonNull(orderItem, "OrderItem must not be null");
    }

    @Override
    public void execute() {
        orderItem.cancel();
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }
}
