package tdd.vendingMachine.state;

import tdd.vendingMachine.VendingMachine;

/**
 * @author Mateusz Urbański <matek2305@gmail.com>
 */
public class ProductSelectState implements VendingMachineState {

    @Override
    public void proceed(VendingMachine vendingMachine) {
        vendingMachine.displayProducts();
        vendingMachine.display("Select product: ");

        int selectedShelfNumber = vendingMachine.selectProductShelf();
        vendingMachine.getProductInfo(selectedShelfNumber)
            .ifPresent(p -> vendingMachine.setState(new CoinsInsertState(p)));
        vendingMachine.proceed();
    }
}