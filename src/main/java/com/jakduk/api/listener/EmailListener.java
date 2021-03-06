package com.jakduk.api.listener;

import com.jakduk.api.model.rabbitmq.EmailPayload;
import com.jakduk.api.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;

/**
 * Created by pyohwanjang on 2017. 6. 17..
 */

@Slf4j
@Component
public class EmailListener {

    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = "${jakduk.rabbitmq.queues.email.binding-queue-name}")
    public void receive(EmailPayload emailPayload) throws MessagingException {

        switch (emailPayload.getType()) {
            case WELCOME:
                emailService.sendWelcome(emailPayload);
                break;
            case RESET_PASSWORD:
                emailService.sendResetPassword(emailPayload);
                break;
        }
    }

}
