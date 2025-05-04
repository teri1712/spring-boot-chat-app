package com.decade.practice.model.domain.entity

import com.decade.practice.model.domain.embeddable.ChatIdentifier
import jakarta.persistence.*
import org.hibernate.annotations.CurrentTimestamp
import org.hibernate.annotations.SourceType
import org.hibernate.generator.EventType
import java.util.*

@Entity
data class Chat(

      @ManyToOne(cascade = [CascadeType.PERSIST])
      @JoinColumn(name = "first_user") // for naming
      @MapsId("firstUser")
      var firstUser: User,

      @ManyToOne(cascade = [CascadeType.PERSIST])
      @JoinColumn(name = "second_user") // for naming
      @MapsId("secondUser")
      var secondUser: User,
) {

      @EmbeddedId
      var identifier: ChatIdentifier

      @CurrentTimestamp(event = [EventType.INSERT], source = SourceType.VM)
      @Temporal(TemporalType.TIMESTAMP)
      var interactTime: Date? = null

      var messageCount: Int = 0

      // for un-saved check
      @Version
      var version: Int? = null

      init {
            if (firstUser.id > secondUser.id) {
                  val temp = firstUser
                  firstUser = secondUser
                  secondUser = temp
            }
            identifier = ChatIdentifier(firstUser.id, secondUser.id)
      }

      override fun hashCode(): Int {
            return identifier.hashCode()
      }

}
