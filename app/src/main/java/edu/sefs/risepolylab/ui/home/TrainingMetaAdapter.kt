package edu.sefs.risepolylab.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.sefs.risepolylab.MainViewModel
import edu.sefs.risepolylab.databinding.HomeRowBinding
import edu.sefs.risepolylab.model.TrainingMeta

class TrainingMetaAdapter (private val viewModel: MainViewModel) : ListAdapter<TrainingMeta, TrainingMetaAdapter.VH>(Diff()){

    class Diff : DiffUtil.ItemCallback<TrainingMeta>() {
        override fun areItemsTheSame(oldItem: TrainingMeta, newItem: TrainingMeta): Boolean {
            return oldItem.fireStoreID == newItem.fireStoreID
        }

        override fun areContentsTheSame(oldItem: TrainingMeta, newItem: TrainingMeta): Boolean {
            return oldItem.fireStoreID == newItem.fireStoreID
                    && oldItem.training == newItem.training
                    && oldItem.status == newItem.status
                    && oldItem.uid == newItem.uid
        }
    }
    inner class VH(private val rowBinding: HomeRowBinding) : RecyclerView.ViewHolder(rowBinding.root){
        fun bind(holder : VH, position : Int){
            val trainingMeta = viewModel.getTrainingMeta(position)
            holder.rowBinding.rowTrainingName.text = trainingMeta.training
            holder.rowBinding.rowTrainingStatus.text = trainingMeta.status
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = HomeRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(holder,position)
    }

    override fun getItemCount(): Int {
        if (viewModel.observeTrainingMetaList().value != null){
            return viewModel.observeTrainingMetaList().value!!.size
        }
        return 0
    }
}