package com.decade.practice.live.application.ports.out;

public interface LivenessBroker {

    void send(String des, Object message);

    void sub(String des);

    void unSub(String des);

}
