package plat.lab7.hernandez_maldonado.location.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert
    suspend fun insertAll(locations: List<LocationEntity>)

    @Query("SELECT * FROM locations ORDER BY id")
    fun getAll(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE id = :id")
    fun getById(id: Int): Flow<LocationEntity?>

    @Query("SELECT COUNT(*) FROM locations")
    suspend fun count(): Int

    @Query("DELETE FROM locations")
    suspend fun deleteAll()
}