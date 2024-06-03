package edu.sefs.risepolylab.ui.searchinv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.sefs.risepolylab.MainViewModel
import edu.sefs.risepolylab.databinding.ChemRowBinding
import edu.sefs.risepolylab.model.ChemMeta
import androidx.navigation.fragment.findNavController

class ChemMetaAdapter (private val viewModel: MainViewModel,
                       private val navtoOneChemical : (String)->Unit) : ListAdapter<ChemMeta, ChemMetaAdapter.VH>(
    Diff()
){

    class Diff : DiffUtil.ItemCallback<ChemMeta>() {
        override fun areItemsTheSame(oldItem: ChemMeta, newItem: ChemMeta): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
        }

        override fun areContentsTheSame(oldItem: ChemMeta, newItem: ChemMeta): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
                    && oldItem.chemicalName == newItem.chemicalName
                    && oldItem.cas == newItem.cas
                    && oldItem.location == newItem.location
                    && oldItem.contentSize == newItem.contentSize
        }
    }
    inner class VH(private val rowBinding: ChemRowBinding) : RecyclerView.ViewHolder(rowBinding.root){
        fun bind(holder : VH, position : Int){
            val chemMeta = viewModel.getChemMeta(position)
            holder.rowBinding.rowChemicalName.text = chemMeta.chemicalName
            holder.rowBinding.rowChemicalCAS.text = chemMeta.cas
            holder.rowBinding.rowContentSize.text = chemMeta.contentSize
            holder.rowBinding.rowChemicalLocation.text = chemMeta.location
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = ChemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(holder,position)
        holder.itemView.setOnClickListener {
            navtoOneChemical(viewModel.getChemMeta(position).cas)
        }
    }

    override fun getItemCount(): Int {
        if (viewModel.observeChemicalMetaList().value != null){
            return viewModel.observeChemicalMetaList().value!!.size
        }
        return 0
    }
}