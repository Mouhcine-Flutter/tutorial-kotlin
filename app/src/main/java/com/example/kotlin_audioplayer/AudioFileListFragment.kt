package com.example.kotlin_audioplayer


import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin_audioplayer.databinding.FragmentAudioFileListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AudioFileListFragment : Fragment() {
    private val viewModel by
    activityViewModels<AudioFilesViewModel>()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        querySongs()
        val binding = FragmentAudioFileListBinding.inflate(
            inflater,
            container,
            false
        )

        binding.audioFileList.layoutManager = LinearLayoutManager(binding.root.context)
        val adapter = AudioFileListAdapter(viewLifecycleOwner)

        viewModel.audioFiles.observe(viewLifecycleOwner, adapter::submitList)
        binding.audioFileList.adapter = adapter

        return binding.root
    }


    @SuppressLint("Recycle")
    @RequiresApi(Build.VERSION_CODES.R)
    fun querySongs() = GlobalScope.launch(Dispatchers.IO) {
        val audioList = mutableListOf<AudioFile>()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
        )

        val c = requireActivity().contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )
        c?.use { cursor ->
            val columnIdx = cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val titleIdx = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistIdx = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumIdx = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationIdx = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(columnIdx)
                val title = cursor.getString(titleIdx)
                val artist = cursor.getString(artistIdx)
                val album = cursor.getString(albumIdx)
                val duration = cursor.getInt(durationIdx)

                val uri: Uri = ContentUris.withAppendedId(uri, id)
                audioList.add(AudioFile(id, title, artist, album, duration, uri))
            }
            viewModel.audioFiles.postValue(
                audioList
            )
        }
    }
}



