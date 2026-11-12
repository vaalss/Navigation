package plat.lab7.hernandez_maldonado.character.data

import plat.lab7.hernandez_maldonado.character.network.CharacterDTO
fun CharacterEntity.toModel(): Character {
    return Character(
        id = this.id,
        name = this.name,
        status = this.status,
        species = this.species,
        gender = this.gender,
        image = this.image,
        isFavorite = this.isFavorite
    )
}

fun Character.toEntity(): CharacterEntity {
    return CharacterEntity(
        id = this.id,
        name = this.name,
        status = this.status,
        species = this.species,
        gender = this.gender,
        image = this.image,
        isFavorite = this.isFavorite
    )
}

fun CharacterDTO.toEntity(): CharacterEntity {
    return CharacterEntity(
        id = id,
        name = name,
        status = status,
        species = species,
        gender = gender,
        image = image,
        isFavorite = false
    )
}