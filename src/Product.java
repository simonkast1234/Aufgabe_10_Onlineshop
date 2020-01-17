public class Product {
    private String name;
    private int price;
    private int ID;
    private static int nextID = 1;

    public Product(String name, int price) {
        this.name = name;
        this.price = price;
        this.ID = nextID++;
    }

    public int getID() {
        return ID;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
