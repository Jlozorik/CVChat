package violett.pro.cvchat.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import violett.pro.cvchat.data.local.dao.ContactDao
import violett.pro.cvchat.data.local.dao.MessageDao
import violett.pro.cvchat.data.local.entity.ContactEntity
import violett.pro.cvchat.data.local.entity.MessageEntity

@Database(
    entities = [ContactEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false // Для продакшена лучше true и настроить схемы
)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun messageDao(): MessageDao
}