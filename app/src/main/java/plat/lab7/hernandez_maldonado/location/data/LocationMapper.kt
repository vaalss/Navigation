package plat.lab7.hernandez_maldonado.location.data

import plat.lab7.hernandez_maldonado.location.network.LocationDTO

fun LocationEntity.toModel(): Location {
    return Location(
        id = this.id,
        name = this.name,
        type = this.type,
        dimension = this.dimension
    )
}

fun Location.toEntity(): LocationEntity {
    return LocationEntity(
        id = this.id,
        name = this.name,
        type = this.type,
        dimension = this.dimension
    )
}

fun LocationDTO.toEntity(): LocationEntity {
    return LocationEntity(
        id = id,
        name = name,
        type = type,
        dimension = dimension
    )
}