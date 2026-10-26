package plat.lab7.hernandez_maldonado.character.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String,
    val isFavorite: Boolean = false
)