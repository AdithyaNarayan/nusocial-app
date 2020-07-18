package com.teamnusocial.nusocial.ui.community

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.teamnusocial.nusocial.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import soup.neumorphism.NeumorphFloatingActionButton


class PostFileEditAdapter(var allFiles: MutableList<Uri>, val isCreate: Boolean,val isEdit: Boolean, val postID: String, val context: Context): RecyclerView.Adapter<PostFileEditAdapter.FileHolder>() {
    class FileHolder(val imageView: ConstraintLayout): RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
        val imageView = LayoutInflater.from(parent.context).inflate(R.layout.post_file_item, parent, false) as ConstraintLayout
        return FileHolder(
            imageView
        )
    }
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.getScheme().equals("content")) {
            val cursor: Cursor = context.getContentResolver().query(uri, null, null, null, null)!!
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor.close()
            }
        }
        if (result == null) {
            result = uri.getPath()
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result
    }

    override fun onBindViewHolder(holder: FileHolder, position: Int) {
        holder.imageView.findViewById<TextView>(R.id.file_name).text = getFileName(allFiles[position])
        if(isCreate || isEdit) {
            holder.imageView.findViewById<Button>(R.id.remove_file).setOnClickListener {
                removeAt(position)
            }
        } else {
            holder.imageView.findViewById<Button>(R.id.remove_file).visibility = View.GONE
        }
        holder.imageView.findViewById<NeumorphFloatingActionButton>(R.id.downloadFileButton).setOnClickListener {
            val request = DownloadManager.Request(Uri.parse(allFiles[position].toString()))

            request.setTitle(getFileName(allFiles[position]))
            request.setDescription("Downloading file from NUSocial Servers")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            val reference =
                (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(
                    request
                )
        }
    }
    fun removeAt(position: Int) {
        val filePath = allFiles[position].toString()
        allFiles.removeAt(position)
        var start_index = 0
        var end_index = 0
        var count = 0
        for (i in 0 until filePath.length) {
            if (filePath[i] == '%') {
                if (count == 0) count++;
                else if (count == 1) start_index = i + 3;
            } else if (filePath[i] == '?') {
                end_index = i;
                break;
            }
        }
        Log.d("TEST_IMG_PATH", "HERE is ${filePath.substring(start_index, end_index)}")
        if(isEdit && postID != "") {
            CoroutineScope(Dispatchers.IO).launch {
                FirebaseStorage.getInstance().getReference("/post_files/${postID}/${filePath.substring(start_index, end_index)}").delete()
            }
        }
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, allFiles.size)
    }

    override fun getItemCount() = allFiles.size
}