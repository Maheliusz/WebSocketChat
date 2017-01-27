import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.util.NoSuchElementException;

/**
 * Created by Michał Zakrzewski on 2017-01-25.
 */
public class Functions {
    private Chat chat = new Chat();
    private HTMLMaker htmlMaker = new HTMLMaker();

    public void refresh() {
        chat.getsUsersSessions().stream().filter(Session::isOpen).forEach(this::refreshForUser);
    }

    public void refreshForUser(Session user) {
        try {
            user.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("reason", "refresh")
                    .put("channellist", chat.getChannels())
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendUsersMessage(Session user, String contents) {
        String username = chat.getUserName(user);
        String channel = chat.getUsersChannel(username);
        broadcastMessage(username, contents, channel);
        if (channel.equals("Chatbot"))
            askChatbot(contents);
    }

    private void askChatbot(String question) {
        String answer = chat.getChatBotAnswer(question);
        broadcastMessage("chatbot", answer, "Chatbot");
    }

    public void broadcastMessage(String sender, String message, String channel) {
        chat.getsUsersSessions().stream().filter(Session::isOpen)
                .filter(session -> {
                    try {
                        return chat.getUsersChannel(chat.getUserName(session))
                                .equals(channel);
                    } catch (NoSuchElementException ex) {
                        return false;
                    }
                })
                .forEach(session -> {
                    try {
                        session.getRemote().sendString(String.valueOf(
                                new JSONObject()
                                        .put("reason", "message")
                                        .put("userMessage", htmlMaker.createHtmlMessageFromSender(sender, message))
                                        .put("channellist", chat.getChannels())
                        ));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    public void sendMessageToUser(Session user, String message) {
        try {
            user.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("reason", "message")
                    .put("userMessage", htmlMaker.createHtmlMessageFromSender("Server", message))
                    .put("channellist", chat.getChannels())
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addUserToChannel(Session user, String channel) {
        String username = chat.getUserName(user);
        if (chat.isUserOnChannel(username))
            removeUserFromChannel(user);
        chat.addUserToChannel(username, channel);
        broadcastMessage(channel, username + " joined", channel);
    }

    public void removeUserFromChannel(Session user) {
        String username = chat.getUserName(user);
        String channel = chat.getUsersChannel(username);
        chat.removeUserFromChannel(username);
        broadcastMessage(channel, username + " left", channel);
    }

    public void addChannel() {
        chat.addChannel();
    }

    public void removeUser(Session user) {
        chat.removeUser(user);
    }

    public void addUsername(Session user, String username) {
        chat.addUsername(user, username);
    }
}
