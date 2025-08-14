package com.decade.practice.model.domain.entity;

import com.decade.practice.model.domain.embeddable.ImageSpec;
import jakarta.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.UUID;

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
/////////////////////////////////////////////
@Entity
public class Theme {
        @Id
        @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
        private UUID id;

        @Embedded
        private ImageSpec background;

        protected Theme() {
        }

        public UUID getId() {
                return id;
        }

        public void setId(UUID id) {
                this.id = id;
        }

        public ImageSpec getBackground() {
                return background;
        }

        public void setBackground(ImageSpec background) {
                this.background = background;
        }
}
