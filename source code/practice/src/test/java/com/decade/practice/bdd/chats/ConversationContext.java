package com.decade.practice.bdd.chats;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

@ScenarioScope
@Component
public class ConversationContext {
    public String chatId;
    public String me;
    public String partner;
}
