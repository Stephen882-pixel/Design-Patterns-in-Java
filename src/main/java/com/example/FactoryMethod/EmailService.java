package com.example.FactoryMethod;

public class EmailService extends NotificationService {
    @Override
    protected Notification createNotification() {
        return new EmailNotification();
    }
}
