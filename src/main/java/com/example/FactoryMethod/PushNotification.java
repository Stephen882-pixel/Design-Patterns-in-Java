package com.example.FactoryMethod;

public class PushNotification implements Notification {
    public void send(String message) {
        System.out.println("PUSH  -> phone app: " + message);
    }
}
