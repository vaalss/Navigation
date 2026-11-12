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

    @Query("SELECT COALESCE(MIN(id), 0) - 1 FROM characters WHERE id < 0")
    suspend fun getNextLocalId(): Int

    @Insert
    suspend fun insert(character: CharacterEntity)

    @Query(
        """
    UPDATE characters
    SET name = :name,
        status = :status,
        species = :species,
        gender = :gender
    WHERE id = :id
    """
    )
    suspend fun updateCharacter(
        id: Int,
        name: String,
        status: String,
        species: String,
        gender: String
    ): Int

    @Query("DELETE FROM characters WHERE id = :id")
    suspend fun deleteCharacter(id: Int): Int
}