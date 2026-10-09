package com.example.FactoryMethod;

// The Creator: knows WHEN to create a notification, but leaves WHICH one to subclasses
public abstract class NotificationService {

    // The factory method
    protected abstract Notification createNotification();

    public void notifyUser(String message) {
        Notification notification = createNotification();
        notification.send(message);
    }
}
