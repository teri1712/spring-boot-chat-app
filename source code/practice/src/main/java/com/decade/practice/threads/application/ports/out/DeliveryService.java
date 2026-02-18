package com.decade.practice.threads.application.ports.out;

import com.decade.practice.threads.dto.EventResponse;

public interface DeliveryService {

    void send(EventResponse event);

}
