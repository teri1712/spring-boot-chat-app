package com.decade.practice.application.domain;

import com.decade.practice.persistence.jpa.embeddables.ChatCreators;

public interface ChatIdentifierMaker {
    String make(ChatCreators creators);

}
