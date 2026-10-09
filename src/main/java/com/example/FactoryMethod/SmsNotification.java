package com.example.FactoryMethod;

public class SmsNotification implements Notification {
    public void send(String message) {
        System.out.println("SMS   -> +254 700 000 000: " + message);
    }
}
