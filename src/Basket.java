import Exceptions.BasketExceptionNonRepeatable;
import Exceptions.BasketExceptionRepeatable;
import Exceptions.DiscountException;
import Prog1Tools.IOTools;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Basket {
    Map<Integer, Product> productMap;
    Map<Integer, Integer> stock;
    Map<Product, Integer> requestedProducts = new HashMap<>();
    Map<String, Double> discountCodes = new HashMap<>();

    public Basket(Map<Integer, Product> productMap, Map<Integer, Integer> stock) {
        this.productMap = productMap;
        this.stock = stock;
        setDiscountCodes();
    }

    private void setDiscountCodes() {
        this.discountCodes.put("SALE00",1.0);
        this.discountCodes.put("SALE10",0.9);
        this.discountCodes.put("SALE20",0.8);
        this.discountCodes.put("N",1.0);
    }

    public void fillBasket() {
        while(true) {
            int IDToBuy = IOTools.readInt("ID of product to buy: ");
            int amount = IOTools.readInt("How many? ");
            try {
                addProduct(IDToBuy, amount);
            } catch(BasketExceptionRepeatable e) {
                System.out.println(e.getMessage());
                continue;
            } catch(BasketExceptionNonRepeatable e) {
                System.out.println(e.getMessage());
            }
            if(IOTools.readString("End? (y/n) ").toLowerCase().equals("y")) break;
            printProducts();
        }
        while(true) {
            try {
                System.out.println(
                        "total: " +
                        total(IOTools.readString("Discount code: (n = none) ")) +
                        " €"
                );
                break;
            } catch(DiscountException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void addProduct(int productID, int amount) throws BasketExceptionRepeatable, BasketExceptionNonRepeatable {
        if(this.productMap.get(productID) == null) {
            throw new BasketExceptionRepeatable("this ID does not exist!");
        }
        if(amount <= 0) {
            throw new BasketExceptionRepeatable("amount too small!");
        }
        if(this.requestedProducts.get(this.productMap.get(productID)) != null) {
            int tmp = this.requestedProducts.get(this.productMap.get(productID));
            this.requestedProducts.replace(this.productMap.get(productID), tmp + amount);
            throw new BasketExceptionNonRepeatable("product " +
                    this.productMap.get(productID).getName() +
                    " is already in your basket!");
        }
        this.requestedProducts.put(this.productMap.get(productID), amount);
    }

    public void printProducts() {
        System.out.println("Available products: ");
        this.productMap.forEach((ID, product) -> {
            if(this.stock.get(ID) > 0) {
                System.out.println("ID: " + ID + " " + product.getName() + " " + product.getPrice() + " € QTY: "
                + this.stock.get(ID));
            }
        });
        System.out.println();
    }

    private double total(String code) throws DiscountException {
        if(code.length() != 6 && !code.equals("n")) throw new DiscountException("Invalid code length!");
        for (char c : code.toCharArray()) {
            if(!Character.isLetterOrDigit(c)) throw new DiscountException("Invalid code format!");
        }

        AtomicInteger total = new AtomicInteger();
        AtomicBoolean isExceptionToThrow = new AtomicBoolean(true);
        // sum up the product price
        this.requestedProducts.forEach((product, amount) -> {
            total.addAndGet(product.getPrice() * amount);
        });
        // use discount code
        this.discountCodes.forEach((discountCode, factor) -> {
            if(code.toUpperCase().equals(discountCode)) {
                total.set((int)(total.doubleValue() * factor));
                isExceptionToThrow.set(false);
            }
        });
        if(isExceptionToThrow.get()) throw new DiscountException("Code is not a discount code!");
        return total.intValue();
    }
}
