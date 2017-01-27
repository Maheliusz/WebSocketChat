import static spark.Spark.*;

/**
 * Created by Michał Zakrzewski on 2017-01-15.
 */
public class Application {
    public static void main(String[] args) {
        staticFileLocation("/public"); //index.html is served at localhost:4567 (default port)
        ChatWebSocketHandler webSocketHandler = new ChatWebSocketHandler();
        webSocket("/chat", webSocketHandler);
        init();
    }
}
