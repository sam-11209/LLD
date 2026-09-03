package atm.hardware;

/**
 * In-memory implementation of {@link CardProcessor}.
 */
public class StandardCardProcessor implements CardProcessor {

    private String currentCardNumber;

    @Override
    public void insertCard(String cardNumber) {
        this.currentCardNumber = cardNumber;
    }

    @Override
    public String getCardNumber() {
        return currentCardNumber;
    }

    @Override
    public void ejectCard() {
        this.currentCardNumber = null;
    }

    @Override
    public void retainCard() {
        this.currentCardNumber = null;
    }

    @Override
    public boolean hasCard() {
        return currentCardNumber != null;
    }
}
