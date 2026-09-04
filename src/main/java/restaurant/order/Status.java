package restaurant.order;

/**
 * Represents the lifecycle status of an individual OrderItem.
 */
public enum Status {
    PENDING,
    SENT_TO_KITCHEN,
    DELIVERED,
    CANCELED
}
