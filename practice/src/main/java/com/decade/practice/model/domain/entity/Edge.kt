package com.decade.practice.model.domain.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.util.*

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(indexes = [Index(columnList = "event_version")])
class Edge(

      @ManyToOne(cascade = [(CascadeType.PERSIST)])
      val owner: User,

      @ManyToOne(cascade = [(CascadeType.PERSIST)])
      @JoinColumns(
            JoinColumn(name = "from_first"), // implicit referencedName
            JoinColumn(name = "from_second") // implicit referencedName
      )
      val from: Chat,

      @ManyToOne(cascade = [(CascadeType.PERSIST)])
      @JoinColumns(
            JoinColumn(name = "dest_first"), // implicit referencedName
            JoinColumn(name = "dest_second") // implicit referencedName
      )
      val dest: Chat?,


      @JsonIgnore
      @ManyToOne(fetch = FetchType.LAZY)
      val event: ChatEvent,

      @Column(nullable = false)
      val head: Boolean,

      @Column(nullable = false)
      val eventVersion: Int = event.eventVersion
) {

      @Id
      @GeneratedValue(strategy = GenerationType.UUID)
      var id: UUID? = null

}
