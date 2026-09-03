package atm.hardware;

/**
 * Standard implementation of {@link Keypad}.
 */
public class StandardKeypad implements Keypad {

    private String simulatedInput = "";

    public void setSimulatedInput(String simulatedInput) {
        this.simulatedInput = simulatedInput;
    }

    @Override
    public String getInput() {
        return simulatedInput;
    }
}
