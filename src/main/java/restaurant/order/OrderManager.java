package restaurant.order;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Invoker in the Command Pattern.
 * Queues OrderCommands and executes them, decoupling Table and Restaurant from directly
 * mutating OrderItem state.
 *
 * Concurrency Fix (Section 9.6):
 * Backed by ConcurrentLinkedQueue instead of a non-thread-safe List (e.g. ArrayList).
 * Rather than a vulnerable loop-then-clear pattern (which would silently drop commands added
 * by another thread between the loop termination and clear()), this invoker uses atomic
 * drain semantics via poll(). Each poll() atomically retrieves and removes the queue's head.
 * Concurrently added commands will either be processed in the current loop or picked up
 * safely in subsequent executions with zero command loss or double execution.
 */
public class OrderManager {
    private final Queue<OrderCommand> commandQueue = new ConcurrentLinkedQueue<>();

    /**
     * Enqueues an order command to be executed.
     *
     * @param command the OrderCommand to add
     */
    public void addCommand(OrderCommand command) {
        Objects.requireNonNull(command, "OrderCommand must not be null");
        commandQueue.add(command);
    }

    /**
     * Runs every queued command in FIFO order using atomic polling, clearing the queue safely.
     */
    public void executeCommands() {
        OrderCommand command;
        while ((command = commandQueue.poll()) != null) {
            command.execute();
        }
    }

    /**
     * Returns true if there are no pending commands in the queue.
     */
    public boolean isQueueEmpty() {
        return commandQueue.isEmpty();
    }
}
