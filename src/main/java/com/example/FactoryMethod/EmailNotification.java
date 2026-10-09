package com.example.FactoryMethod;

public class EmailNotification implements Notification {
    public void send(String message) {
        System.out.println("EMAIL -> stephen@example.com: " + message);
    }
}
