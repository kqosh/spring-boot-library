package com.jdriven.library.access.model

import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.util.Objects

@MappedSuperclass
abstract class AbstractBaseEntity {

    @Id
    @GeneratedValue
    var id: Long? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as AbstractBaseEntity

        // Als de ID null is, kan het object nooit gelijk zijn aan een ander.
        if (id == null) return false
        
        return id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}