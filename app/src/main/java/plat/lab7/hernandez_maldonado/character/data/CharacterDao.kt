package plat.lab7.hernandez_maldonado.character.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    @Insert
    suspend fun insertAll(characters: List<CharacterEntity>)

    @Query("SELECT * FROM characters ORDER BY id")
    fun getAll(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :id")
    fun getById(id: Int): Flow<CharacterEntity?>

    @Query("UPDATE characters SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM characters")
    suspend fun count(): Int

    @Query("DELETE FROM characters")
    suspend fun deleteAll()
}