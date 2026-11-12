package plat.lab7.hernandez_maldonado.core.data

import plat.lab7.hernandez_maldonado.character.domain.CharacterRepository
import plat.lab7.hernandez_maldonado.location.domain.LocationRepository

class InitialSync(
    private val characterRepository: CharacterRepository,
    private val locationRepository: LocationRepository
) {

    suspend fun synchronize() {
        characterRepository.synchronize()
        locationRepository.synchronize()
    }
}