package com.decade.practice.model.domain.entity;

import com.decade.practice.model.domain.DefaultAvatar;
import com.decade.practice.model.domain.embeddable.ImageSpec;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(
        name = "UserMember", indexes = {
        @Index(columnList = "name")}
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role")
@DiscriminatorValue("ROLE_USER")
public class User {

        public static final String MALE = "male";
        public static final String FEMALE = "female";

        @Column(unique = true, nullable = false, updatable = false)
        private String username;

        @JsonIgnore
        @Column(nullable = false)
        private String password;

        private String name;

        @Temporal(value = TemporalType.TIMESTAMP)
        private Date dob;

        @Column(insertable = false, updatable = false)
        private String role;

        @Id
        private UUID id;

        @JsonProperty(value = "avatar")
        @Embedded
        private ImageSpec avatar;

        @Version
        private Integer version;

        @JsonIgnore
        @ElementCollection(fetch = FetchType.EAGER)
        @CollectionTable(name = "user_genders", joinColumns = @JoinColumn(name = "user_id"))
        private Set<String> gender = new HashSet<>();

        @JsonProperty("gender")
        private String json_gender;

        @JsonIgnore
        @OneToOne(
                mappedBy = "owner",
                cascade = CascadeType.ALL
        )
        private SyncContext syncContext;

        // No-arg constructor required by JPA
        protected User() {
                this.username = null;
                this.role = "ROLE_USER";
                this.id = UUID.randomUUID();
        }

        public User(String username, String password) {
                this(username, password, username, new Date(), "ROLE_USER", UUID.randomUUID());
        }

        public User(String username, String password, String name, Date dob, String role, UUID id) {
                this.username = username;
                this.password = password;
                this.name = name;
                this.dob = dob;
                this.role = role;
                this.id = id;
                this.avatar = DefaultAvatar.getInstance();
                this.gender.add(MALE);
                this.syncContext = new SyncContext(this);
        }

        public String getUsername() {
                return username;
        }

        public String getPassword() {
                return password;
        }

        public void setPassword(String password) {
                this.password = password;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public Date getDob() {
                return dob;
        }

        public void setDob(Date dob) {
                this.dob = dob;
        }

        public String getRole() {
                return role;
        }

        public UUID getId() {
                return id;
        }

        public void setId(UUID id) {
                this.id = id;
        }

        public ImageSpec getAvatar() {
                return avatar;
        }

        public void setAvatar(ImageSpec avatar) {
                this.avatar = avatar;
        }

        public Integer getVersion() {
                return version;
        }

        public void setVersion(Integer version) {
                this.version = version;
        }

        public Set<String> getGender() {
                return gender;
        }

        public void setGender(Set<String> gender) {
                this.gender = gender;
        }

        public String getJson_gender() {
                if (json_gender == null && !gender.isEmpty()) {
                        // Simulate Kotlin's randomOrNull() by getting a random element
                        int randomIndex = new Random().nextInt(gender.size());
                        Iterator<String> iterator = gender.iterator();
                        for (int i = 0; i < randomIndex; i++) {
                                iterator.next();
                        }
                        return iterator.next();
                }
                return json_gender;
        }

        public void setJson_gender(String json_gender) {
                this.json_gender = json_gender;
        }

        public SyncContext getSyncContext() {
                return syncContext;
        }

        public void setSyncContext(SyncContext syncContext) {
                this.syncContext = syncContext;
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof User)) return false;
                User user = (User) o;
                return Objects.equals(id, user.id);
        }

        @Override
        public int hashCode() {
                return Objects.hash(id);
        }
}

