import org.eclipse.jetty.websocket.api.Session;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by Michał Zakrzewski on 2017-01-25.
 */
public class Chat {
    private Map<Session, String> usernames = new ConcurrentHashMap<>();
    private Map<String, String> usersOnChannel = new ConcurrentHashMap<>();
    private List<String> channels = new CopyOnWriteArrayList<>();
    private AtomicInteger nextChannelNumber;
    private ChatBot chatBot;

    public Chat(){
        channels.add("Chatbot");
        chatBot = new ChatBot();
        nextChannelNumber = new AtomicInteger(0);
    }

    public void addChannel(){
        String channel = "Channel " + nextChannelNumber.incrementAndGet();
        channels.add(channel);
    }

    public void addUserToChannel(String username, String channel){
        usersOnChannel.put(username, channel);
    }

    public void addUsername(Session user, String username){
        if(!usernames.containsValue(username))
            usernames.put(user, username);
        else
            throw new IllegalArgumentException();
    }

    public void removeUser(Session user){
        if(usernames.containsKey(user))
            usernames.remove(user);
        else
            throw new NoSuchElementException();
    }

    public void removeUserFromChannel(String username){
        if(usersOnChannel.containsKey(username))
            usersOnChannel.remove(username);
        else
            throw new NoSuchElementException();
    }

    public String getUsersChannel(String username){
        if(usersOnChannel.containsKey(username))
            return usersOnChannel.get(username);
        else
            throw new NoSuchElementException();
    }

    public boolean isUserOnChannel(String username){
        return usersOnChannel.containsKey(username);
    }

    public String getUserName(Session user){
        if(usernames.containsKey(user))
            return usernames.get(user);
        else return "";
    }

    public String getChatBotAnswer(String question) {
        return chatBot.getAnswer(question);
    }

    public Set<Session> getsUsersSessions() {
        return usernames.keySet();
    }

    public List<String> getChannels() {
        return channels;
    }

    public List<String> getUsersOnChannel(String channel){
        List<String> res = usersOnChannel.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(channel))
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedList::new));
        return res;
    }
}
