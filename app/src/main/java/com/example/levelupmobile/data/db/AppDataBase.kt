package com.example.levelupmobile.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.levelupmobile.data.dao.CartDao
import com.example.levelupmobile.data.dao.ProductDao
import com.example.levelupmobile.data.entity.CartLineEntity
import com.example.levelupmobile.data.entity.ProductEntity
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Database(
    entities = [
        ProductEntity::class,
        CartLineEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Scope para trabajo de siembra
        private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                // Usamos un ref local para poder acceder dentro del callback
                lateinit var dbRef: AppDatabase

                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "levelup.db"
                ).addCallback(object : RoomDatabase.Callback() {

                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // ⚠️ Se ejecuta solo la primera vez que se crea la DB
                        applicationScope.launch {
                            seedFromJsonIfEmpty(context, dbRef)
                        }
                    }
                })

                dbRef = builder.build()
                INSTANCE = dbRef
                dbRef
            }

        private suspend fun seedFromJsonIfEmpty(context: Context, db: AppDatabase) {
            val dao = db.productDao()
            if (dao.count() > 0) return // ya hay datos, no sembrar

            val jsonText = withContext(Dispatchers.IO) {
                context.assets.open("products.json")
                    .bufferedReader().use { it.readText() }
            }
            val items = Json.decodeFromString<List<ProductSeed>>(jsonText)
            dao.upsertAll(items.map { it.toEntity() })
        }
    }
}

/* ====== Semilla desde JSON ====== */

@Serializable
data class ProductSeed(
    val id: String,
    val name: String,
    val description: String = "",
    val priceNeto: Long,
    val ivaRate: Double = 0.19,
    val imageUrl: String? = null,
    val stock: Int = 999,
    val categoryId: Int? = null
)

private fun ProductSeed.toEntity() = ProductEntity(
    id = id,
    name = name,
    description = description,
    priceNeto = priceNeto,
    ivaRate = ivaRate,
    imageUrl = imageUrl,
    stock = stock,
    categoryId = categoryId
)
