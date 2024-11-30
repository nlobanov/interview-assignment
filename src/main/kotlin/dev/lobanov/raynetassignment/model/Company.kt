package dev.lobanov.raynetassignment.model

import jakarta.persistence.*
import jakarta.validation.constraints.*
import org.hibernate.proxy.HibernateProxy
import java.time.LocalDate

@Entity
@Table(name = "companies")
data class Company(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @field:NotBlank
    @field:Size(max = 255)
    val name: String,

    val person: Boolean = false,

    @field:Size(max = 255)
    val lastName: String? = null,

    @field:Size(max = 255)
    val firstName: String? = null,

    @field:Size(max = 50)
    val titleBefore: String? = null,

    @field:Size(max = 50)
    val titleAfter: String? = null,

    @field:Size(max = 255)
    val salutation: String? = null,

    @field:Min(1)
    val securityLevel: Long? = null,

    val owner: Long? = null,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    val rating: Rating = Rating.C,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    val state: CompanyState = CompanyState.A_POTENTIAL,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    val role: CompanyRole = CompanyRole.A_SUBSCRIBER,

    @field:Size(max = 1000)
    val notice: String? = null,

    val category: Long? = null,
    val contactSource: Long? = null,
    val employeesNumber: Long? = null,
    val legalForm: Long? = null,
    val paymentTerm: Long? = null,
    val turnover: Long? = null,
    val economyActivity: Long? = null,
    val companyClassification1: Long? = null,
    val companyClassification2: Long? = null,
    val companyClassification3: Long? = null,

    @field:Pattern(regexp = "\\d{8}")
    val regNumber: String? = null,

    @field:Pattern(regexp = "[A-Z]{2}\\d{8,10}")
    val taxNumber: String? = null,

    @field:Pattern(regexp = "SK\\d{10}")
    val taxNumber2: String? = null,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    val taxPayer: TaxPayer? = null,

    @field:Size(max = 50)
    val bankAccount: String? = null,

    @field:Size(max = 50)
    val databox: String? = null,

    @field:Size(max = 255)
    val court: String? = null,

    val birthday: LocalDate? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "company_id")
    val addresses: List<CompanyAddress> = emptyList(),

    @Embedded
    val socialNetworkContact: SocialNetworkContact? = null,

    val originLead: Long? = null,

    @ElementCollection
    @CollectionTable(name = "company_tags")
    val tags: Set<String> = emptySet(),

    @ElementCollection
    @CollectionTable(name = "company_custom_fields")
    @MapKeyColumn(name = "field_name")
    @Column(name = "field_value")
    val customFields: Map<String, String> = emptyMap()
) {
    @AssertTrue(message = "Last name is required for person")
    fun isLastNameValidForPerson(): Boolean {
        return !person || (person && !lastName.isNullOrBlank())
    }

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Company

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    override fun toString(): String {
        return this::class.simpleName + "(  id = $id   ,   name = $name )"
    }
}
