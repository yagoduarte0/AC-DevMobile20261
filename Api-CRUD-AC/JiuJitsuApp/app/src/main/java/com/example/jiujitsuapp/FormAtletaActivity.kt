package com.example.jiujitsuapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.jiujitsuapp.databinding.ActivityFormAtletaBinding
import com.example.jiujitsuapp.model.Atleta
import com.example.jiujitsuapp.network.RetrofitClient
import kotlinx.coroutines.launch

class FormAtletaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormAtletaBinding
    private var atletaId: Int = 0

    private val faixas = listOf(
        "Branca", "Cinza", "Amarela", "Laranja",
        "Verde", "Azul", "Roxa", "Marrom", "Preta"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormAtletaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spinnerAdapter = android.widget.ArrayAdapter(
            this, android.R.layout.simple_spinner_item, faixas
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        binding.spinnerFaixa.adapter = spinnerAdapter

        atletaId = intent.getIntExtra("id", 0)
        if (atletaId != 0) {
            binding.etNome.setText(intent.getStringExtra("nome"))
            binding.etAcademia.setText(intent.getStringExtra("academia"))
            val faixaAtual = intent.getStringExtra("faixa") ?: "Branca"
            binding.spinnerFaixa.setSelection(faixas.indexOf(faixaAtual).coerceAtLeast(0))
            binding.btnSalvar.text = "Salvar Alterações"
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = if (atletaId == 0) "Novo Atleta" else "Editar Atleta"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnSalvar.setOnClickListener { salvar() }
    }

    private fun salvar() {
        val nome     = binding.etNome.text.toString().trim()
        val faixa    = binding.spinnerFaixa.selectedItem.toString()
        val academia = binding.etAcademia.text.toString().trim()

        if (nome.isEmpty()) { binding.etNome.error = "Informe o nome"; return }
        if (academia.isEmpty()) { binding.etAcademia.error = "Informe a academia"; return }

        binding.btnSalvar.isEnabled = false
        val atleta = Atleta(nome = nome, faixa = faixa, academia = academia)

        lifecycleScope.launch {
            try {
                val response = if (atletaId == 0)
                    RetrofitClient.api.cadastrarAtleta(atleta)
                else
                    RetrofitClient.api.editarAtleta(atletaId, atleta)

                if (response.isSuccessful) {
                    Toast.makeText(this@FormAtletaActivity,
                        if (atletaId == 0) "Atleta cadastrado!" else "Atleta atualizado!",
                        Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@FormAtletaActivity, "Erro ao salvar", Toast.LENGTH_SHORT).show()
                    binding.btnSalvar.isEnabled = true
                }
            } catch (e: Exception) {
                Toast.makeText(this@FormAtletaActivity, "Sem conexão", Toast.LENGTH_SHORT).show()
                binding.btnSalvar.isEnabled = true
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}