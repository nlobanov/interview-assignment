package dev.lobanov.raynetassignment.model

import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "companies")
@EntityListeners(AuditingEntityListener::class)
class Company(
    @Id
    var regNumber: String,
    var title: String,
    var email: String,
    var phone: String,

    @Enumerated(EnumType.STRING)
    var upsertStatus: UpsertStatus = UpsertStatus.PENDING,

    @Column(nullable = true)
    var uploadError: String? = null,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Company
        return regNumber == other.regNumber
    }

    override fun hashCode(): Int = regNumber.hashCode()

    override fun toString(): String {
        return "Company(regNumber=$regNumber, title=$title)"
    }
}