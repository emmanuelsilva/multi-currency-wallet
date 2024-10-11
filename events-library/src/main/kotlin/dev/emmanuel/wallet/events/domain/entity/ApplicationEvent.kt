package dev.emmanuel.wallet.events.domain.entity

import com.fasterxml.jackson.annotation.JsonIgnore

interface ApplicationEvent {
    @get:JsonIgnore
    val topic: String

    @get:JsonIgnore
    val key: String?
    val event: String

}