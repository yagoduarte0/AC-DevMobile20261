package com.example.jiujitsuapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jiujitsuapp.databinding.ActivityMainBinding
import com.example.jiujitsuapp.network.RetrofitClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: AtletaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "🥋 Atletas de Jiu-Jitsu"

        adapter = AtletaAdapter(
            onEditar = { atleta ->
                startActivity(Intent(this, FormAtletaActivity::class.java).apply {
                    putExtra("id", atleta.id)
                    putExtra("nome", atleta.nome)
                    putExtra("faixa", atleta.faixa)
                    putExtra("academia", atleta.academia)
                })
            },
            onExcluir = { atleta ->
                AlertDialog.Builder(this)
                    .setTitle("Excluir atleta")
                    .setMessage("Deseja excluir ${atleta.nome}?")
                    .setPositiveButton("Sim") { _, _ -> excluir(atleta.id) }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.fabAdicionar.setOnClickListener {
            startActivity(Intent(this, FormAtletaActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        carregarAtletas()
    }

    private fun carregarAtletas() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.listarAtletas()
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    adapter.submitList(lista)
                    binding.tvVazio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
                } else {
                    toast("Erro ao carregar atletas")
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                toast("Sem conexão com o servidor")
            }
        }
    }

    private fun excluir(id: Int) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.excluirAtleta(id)
                if (response.isSuccessful) {
                    toast("Atleta excluído!")
                    carregarAtletas()
                } else {
                    toast("Erro ao excluir")
                }
            } catch (e: Exception) {
                toast("Sem conexão com o servidor")
            }
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}