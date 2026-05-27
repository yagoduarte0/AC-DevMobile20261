package com.example.jiujitsuapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jiujitsuapp.databinding.ItemAtletaBinding
import com.example.jiujitsuapp.model.Atleta

class AtletaAdapter(
    private val onEditar: (Atleta) -> Unit,
    private val onExcluir: (Atleta) -> Unit
) : ListAdapter<Atleta, AtletaAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val binding: ItemAtletaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(atleta: Atleta) {
            binding.tvNome.text     = atleta.nome
            binding.tvFaixa.text    = "🥋 Faixa: ${atleta.faixa}"
            binding.tvAcademia.text = "🏫 ${atleta.academia}"

            val cor = when (atleta.faixa.lowercase()) {
                "branca"  -> 0xFFFFFFFF
                "cinza"   -> 0xFFBDBDBD
                "amarela" -> 0xFFFFF176
                "laranja" -> 0xFFFFCC80
                "verde"   -> 0xFFA5D6A7
                "azul"    -> 0xFF90CAF9
                "roxa"    -> 0xFFCE93D8
                "marrom"  -> 0xFFBCAAA4
                "preta"   -> 0xFF424242
                else      -> 0xFFFFFFFF
            }
            binding.cardView.setCardBackgroundColor(cor.toInt())

            val corTexto = if (atleta.faixa.lowercase() in listOf("preta", "marrom", "roxa"))
                0xFFFFFFFF.toInt() else 0xFF212121.toInt()

            binding.tvNome.setTextColor(corTexto)
            binding.tvFaixa.setTextColor(corTexto)
            binding.tvAcademia.setTextColor(corTexto)

            binding.btnEditar.setOnClickListener  { onEditar(atleta) }
            binding.btnExcluir.setOnClickListener { onExcluir(atleta) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemAtletaBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Atleta>() {
            override fun areItemsTheSame(a: Atleta, b: Atleta) = a.id == b.id
            override fun areContentsTheSame(a: Atleta, b: Atleta) = a == b
        }
    }
}