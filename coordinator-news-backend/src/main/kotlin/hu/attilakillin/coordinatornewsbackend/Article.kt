package hu.attilakillin.coordinatornewsbackend

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity(name = "articles")
data class Article (
    @Id @GeneratedValue var id: Int,
    var title: String,
    var text: String,
    var content: String
)
