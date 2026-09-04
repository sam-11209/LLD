package restaurant.order;

import java.util.Objects;

/**
 * Concrete Command that transitions an OrderItem to SENT_TO_KITCHEN.
 */
public class SendToKitchenCommand implements OrderCommand {
    private final OrderItem orderItem;

    public SendToKitchenCommand(OrderItem orderItem) {
        this.orderItem = Objects.requireNonNull(orderItem, "OrderItem must not be null");
    }

    @Override
    public void execute() {
        orderItem.sendToKitchen();
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }
}
