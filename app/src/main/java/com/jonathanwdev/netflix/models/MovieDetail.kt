package com.jonathanwdev.netflix.models

data class MovieDetail(
    val movie: Movie,
    val similars: List<Movie>
)
