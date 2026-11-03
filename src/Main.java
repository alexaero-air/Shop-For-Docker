import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class Main {
    // Временное хранилище корзины в памяти (для простоты)
    private static final List<Product> cart = new ArrayList<>();
    private static final Product[] catalog = {
            new Product(1, "Ноутбук", 999.99),
            new Product(2, "Смартфон", 499.99),
            new Product(3, "Наушники", 89.99)
    };

    public static void main(String[] args) throws IOException {
        // Запускаем сервер на порту 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Главная страница и добавление товара
        server.createContext("/", new ShopHandler());

        server.setExecutor(null);
        System.out.println("🚀 Магазин запущен на порту 8080...");
        server.start();
    }

    static class ShopHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();

            // Логика добавления в корзину через URL-параметр ?add=ID
            if (query != null && query.startsWith("add=")) {
                try {
                    int id = Integer.parseInt(query.split("=")[1]);
                    if (id >= 1 && id <= catalog.length) {
                        cart.add(catalog[id - 1]);
                    }
                } catch (NumberFormatException ignored) {}

                // Редирект обратно на главную, чтобы не дублировать добавление при обновлении
                exchange.getResponseHeaders().set("Location", "/");
                exchange.sendResponseHeaders(303, -1);
                return;
            }

            // Логика очистки корзины ?clear=true
            if (query != null && query.equals("clear=true")) {
                cart.clear();
                exchange.getResponseHeaders().set("Location", "/");
                exchange.sendResponseHeaders(303, -1);
                return;
            }

            // Генерация простейшей HTML-страницы
            StringBuilder html = new StringBuilder();
            html.append("<html><head><meta charset='UTF-8'><title>Docker Shop</title></head><body>");
            html.append("<h1>🐳 Добро пожаловать в Docker-магазин!</h1>");

            html.append("<h2>Каталог товаров</h2><ul>");
            for (Product p : catalog) {
                html.append(String.format("<li>%s — $%s <a href='/?add=%d'>[Купить]</a></li>",
                        p.getName(), p.getPrice(), p.getId()));
            }
            html.append("</ul>");

            html.append("<h2>Ваша корзина</h2>");
            if (cart.isEmpty()) {
                html.append("<p>Корзина пуста</p>");
            } else {
                html.append("<ul>");
                double total = 0;
                for (Product p : cart) {
                    html.append(String.format("<li>%s — $%s</li>", p.getName(), p.getPrice()));
                    total += p.getPrice();
                }
                html.append("</ul>");
                html.append("<p><b>Итого: $").append(total).append("</b></p>");
                html.append("<p><a href='/?clear=true'>[Очистить корзину]</a></p>");
            }

            html.append("</body></html>");

            byte[] response = html.toString().getBytes("UTF-8");
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }
}
