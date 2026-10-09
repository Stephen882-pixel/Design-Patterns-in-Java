package com.example.FactoryMethod;

public class PushService extends NotificationService {
    @Override
    protected Notification createNotification() {
        return new PushNotification();
    }
}
