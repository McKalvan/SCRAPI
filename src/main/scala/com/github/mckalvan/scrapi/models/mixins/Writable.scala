package com.github.mckalvan.scrapi.models.mixins

import org.json4s.{DefaultFormats, Formats}
import org.json4s.jackson.Serialization.write

// Convenience class used to allow object of some type T to be written in some way
// TODO: Is there a more specific subclass that T could take other than AnyRef?
class Writable[T <: AnyRef](t: T) {
    implicit val formats: Formats = DefaultFormats.preservingEmptyValues
    def asJson: String = write(t)
}


