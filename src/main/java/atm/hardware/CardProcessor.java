package atm.hardware;

/**
 * Interface abstracting the physical Card Reader/Processor hardware.
 */
public interface CardProcessor {

    /**
     * Captures and retains a reference to the card number upon insertion.
     */
    void insertCard(String cardNumber);

    /**
     * Reads the current card number held in the card reader.
     *
     * @return card number or {@code null} if no card is inserted
     */
    String getCardNumber();

    /**
     * Ejects the card back to the customer.
     */
    void ejectCard();

    /**
     * Retains (swallows) the card in the machine for security purposes.
     */
    void retainCard();

    /**
     * Checks if a card is currently inserted in the reader.
     */
    boolean hasCard();
}
