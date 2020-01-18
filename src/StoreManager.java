import Exceptions.StoreException;
import Prog1Tools.IOTools;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class StoreManager {
    Map<Integer, Product> productMap = new HashMap<>();
    Map<Integer, Integer> stock = new HashMap<>();

    public void pick(Basket basket) {
        AtomicBoolean isExceptionToThrow = new AtomicBoolean(false);
        basket.requestedProducts.forEach((product, amount) -> {
            int tmp = this.stock.get(product.getID());
            this.stock.replace(product.getID(), tmp - amount);
            if(this.stock.get(product.getID()) < 0) isExceptionToThrow.set(true);
        });
        try {
            if(isExceptionToThrow.get()) {
                throw new StoreException("Products not sufficiently stocked!");
            }
        } catch (StoreException e) {
            System.out.println(e.getMessage());
            basket.requestedProducts.forEach((product, amount) -> {
                int tmp = this.stock.get(product.getID());
                this.stock.replace(product.getID(), tmp + amount);
            });
        }
        basket.requestedProducts.clear();
    }

    public void addProduct(Product product, int amount) {
        this.productMap.putIfAbsent(product.getID(), product);
        if(this.stock.containsKey(product.getID())) {
            int tmp = this.stock.get(product.getID());
            this.stock.replace(product.getID(), tmp + amount);
        } else{
            this.stock.put(product.getID(), amount);
        }
    }

    public static void main(String[] args) {
        StoreManager storeManager = new StoreManager();
        for (int i = 0; i < 5; i++) {
            storeManager.addProduct(new Product("randomProduct", 1 + (int)(Math.random()*10)), (1 + (int)(Math.random()*10)));
        }
        Basket basket = new Basket(storeManager.productMap, storeManager.stock);
        basket.printProducts();
        do {
            basket.fillBasket();
            storeManager.pick(basket);
            basket.printProducts();
        } while (!IOTools.readString("Buy more? (y/n) ").toLowerCase().equals("n"));
        System.out.println("Bye!");
    }
}
