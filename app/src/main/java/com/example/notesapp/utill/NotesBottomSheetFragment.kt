package com.example.notesapp.utill

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.notesapp.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NotesBottomSheetFragment : BottomSheetDialogFragment() {

    var selectedColor = "#171C26"

    companion object {
        var noteId = -1
        fun newInstance(id: Int): NotesBottomSheetFragment {
            val args = Bundle()
            val fragment = NotesBottomSheetFragment()
            fragment.arguments = args
            noteId = id
            return fragment
        }
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        val view = LayoutInflater.from(context).inflate(R.layout.fragment_notes_bottom_sheet, null)
        dialog.setContentView(view)

        val param = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams

        val behavior = param.behavior

        if (behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    TODO("Not yet implemented")
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    var state = ""
                    when (newState) {
                        BottomSheetBehavior.STATE_DRAGGING -> {
                            state = "DRAGGING"
                        }

                        BottomSheetBehavior.STATE_SETTLING -> {
                            state = "SETTLING"
                        }

                        BottomSheetBehavior.STATE_EXPANDED -> {
                            state = "EXPANDED"
                        }

                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            state = "COLLAPSED"
                        }

                        BottomSheetBehavior.STATE_HIDDEN -> {
                            state = "HIDDEN"
                            dismiss()
                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }

                    }
                }

            })


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_notes_bottom_sheet, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
    }

    private fun setListener() {
        val fNote1 = view?.findViewById<FrameLayout>(R.id.fNote1)
        fNote1?.setOnClickListener {

            val imgNote1 = view?.findViewById<ImageView>(R.id.imgNote1)
            val imgNote2 = view?.findViewById<ImageView>(R.id.imgNote2)
            val imgNote4 = view?.findViewById<ImageView>(R.id.imgNote4)
            val imgNote5 = view?.findViewById<ImageView>(R.id.imgNote5)
            val imgNote6 = view?.findViewById<ImageView>(R.id.imgNote6)
            val imgNote7 = view?.findViewById<ImageView>(R.id.imgNote7)

            imgNote1?.setImageResource(R.drawable.ic_tick)
            imgNote2?.setImageResource(0)
            imgNote4?.setImageResource(0)
            imgNote5?.setImageResource(0)
            imgNote6?.setImageResource(0)
            imgNote7?.setImageResource(0)
            selectedColor = "#4e33ff"

            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action", "Blue")
            intent.putExtra("selectedColor", selectedColor)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)

        }

        val fNote2 = view?.findViewById<FrameLayout>(R.id.fNote2)
        fNote2?.setOnClickListener {

            val imgNote1 = view?.findViewById<ImageView>(R.id.imgNote1)
            val imgNote2 = view?.findViewById<ImageView>(R.id.imgNote2)
            val imgNote4 = view?.findViewById<ImageView>(R.id.imgNote4)
            val imgNote5 = view?.findViewById<ImageView>(R.id.imgNote5)
            val imgNote6 = view?.findViewById<ImageView>(R.id.imgNote6)
            val imgNote7 = view?.findViewById<ImageView>(R.id.imgNote7)

            imgNote1?.setImageResource(0)
            imgNote2?.setImageResource(R.drawable.ic_tick)
            imgNote4?.setImageResource(0)
            imgNote5?.setImageResource(0)
            imgNote6?.setImageResource(0)
            imgNote7?.setImageResource(0)
            selectedColor = "#ffd633"

            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action", "Yellow")
            intent.putExtra("selectedColor", selectedColor)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)

        }

        val fNote4 = view?.findViewById<FrameLayout>(R.id.fNote4)
        fNote4?.setOnClickListener {

            val imgNote1 = view?.findViewById<ImageView>(R.id.imgNote1)
            val imgNote2 = view?.findViewById<ImageView>(R.id.imgNote2)
            val imgNote4 = view?.findViewById<ImageView>(R.id.imgNote4)
            val imgNote5 = view?.findViewById<ImageView>(R.id.imgNote5)
            val imgNote6 = view?.findViewById<ImageView>(R.id.imgNote6)
            val imgNote7 = view?.findViewById<ImageView>(R.id.imgNote7)

            imgNote1?.setImageResource(0)
            imgNote2?.setImageResource(0)
            imgNote4?.setImageResource(R.drawable.ic_tick)
            imgNote5?.setImageResource(0)
            imgNote6?.setImageResource(0)
            imgNote7?.setImageResource(0)
            selectedColor = "#ae3b76"

            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action", "Purple")
            intent.putExtra("selectedColor", selectedColor)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)

        }

        val fNote5 = view?.findViewById<FrameLayout>(R.id.fNote5)
        fNote5?.setOnClickListener {

            val imgNote1 = view?.findViewById<ImageView>(R.id.imgNote1)
            val imgNote2 = view?.findViewById<ImageView>(R.id.imgNote2)
            val imgNote4 = view?.findViewById<ImageView>(R.id.imgNote4)
            val imgNote5 = view?.findViewById<ImageView>(R.id.imgNote5)
            val imgNote6 = view?.findViewById<ImageView>(R.id.imgNote6)
            val imgNote7 = view?.findViewById<ImageView>(R.id.imgNote7)

            imgNote1?.setImageResource(0)
            imgNote2?.setImageResource(0)
            imgNote4?.setImageResource(0)
            imgNote5?.setImageResource(R.drawable.ic_tick)
            imgNote6?.setImageResource(0)
            imgNote7?.setImageResource(0)
            selectedColor = "#0aebaf"

            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action", "Green")
            intent.putExtra("selectedColor", selectedColor)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }

        val fNote6 = view?.findViewById<FrameLayout>(R.id.fNote6)
        fNote6?.setOnClickListener {

            val imgNote1 = view?.findViewById<ImageView>(R.id.imgNote1)
            val imgNote2 = view?.findViewById<ImageView>(R.id.imgNote2)
            val imgNote4 = view?.findViewById<ImageView>(R.id.imgNote4)
            val imgNote5 = view?.findViewById<ImageView>(R.id.imgNote5)
            val imgNote6 = view?.findViewById<ImageView>(R.id.imgNote6)
            val imgNote7 = view?.findViewById<ImageView>(R.id.imgNote7)

            imgNote1?.setImageResource(0)
            imgNote2?.setImageResource(0)
            imgNote4?.setImageResource(0)
            imgNote5?.setImageResource(0)
            imgNote6?.setImageResource(R.drawable.ic_tick)
            imgNote7?.setImageResource(0)
            selectedColor = "#ff7746"
            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action", "Orange")
            intent.putExtra("selectedColor", selectedColor)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }

        val fNote7 = view?.findViewById<FrameLayout>(R.id.fNote7)
        fNote7?.setOnClickListener {

            val imgNote1 = view?.findViewById<ImageView>(R.id.imgNote1)
            val imgNote2 = view?.findViewById<ImageView>(R.id.imgNote2)
            val imgNote4 = view?.findViewById<ImageView>(R.id.imgNote4)
            val imgNote5 = view?.findViewById<ImageView>(R.id.imgNote5)
            val imgNote6 = view?.findViewById<ImageView>(R.id.imgNote6)
            val imgNote7 = view?.findViewById<ImageView>(R.id.imgNote7)

            imgNote1?.setImageResource(0)
            imgNote2?.setImageResource(0)
            imgNote4?.setImageResource(0)
            imgNote5?.setImageResource(0)
            imgNote6?.setImageResource(0)
            imgNote7?.setImageResource(R.drawable.ic_tick)
            selectedColor = "#202734"

            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action", "Black")
            intent.putExtra("selectedColor", selectedColor)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }

        val layoutImage = view?.findViewById<LinearLayout>(R.id.layoutImage)
        layoutImage?.setOnClickListener {
            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action", "Image")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            dismiss()
        }
        val layoutWebUrl = view?.findViewById<LinearLayout>(R.id.layoutWebUrlBs)
        layoutWebUrl?.setOnClickListener{
            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action","WebUrl")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            dismiss()
        }
        val layoutDeleteNote = view?.findViewById<LinearLayout>(R.id.layoutDeleteNote)
        layoutDeleteNote?.setOnClickListener {
            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action","DeleteNote")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            dismiss()
        }
    }
}