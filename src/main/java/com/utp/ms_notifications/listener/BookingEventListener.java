package com.utp.ms_notifications.listener;

import com.utp.ms_notifications.config.RabbitConfig;
import com.utp.ms_notifications.dto.BookingEvent;
import com.utp.ms_notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onBookingEvent(BookingEvent event) {
        notificationService.processBookingEvent(event);
    }
}