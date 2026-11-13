package plat.lab7.hernandez_maldonado.location.data

import plat.lab7.hernandez_maldonado.location.network.LocationDTO

fun LocationEntity.toModel(): Location {
    return Location(
        id = id,
        name = name,
        type = type,
        dimension = dimension,
        isFavorite = isFavorite
    )
}

fun Location.toEntity(): LocationEntity {
    return LocationEntity(
        id = id,
        name = name,
        type = type,
        dimension = dimension,
        isFavorite = isFavorite
    )
}

fun LocationDTO.toEntity(): LocationEntity {
    return LocationEntity(
        id = id,
        name = name,
        type = type,
        dimension = dimension,
        isFavorite = false
    )
}
