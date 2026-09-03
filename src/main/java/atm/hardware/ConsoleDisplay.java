package atm.hardware;

/**
 * Standard implementation of {@link Display} that writes messages to stdout.
 */
public class ConsoleDisplay implements Display {

    @Override
    public void showMessage(String message) {
        System.out.println("[ATM DISPLAY]: " + message);
    }
}
