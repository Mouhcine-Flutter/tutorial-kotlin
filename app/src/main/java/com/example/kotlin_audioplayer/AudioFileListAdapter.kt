package com.example.kotlin_audioplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_audioplayer.databinding.AudioFileItemBinding

class AudioFileListAdapter(
    private val fileList: List<AudioFile>,
    private val lifecycleOwner: LifecycleOwner
): RecyclerView.Adapter<AudioFileListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = AudioFileItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val file = fileList[position]
        holder.bind(file)
    }
    override fun getItemCount(): Int = fileList.size
    inner class ViewHolder(
        binding: AudioFileItemBinding) : RecyclerView.ViewHolder(binding.root) {
            private val viewModel = MutableLiveData<AudioFile>()
            init {
                binding.audioFileVM = viewModel
                binding.lifecycleOwner = lifecycleOwner
            }
            fun bind(audioFile: AudioFile) =
                viewModel.postValue(audioFile)
        }
}

