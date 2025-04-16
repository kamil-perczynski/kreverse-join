package io.github.kamilperczynski.kreversejoin.dto

data class EntityChangedDto(val entityName: String, val id: String)

fun EntityChangedDto.toEntityChangedKey(): String {
    return "EntityChanged/$entityName/$id"
}
