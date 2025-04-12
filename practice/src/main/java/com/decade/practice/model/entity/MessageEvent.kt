package com.decade.practice.model.entity

import com.decade.practice.model.embeddable.ImageSpec
import com.fasterxml.jackson.annotation.JsonGetter
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull


const val TEXT = "TEXT"
const val IMAGE = "IMAGE"
const val ICON = "ICON"

@Entity
@DiscriminatorValue(ICON)
class IconEvent(
    chat: Chat,
    sender: User,

    @field:NotNull
    @Column(updatable = false)
    val resourceId: Int
) : ChatEvent(chat, sender, ICON) {
    constructor(event: IconEvent) : this(event.chat, event.sender, event.resourceId)

    override fun copy(): ChatEvent {
        return IconEvent(this)
    }

    @get:JsonGetter
    val iconEvent
        get() = com.decade.practice.model.local.IconEvent(resourceId)
}


@Entity
@DiscriminatorValue(TEXT)
open class TextEvent(
    chat: Chat,
    sender: User,

    @field:NotEmpty
    @Column(updatable = false)
    val content: String
) : ChatEvent(chat, sender, TEXT) {

    constructor(event: TextEvent) :
            this(event.chat, event.sender, event.content)

    override fun copy(): ChatEvent {
        return TextEvent(this)
    }

    @get:JsonGetter
    val textEvent
        get() = com.decade.practice.model.local.TextEvent(content)
}


@Entity
@DiscriminatorValue(IMAGE)
class ImageEvent(
    chat: Chat,
    sender: User,

    @field:NotNull
    @field:Valid
    @Column(updatable = false)
    @Embedded
    val image: ImageSpec
) : ChatEvent(chat, sender, IMAGE) {
    constructor(event: ImageEvent) :
            this(event.chat, event.sender, event.image)

    override fun copy(): ChatEvent {
        return ImageEvent(this)
    }

    @get:JsonGetter
    val imageEvent
        get() = com.decade.practice.model.local.ImageEvent(image)
}


fun ChatEvent.isMessage(): Boolean = eventType == TEXT || eventType == IMAGE || eventType == ICON