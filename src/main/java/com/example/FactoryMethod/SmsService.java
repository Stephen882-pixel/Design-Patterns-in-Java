package com.example.FactoryMethod;

public class SmsService extends NotificationService {
    @Override
    protected Notification createNotification() {
        return new SmsNotification();
    }
}
