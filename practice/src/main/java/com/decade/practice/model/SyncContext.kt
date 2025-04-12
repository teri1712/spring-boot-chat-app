package com.decade.practice.model

import com.decade.practice.model.entity.User
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import java.util.*


const val STARTING_VERSION = 0

@Entity
data class SyncContext(
    // primary key is derived
    @MapsId // PrimaryKeyJoinColumn
    @OneToOne
    var owner: User
) {
    @Id
    var id: UUID? = null

    var eventVersion: Int = STARTING_VERSION

    fun incVersion(): Int {
        return ++this.eventVersion
    }
}
