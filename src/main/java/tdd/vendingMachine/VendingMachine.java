package tdd.vendingMachine;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;
import tdd.vendingMachine.io.Display;
import tdd.vendingMachine.io.Keyboard;
import tdd.vendingMachine.state.VendingMachineState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Accessors(chain = true)
@RequiredArgsConstructor
public class VendingMachine {

    private final int shelfCount;

    @Delegate
    private final Keyboard keyboard;

    @Delegate
    private final Display display;

    @Delegate
    private CoinWallet coinWallet = new CoinWallet();

    @Setter
    private VendingMachineState state;

    private Map<Integer, ProductStack> productMap = new HashMap<>();

    public void start(VendingMachineState state) {
        setState(state).proceed();
    }

    public void proceed() {
        state.proceed(this);
    }

    public VendingMachine putProductsOnShelf(int shelfNumber, Product product, int count) {
        if (!correctShelfNumber(shelfNumber)) {
            throw new IllegalArgumentException("Incorrect shelf number: " + shelfNumber);
        }

        productMap.put(shelfNumber, ProductStack.of(product, count));
        return this;
    }

    public void popProduct(int shelfNumber) {
        productMap.get(shelfNumber).pop();
    }

    public Optional<Product> getProductInfo(int shelfNumber) {
        if (productMap.containsKey(shelfNumber)) {
            return Optional.of(productMap.get(shelfNumber).getProduct());
        }
        return Optional.empty();
    }

    public int selectProductShelf() {
        final int selectedShelfNumber = readNumber();
        if (!correctShelfNumber(selectedShelfNumber)) {
            display("Incorrect shelf number! Try again: ");
            return selectProductShelf();
        }

        if (!productMap.containsKey(selectedShelfNumber) || productMap.get(selectedShelfNumber).empty()) {
            display("Product out of stock, select other product: ");
            return selectProductShelf();
        }

        return selectedShelfNumber;
    }

    public void displayProducts() {
        display("Money in machine: %s PLN\n", coinWallet.calculator().getCoinsValue());
        for (int i = 1; i <= shelfCount; i++) {
            if (productMap.containsKey(i) && productMap.get(i).size() > 0) {
                ProductStack productStack = productMap.get(i);
                display("%s -> %s (%s PLN) x%s\n", i, productStack.getName(), productStack.getPrice(), productStack.size());
            } else {
                display("%s -> empty\n", i);
            }
        }
    }

    public void display(String messageTemplate, Object... args) {
        display(String.format(messageTemplate, args));
    }

    private boolean correctShelfNumber(int number) {
        return number >= 1 && number <= shelfCount;
    }
}
