package atm.hardware;

/**
 * Interface abstracting the ATM screen display.
 */
public interface Display {

    /**
     * Displays a text message or prompt to the user.
     *
     * @param message notification or instructions to display
     */
    void showMessage(String message);
}
