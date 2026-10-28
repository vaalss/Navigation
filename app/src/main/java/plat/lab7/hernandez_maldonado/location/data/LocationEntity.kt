package plat.lab7.hernandez_maldonado.location.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String
)