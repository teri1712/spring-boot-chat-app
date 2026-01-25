package com.decade.practice.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ChatOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;
    
    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private ChatEvent currentEvent;

    int currentVersion;
}
