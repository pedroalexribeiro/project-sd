package web.action;

import rmiclient.clientInterface;
import rmiserver.Interface;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnError;
import javax.websocket.Session;

@ServerEndpoint(value = "/ws")
public class WebSocket extends UnicastRemoteObject implements clientInterface, Serializable {

    private static final AtomicInteger sequence = new AtomicInteger(1);
    private static ConcurrentHashMap<String,Session> userSessions =new ConcurrentHashMap<>();
    private Session session;
    private String username;
    public WebSocket() throws RemoteException {

    }
    @OnOpen
    public void start(Session session) {
        this.session = session;
    }

    @OnClose
    public void end() {
    }

    @OnMessage
    public void receiveMessage(String username) {
        this.username=username;
        userSessions.put(this.username,this.session);
        try {
            Interface server = (Interface) LocateRegistry.getRegistry("192.168.1.12", 7000).lookup("Server");
            server.subscribe(this.username,this);
        }
        catch(NotBoundException |RemoteException e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void handleError(Throwable t) {
        t.printStackTrace();
    }

    public void liveNotification(String message){
        sendMessage(message);
    }

    private void sendMessage(String text){
        try {
            userSessions.get(username).getBasicRemote().sendText(text);
        }
         catch (IOException e) {
            try {
                this.session.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /*public static HashMap<String, Session> getUserSessions()
    {
        return userSessions;
    }*/
}
