package com.example.jiujitsuapp.network

import com.example.jiujitsuapp.model.Atleta
import retrofit2.Response
import retrofit2.http.*

interface AtletaApi {
    @GET("atletas")
    suspend fun listarAtletas(): Response<List<Atleta>>

    @POST("atletas")
    suspend fun cadastrarAtleta(@Body atleta: Atleta): Response<Atleta>

    @PUT("atletas/{id}")
    suspend fun editarAtleta(@Path("id") id: Int, @Body atleta: Atleta): Response<Atleta>

    @DELETE("atletas/{id}")
    suspend fun excluirAtleta(@Path("id") id: Int): Response<Map<String, String>>
}