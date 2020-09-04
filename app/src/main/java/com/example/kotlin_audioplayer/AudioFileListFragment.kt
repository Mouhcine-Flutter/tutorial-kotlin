package com.example.kotlin_audioplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin_audioplayer.databinding.FragmentAudioFileListBinding


class AudioFileListFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fakeList = listOf(
            AudioFile(1, "titre1","artiste1","album1"),
            AudioFile(2, "titre2","artiste2","album2")
        )
        val binding = FragmentAudioFileListBinding.inflate(
            inflater,
            container,
            false
        )

        binding.audioFileList.layoutManager = LinearLayoutManager(binding.root.context)
        binding.audioFileList.adapter = AudioFileListAdapter(fakeList, viewLifecycleOwner)

        return binding.root
    }
}