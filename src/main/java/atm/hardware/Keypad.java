package atm.hardware;

/**
 * Interface abstracting user input via physical keypad or touchscreen.
 */
public interface Keypad {

    /**
     * Reads a line or sequence of characters entered by the user.
     *
     * @return user input string
     */
    String getInput();
}
