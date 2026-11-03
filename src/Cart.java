import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<Product> items = new ArrayList<>();

    public void add(Product product) {
        items.add(product);
        System.out.println("➕ " + product.getName() + " добавлен в корзину.");
    }

    public void show() {
        if (items.isEmpty()) {
            System.out.println("🛒 Корзина пуста.");
            return;
        }
        System.out.println("--- Ваша корзина ---");
        double total = 0;
        for (Product p : items) {
            System.out.println("- " + p.getName() + ": $" + p.getPrice());
            total += p.getPrice();
        }
        System.out.println("Итого: $" + total);
    }

    public void clear() {
        items.clear();
    }
}
