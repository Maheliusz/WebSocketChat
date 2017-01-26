import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import java.util.NoSuchElementException;

@WebSocket
public class ChatWebSocketHandler {

    private Functions functions = new Functions();

    @OnWebSocketConnect
    public void onConnect(Session user) {
        functions.refreshForUser(user);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        functions.removeUser(user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        String reason = message.substring(0, message.indexOf('|'));
        String contents = message.substring(message.indexOf('|') + 1);
        switch (reason) {
            case "user":
                try {
                    functions.sendUsersMessage(user, contents);
                } catch (NoSuchElementException ex) {
                    functions.sendMessageToUser(user, "Enter channel first");
                }
                break;
            case "name":
                try {
                    functions.addUsername(user, contents);
                    functions.refreshForUser(user);
                } catch (IllegalArgumentException ex) {
                    try {
                        user.getRemote().sendString(String.valueOf(new JSONObject()
                                .put("reason", "takenusername")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "addChannel":
                functions.addChannel();
                functions.refresh();
                break;
            case "channelEnter":
                functions.addUserToChannel(user, contents);
                break;
            case "channelExit":
                try {
                    functions.removeUserFromChannel(user);
                } catch (NoSuchElementException ex) {
                    functions.sendMessageToUser(user, "You aren't on any channel");
                }
                break;
            case "refresh":

                break;

        }
    }

}