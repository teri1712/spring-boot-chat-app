package com.decade.practice.persistence.jpa.entities;

import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_member")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role")
@DiscriminatorValue("ROLE_USER")
public class User {

    public static final float MALE = 1;
    public static final float FEMALE = 2;
    public static final float OTHER = 3;

    @Column(unique = true, nullable = false, updatable = false)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    private String name;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date dob;

    @Column(insertable = false, updatable = false)
    private String role = "ROLE_USER";

    @Id
    private UUID id;

    @Embedded
    private ImageSpec avatar;

    @Version
    private Integer version;

    @Column(nullable = false)
    private Float gender;

    @OneToOne(
            mappedBy = "owner",
            cascade = CascadeType.ALL
    )
    private SyncContext syncContext;

}

