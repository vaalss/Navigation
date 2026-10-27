package plat.lab7.hernandez_maldonado.character.data

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