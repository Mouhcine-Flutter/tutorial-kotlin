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


class AudioFileListFragment: Fragment() {
    private val viewModel by
    activityViewModels<AudioFilesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.audioFiles.postValue(
            listOf(
                AudioFile(1, "Rising Force","Malmsteen","ODYSSEY", 185),
                AudioFile(2, "Hold On","Malmsteen","ODYSSEY", 260),
                AudioFile(3, "Heaven Tonight","Malmsteen","ODYSSEY", 190)
            )
        )
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

        val query = Application().contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )
        query?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

                val thisId: Long = cursor.getLong(idColumn)
                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    thisId
                )
                do {
                    val thisId: Long = cursor.getLong(idColumn)
                    val thisTitle: String = cursor.getString(titleColumn)
                    val thisArtist: String = cursor.getString(artistColumn)
                    val thisAlbum: String = cursor.getString(albumColumn)
                    val thisDuration: Int = cursor.getInt(durationColumn)
                    audioList.add(
                        AudioFile(thisId, thisTitle, thisArtist, thisAlbum, thisDuration)
                    )
                } while (cursor.moveToNext())
            }
            cursor.close()
            val adapter = AudioFileListAdapter(viewLifecycleOwner)
            adapter.notifyDataSetChanged()
        }
    }
}