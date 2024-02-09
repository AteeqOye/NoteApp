package com.example.notesapp.adapters

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.notesapp.R
import com.example.notesapp.entities.Notes
import java.io.File


class NotesAdapter : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {
    var listener: OnItemClickListener? = null
    var arrList = ArrayList<Notes>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rv_notes, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrList.size
    }

    fun setData(arrNotesList: List<Notes>) {
        arrList = arrNotesList as ArrayList<Notes>
    }

    fun setOnClickListener(listener1: OnItemClickListener) {
        listener = listener1
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val tvTitle = holder.itemView.findViewById<TextView>(R.id.tvTitle)
        val tvDesc = holder.itemView.findViewById<TextView>(R.id.tvDesc)
        val imgNote = holder.itemView.findViewById<ImageView>(R.id.imgNote)
        val tvWebLink = holder.itemView.findViewById<TextView>(R.id.tvWebLink)
        val cardView = holder.itemView.findViewById<CardView>(R.id.cardView)

        tvTitle.text = arrList[position].title

        val noteText = arrList[position].noteText ?: ""
        val formattedText = formatText(noteText, arrList[position])
        tvDesc.text = formattedText

        if (arrList[position].color != null) {
            cardView.setCardBackgroundColor(Color.parseColor(arrList[position].color))
        } else {
            cardView.setCardBackgroundColor(Color.parseColor(R.color.ColorLightGray.toString()))
        }

        if (!arrList[position].imgPath.isNullOrEmpty()) {
            try {
                val file = arrList[position].imgPath?.let { File(it) }
                if (file != null) {
                    if (file.exists()) {
                        Glide.with(holder.itemView.context)
                            .load(file)
                            .into(imgNote)
                        imgNote.visibility = View.VISIBLE
                    } else {
                        Log.e("ImageError", "File does not exist at path: ${arrList[position].imgPath}")
                        imgNote.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                Log.e("ImageError", "Failed to load image", e)
                imgNote.visibility = View.GONE
            }
        } else {
            imgNote.visibility = View.GONE
        }

        if (!arrList[position].webLink.isNullOrEmpty()) {
            tvWebLink.text = arrList[position].webLink
            tvWebLink.visibility = View.VISIBLE
        } else {
            tvWebLink.visibility = View.GONE
        }

        cardView.setOnClickListener {
            listener?.onClicked(arrList[position].id ?: -1)
        }
    }

    private fun formatText(text: String, note: Notes): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder(text)
        if (note.isBold) {
            spannableStringBuilder.setSpan(StyleSpan(Typeface.BOLD), 0, text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
        if (note.isItalic) {
            spannableStringBuilder.setSpan(StyleSpan(Typeface.ITALIC), 0, text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
        if (note.isUnderlined) {
            spannableStringBuilder.setSpan(UnderlineSpan(), 0, text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
        return spannableStringBuilder
    }

    class NotesViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface OnItemClickListener {
        fun onClicked(noteId: Int)
    }
}
