package com.decade.practice.engagement.domain.services;

import com.decade.practice.engagement.domain.ChatCreators;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TwoParticipantChatIdentifierMaker implements ChatIdentifierMaker {
      @Override
      public String make(ChatCreators creators) {
            UUID smaller, bigger;
            if (creators.callerId().compareTo(creators.partnerId()) > 0) {
                  smaller = creators.partnerId();
                  bigger = creators.callerId();
            } else {
                  smaller = creators.callerId();
                  bigger = creators.partnerId();
            }
            return smaller + "+" + bigger;
      }
}
