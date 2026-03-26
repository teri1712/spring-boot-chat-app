package com.decade.practice.chatsettings.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class Theme {

      @Id
      @GeneratedValue(strategy = GenerationType.SEQUENCE)
      private Long id;

      // TODO: Fix client
      private String background;

      private String name;


}
