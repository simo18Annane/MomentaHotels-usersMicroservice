package com.project.users_microservice.register;

public interface EmailSender {

    void sendEmail(String to, String body);

}
