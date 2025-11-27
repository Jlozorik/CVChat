package violett.pro.cvchat.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import violett.pro.cvchat.domain.model.Contact

@Entity(tableName = "contacts")
data class ContactEntity(
    val publicKey: String,
    val name: String = "",
    @PrimaryKey(autoGenerate = false)
    val tempId: String,
) {
    fun toDomain(): Contact {
        return Contact(
            publicKey = publicKey,
            name = name,
            tempId = tempId,
        )
    }
}


fun Contact.toEntity(): ContactEntity {
    return ContactEntity(
        publicKey = this.publicKey,
        name = this.name,
        tempId = this.tempId
    )
}