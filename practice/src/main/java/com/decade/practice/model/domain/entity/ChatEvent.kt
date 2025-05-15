package com.decade.practice.model.domain.entity

import com.decade.practice.model.domain.STARTING_VERSION
import com.decade.practice.model.domain.embeddable.ChatIdentifier
import com.decade.practice.model.local.LocalChat
import com.decade.practice.utils.inspectPartner
import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.util.*

@JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.EXISTING_PROPERTY,
      property = "eventType",
      visible = true
)
@JsonSubTypes(
      JsonSubTypes.Type(value = SeenEvent::class, name = SEEN),
      JsonSubTypes.Type(value = TextEvent::class, name = TEXT),
      JsonSubTypes.Type(value = IconEvent::class, name = ICON),
      JsonSubTypes.Type(value = ImageEvent::class, name = IMAGE),
      JsonSubTypes.Type(value = WelcomeEvent::class, name = WELCOME)
)

@Entity
@Table(indexes = [Index(columnList = "event_version")])
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type")
abstract class ChatEvent(

      @JsonIgnore
      @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
      @JoinColumns(
            JoinColumn(name = "first_user", insertable = false, updatable = false), // referencedName derived
            JoinColumn(name = "second_user", insertable = false, updatable = false) // referencedName derived
      )
      var chat: Chat,

      @JsonIgnore
      @ManyToOne(cascade = [CascadeType.PERSIST])
      var sender: User,

      @Column(name = "event_type", insertable = false, updatable = false)
      val eventType: String
) {

      @JsonProperty(access = JsonProperty.Access.READ_ONLY)
      @ManyToOne(cascade = [CascadeType.PERSIST])
      var owner: User = sender

      @JsonDeserialize(`as` = HashSet::class)
      @OneToMany(cascade = [CascadeType.PERSIST], mappedBy = "event", fetch = FetchType.EAGER)
      val edges: MutableSet<Edge> = mutableSetOf()

      @JsonIgnore
      @Id
      @GeneratedValue(strategy = GenerationType.UUID)
      var id: UUID? = null

      @JsonProperty(value = "id")
      @Column(nullable = false, unique = true)
      @NotNull
      var localId: UUID = UUID.randomUUID()

      @Embedded
      @AttributeOverrides(
            AttributeOverride(
                  name = "firstUser",
                  column = Column(name = "first_user", updatable = false)
            ),
            AttributeOverride(
                  name = "secondUser",
                  column = Column(name = "second_user", updatable = false)
            )
      )
      val chatIdentifier: ChatIdentifier = chat.identifier

      var eventVersion: Int = STARTING_VERSION
      var createdTime: Long = System.currentTimeMillis()

      abstract fun copy(): ChatEvent

      @get:JsonGetter("partner")
      val partner: User
            get() = chat.inspectPartner(owner)

      @get:JsonGetter("chat")
      val localChat: LocalChat
            get() = LocalChat(chat, owner)

      @get:JsonGetter("sender")
      val senderId: UUID
            get() = sender.id


}