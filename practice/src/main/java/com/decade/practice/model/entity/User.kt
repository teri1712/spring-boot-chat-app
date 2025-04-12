package com.decade.practice.model.entity

import com.decade.practice.model.DefaultAvatar
import com.decade.practice.model.SyncContext
import com.decade.practice.model.embeddable.ImageSpec
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import java.util.*

const val MALE: String = "male"
const val FEMALE: String = "female"

@Entity
@Table(name = "UserMember", indexes = [Index(columnList = "username")])
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role")
@DiscriminatorValue("ROLE_USER")
open class User(

    @Column(unique = true, nullable = false, updatable = false)
    val username: String,

    @JsonIgnore
    @Column(nullable = false)
    var password: String,

    var name: String = username,

    @Temporal(value = TemporalType.TIMESTAMP)
    var dob: Date = Date(),

    @Column(insertable = false, updatable = false)
    val role: String = "ROLE_USER",

    @Id
    var id: UUID = UUID.randomUUID(),
) {

    @JsonProperty(value = "avatar")
    @Embedded
    var avatar: ImageSpec = DefaultAvatar

    @Version
    var version: Int? = null

    @JsonIgnore
    @ElementCollection(fetch = FetchType.EAGER)
    val gender: MutableSet<String> = mutableSetOf(MALE)


    @field:JsonProperty("gender")
    val json_gender: String? = null
        get() = field ?: gender.randomOrNull()

    @JsonIgnore
    @OneToOne(
        mappedBy = "owner",
        cascade = [CascadeType.ALL]
    )
    // bc User is the parent table, saving user will ensure the user is persisted
    // before cascading this child tables
    var syncContext: SyncContext = SyncContext(this)

    // for invalidate tokens
    @JsonIgnore
    var passwordVersion = 0


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return id == other.id
    }
}


@Entity
@DiscriminatorValue("ROLE_ADMIN")
class Admin(username: String, password: String) : User(username, password, role = "ROLE_ADMIN") {
}