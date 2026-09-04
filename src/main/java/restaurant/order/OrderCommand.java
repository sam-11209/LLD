package restaurant.order;

/**
 * Command interface encapsulating an action performed on an OrderItem.
 * Part of the Command Pattern: decouples the invoker (OrderManager / Restaurant)
 * from the receiver (OrderItem) and allows queuing, sequential execution,
 * and decoupling of order progression actions.
 */
public interface OrderCommand {
    void execute();
}
