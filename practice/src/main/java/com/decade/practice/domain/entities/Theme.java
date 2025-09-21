package com.decade.practice.domain.entities;

import com.decade.practice.domain.embeddables.ImageSpec;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
/////////////////////////////////////////////
@Entity
public class Theme {

        @Id
        private Integer id;

        @Embedded
        private ImageSpec background;

        protected Theme() {
        }

        public Theme(Integer id, ImageSpec background) {
                this.id = id;
                this.background = background;
        }

        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        public ImageSpec getBackground() {
                return background;
        }

        public void setBackground(ImageSpec background) {
                this.background = background;
        }
}
