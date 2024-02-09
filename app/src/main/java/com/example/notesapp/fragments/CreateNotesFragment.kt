package com.example.notesapp.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.example.notesapp.R
import com.example.notesapp.database.NotesDatabase
import com.example.notesapp.entities.Notes
import com.example.notesapp.utill.NotesBottomSheetFragment
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


class CreateNotesFragment : BaseFragment(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    var selectedColor = "#EDEDED"
    private var currentDate: String? = null
    private var READ_STORAGE_PERM = 123
    private var REQUEST_CODE_IMAGE = 456
    private var selectedImagePath = ""
    private var webLink = ""
    private var noteId = -1

    // Declare text formatting variables
    private var isBold = false
    private var isItalic = false
    private var isUnderline = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noteId = requireArguments().getInt("noteId", -1)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_notes, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreateNotesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imgMore = view.findViewById<ImageView>(R.id.img_more)
        val imgBack = view.findViewById<ImageView>(R.id.imgBack)
        val imgDone = view.findViewById<ImageView>(R.id.imgDone)
        val etWebLink = view.findViewById<EditText>(R.id.etWebLink)
        val btnOk = view.findViewById<Button>(R.id.btnOk)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val layoutWebUrl = view.findViewById<LinearLayout>(R.id.layoutWebUrl)
        val colorView = view.findViewById<View>(R.id.colorView)
        val etNoteTitle = view.findViewById<EditText>(R.id.etNoteTitle)
        val etNoteDesc = view.findViewById<EditText>(R.id.etNoteDesc)
        val layoutImage = view.findViewById<RelativeLayout>(R.id.layoutImage)
        val tvWebLink = view.findViewById<TextView>(R.id.tvWebLink)
        val imgNote = view.findViewById<ImageView>(R.id.imgNote)
        val imgDelete = view.findViewById<ImageView>(R.id.imgDelete)
        val imgUrlDelete = view.findViewById<ImageView>(R.id.imgUrlDelete)
        val imgText = view.findViewById<ImageView>(R.id.img_text)


        if (noteId != -1) {

            launch {
                context?.let {
                    val notes = NotesDatabase.getDatabase(it).noteDao().getSpecificNote(noteId)
                    colorView.setBackgroundColor(Color.parseColor(notes.color))
                    etNoteTitle.setText(notes.title)
                    etNoteDesc.setText(notes.noteText)
                    if (notes.imgPath != "") {
                        selectedImagePath = notes.imgPath!!
                        Glide.with(requireContext())
                            .load(File(notes.imgPath))
                            .into(imgNote)
                        layoutImage.visibility = View.VISIBLE
                        imgNote.visibility = View.VISIBLE
                        imgDelete.visibility = View.VISIBLE
                        Log.e("ImageError", "Failed to load image :$imgNote")
                    } else {
                        layoutImage.visibility = View.GONE
                        imgNote.visibility = View.GONE
                        imgDelete.visibility = View.GONE
                    }

                    if (notes.webLink != "") {
                        webLink = notes.webLink!!
                        tvWebLink.text = notes.webLink
                        layoutWebUrl.visibility = View.VISIBLE
                        etWebLink.setText(notes.webLink)
                        imgUrlDelete.visibility = View.VISIBLE
                    } else {
                        imgUrlDelete.visibility = View.GONE
                        layoutWebUrl.visibility = View.GONE
                    }
                }
            }
        }
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            BroadcastReceiver, IntentFilter("bottom_sheet_action")
        )

        colorView.setBackgroundColor(Color.parseColor(selectedColor))

        imgDone.setOnClickListener {
            if (noteId != -1) {
                updateNote()
            } else {
                saveNote()
            }
        }

        imgBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        imgMore.setOnClickListener {

            val noteBottomSheetFragment = NotesBottomSheetFragment.newInstance(noteId)
            noteBottomSheetFragment.show(
                requireActivity().supportFragmentManager,
                "Note Bottom Sheet Fragment"
            )
        }

        imgDelete.setOnClickListener {
            selectedImagePath = ""
            layoutImage.visibility = View.GONE

        }

        btnOk.setOnClickListener {
            if (etWebLink.text.toString().trim().isNotEmpty()) {
                checkWebUrl()
            } else {
                Toast.makeText(requireContext(), "Url is Required", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            if (noteId != -1) {
                tvWebLink.visibility = View.VISIBLE
                layoutWebUrl.visibility = View.GONE
            } else {
                layoutWebUrl.visibility = View.GONE
            }

        }

        imgUrlDelete.setOnClickListener {
            webLink = ""
            tvWebLink.visibility = View.GONE
            imgUrlDelete.visibility = View.GONE
            layoutWebUrl.visibility = View.GONE
        }

        tvWebLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(etWebLink.text.toString()))
            startActivity(intent)
        }
        imgText.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_text_format, null)
            val checkBoxBold = dialogView.findViewById<CheckBox>(R.id.checkBoxBold)
            val checkBoxItalic = dialogView.findViewById<CheckBox>(R.id.checkBoxItalic)
            val checkBoxUnderline = dialogView.findViewById<CheckBox>(R.id.checkBoxUnderline)

            checkBoxBold.isChecked = isBold
            checkBoxItalic.isChecked = isItalic
            checkBoxUnderline.isChecked = isUnderline

            AlertDialog.Builder(requireContext())
                .setTitle("Text Formatting")
                .setView(dialogView)
                .setPositiveButton("OK") { dialog, _ ->
                    isBold = checkBoxBold.isChecked
                    isItalic = checkBoxItalic.isChecked
                    isUnderline = checkBoxUnderline.isChecked

                    applyTextFormatting(etNoteDesc, isBold, isItalic, isUnderline)
                    updateTextFormatting(isBold, isItalic, isUnderline)

                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }


    }

    // Method to apply text formatting to the EditText
    private fun applyTextFormatting(
        editText: EditText,
        isBold: Boolean,
        isItalic: Boolean,
        isUnderline: Boolean
    ) {
        val start = editText.selectionStart
        val end = editText.selectionEnd
        val editableText = editText.text

        val spannable = SpannableStringBuilder(editableText)

        if (isBold) {
            spannable.setSpan(
                StyleSpan(android.graphics.Typeface.BOLD),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        if (isItalic) {
            spannable.setSpan(
                StyleSpan(android.graphics.Typeface.ITALIC),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        if (isUnderline) {
            spannable.setSpan(UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        editText.text = spannable
    }

    private fun updateTextFormatting(isBold: Boolean, isItalic: Boolean, isUnderline: Boolean) {
        launch {
            context?.let {
                val notes = NotesDatabase.getDatabase(it).noteDao().getSpecificNote(noteId)

                if (notes != null) {
                    // Update the formatting options
                    notes.isBold = isBold
                    notes.isItalic = isItalic
                    notes.isUnderlined = isUnderline

                    // Update the note in the database
                    try {
                        NotesDatabase.getDatabase(it).noteDao().updateNoteFormatting(notes)
                        Log.d(
                            "UpdateNote",
                            "Note updated successfully with text formatting options"
                        )
                    } catch (e: Exception) {
                        Log.e(
                            "UpdateNote",
                            "Error updating note with text formatting options: ${e.message}"
                        )
                        Toast.makeText(requireContext(), "Error updating note", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Log.e("UpdateTextFormatting", "Notes object is null")
                    // Handle the null case appropriately
                }
            }
        }
    }


    private fun updateNote() {
        val etNoteTitle = view?.findViewById<EditText>(R.id.etNoteTitle)
        val etNoteDesc = view?.findViewById<EditText>(R.id.etNoteDesc)
        val layoutImage = view?.findViewById<RelativeLayout>(R.id.layoutImage)
        val tvWebLink = view?.findViewById<TextView>(R.id.tvWebLink)
        val imgNote = view?.findViewById<ImageView>(R.id.imgNote)

        launch {
            context?.let {
                val notes = NotesDatabase.getDatabase(it).noteDao().getSpecificNote(noteId)

                notes.title = etNoteTitle?.text.toString()
                notes.noteText = etNoteDesc?.text.toString()
                notes.color = selectedColor
                notes.imgPath = selectedImagePath
                notes.webLink = webLink

                // Load the image if there's a path available
                if (!notes.imgPath.isNullOrEmpty()) {
                    val file = File(notes.imgPath!!)
                    if (file.exists()) {
                        Glide.with(requireContext())
                            .load(file)
                            .into(imgNote!!)
                        imgNote.visibility = View.VISIBLE
                    } else {
                        Log.e("UpdateNote", "File does not exist at path: ${notes.imgPath}")
                        imgNote?.visibility = View.GONE
                    }
                } else {
                    imgNote?.visibility = View.GONE
                }

                // Update the note in the database
                try {
                    NotesDatabase.getDatabase(it).noteDao().updateNote(notes)
                    Log.d("UpdateNote", "Note updated successfully")
                } catch (e: Exception) {
                    Log.e("UpdateNote", "Error updating note: ${e.message}")
                    Toast.makeText(requireContext(), "Error updating note", Toast.LENGTH_SHORT)
                        .show()
                }

                // Reset UI elements
                etNoteTitle?.setText("")
                etNoteDesc?.setText("")
                layoutImage?.visibility = View.GONE
                tvWebLink?.visibility = View.GONE

                // Pop the fragment from the back stack
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun saveNote() {
        val etNoteTitle = view?.findViewById<EditText>(R.id.etNoteTitle)
        val etNoteDesc = view?.findViewById<EditText>(R.id.etNoteDesc)
        val layoutImage = view?.findViewById<RelativeLayout>(R.id.layoutImage)
        val tvWebLink = view?.findViewById<TextView>(R.id.tvWebLink)
        val imgNote = view?.findViewById<ImageView>(R.id.imgNote)

        if (etNoteTitle?.text.isNullOrEmpty()) {
            Toast.makeText(context, "Note Title is Required", Toast.LENGTH_SHORT).show()
        } else if (etNoteDesc?.text.isNullOrEmpty()) {
            Toast.makeText(context, "Note Description is Required", Toast.LENGTH_SHORT).show()
        } else {
            launch {
                val notes = Notes()
                notes.title = etNoteTitle?.text.toString()
                notes.noteText = etNoteDesc?.text.toString()
                notes.color = selectedColor
                notes.imgPath = selectedImagePath
                notes.webLink = webLink
                notes.isBold = isBold
                notes.isItalic = isItalic
                notes.isUnderlined = isUnderline

                context?.let {
                    NotesDatabase.getDatabase(it).noteDao().insertNotes(notes)

                    Log.d("SaveNote", "Note saved successfully")


                    etNoteTitle?.setText("")
                    etNoteDesc?.setText("")
                    layoutImage?.visibility = View.GONE
                    imgNote?.visibility = View.GONE
                    tvWebLink?.visibility = View.GONE
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }


    private fun deleteNote() {

        launch {
            context?.let {
                NotesDatabase.getDatabase(it).noteDao().deleteSpecificNote(noteId)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun checkWebUrl() {

        val layoutWebUrl = view?.findViewById<LinearLayout>(R.id.layoutWebUrl)
        val etWebLink = view?.findViewById<EditText>(R.id.etWebLink)
        val tvWebLink = view?.findViewById<TextView>(R.id.tvWebLink)

        if (Patterns.WEB_URL.matcher(etWebLink?.text.toString()).matches()) {
            layoutWebUrl?.visibility = View.GONE
            etWebLink?.isEnabled = false
            webLink = etWebLink?.text.toString()
            tvWebLink?.visibility = View.VISIBLE
            tvWebLink?.text = etWebLink?.text.toString()
        } else {
            Toast.makeText(requireContext(), "Url is not valid", Toast.LENGTH_SHORT).show()
        }
    }


    private val BroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            val actionColor = p1!!.getStringExtra("action")

            val colorView = view!!.findViewById<View>(R.id.colorView)
            val imgNote = view!!.findViewById<ImageView>(R.id.imgNote)
            val layoutImage = view!!.findViewById<RelativeLayout>(R.id.layoutImage)
            val layoutWebUrl = view!!.findViewById<LinearLayout>(R.id.layoutWebUrl)

            when (actionColor!!) {


                "Blue" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))

                }

                "Yellow" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))

                }


                "Purple" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))

                }


                "Green" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))

                }

                "Orange" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))

                }

                "Black" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))

                }

                "Image" -> {
                    pickImageFromGallery()
                    layoutWebUrl.visibility = View.GONE
                }

                "WebUrl" -> {
                    layoutWebUrl.visibility = View.VISIBLE
                }

                "DeleteNote" -> {
                    //delete note
                    deleteNote()
                }

                else -> {
                    layoutImage.visibility = View.GONE
                    imgNote.visibility = View.GONE
                    layoutWebUrl.visibility = View.GONE
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))

                }
            }
        }

    }

    override fun onDestroy() {

        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(BroadcastReceiver)
        super.onDestroy()


    }


    private fun hasReadStoragePerm(): Boolean {
        return EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }


    private fun readStorageTask() {
        if (hasReadStoragePerm()) {


            pickImageFromGallery()
        } else {
            EasyPermissions.requestPermissions(
                requireActivity(),
                getString(R.string.storage_permission_text),
                READ_STORAGE_PERM,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE_IMAGE)
        }
    }

    private fun getPathFromUri(contentUri: Uri): String? {
        val filePath: String?
        val cursor = requireActivity().contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        Log.d("filePath", "" + filePath)
        return filePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
            Log.d("ActivityResult", "Image selection result received")

            val imgNote = view?.findViewById<ImageView>(R.id.imgNote)
            val layoutImage = view?.findViewById<RelativeLayout>(R.id.layoutImage)

            if (data != null) {
                val selectedImageUrl = data.data
                Log.d("ActivityResult", "Selected image URI: $selectedImageUrl")
                if (selectedImageUrl != null) {
                    try {
                        val imagePath = getPathFromUri(selectedImageUrl)
                        Log.d("ActivityResult", "Image path from URI: $imagePath")
                        if (imagePath != null) {
                            val inputStream =
                                requireActivity().contentResolver.openInputStream(selectedImageUrl)
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            imgNote?.setImageBitmap(bitmap)
                            imgNote?.visibility = View.VISIBLE
                            layoutImage?.visibility = View.VISIBLE

                            selectedImagePath = imagePath
                            Log.d("ActivityResult", "Image loaded successfully")
                        } else {
                            Log.e("ActivityResult", "Failed to get image path")
                            Toast.makeText(
                                requireContext(),
                                "Failed to get image path",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Log.e("ActivityResult", "Error loading image: ${e.message}")
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("ActivityResult", "Selected image URI is null")
                    Toast.makeText(
                        requireContext(),
                        "Selected image URI is null",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Log.e("ActivityResult", "Intent data is null")
                Toast.makeText(requireContext(), "Intent data is null", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            requireActivity()
        )
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)) {
            AppSettingsDialog.Builder(requireActivity()).build().show()
        }
        Log.d("permission", "permission granted")

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {


    }

    override fun onRationaleDenied(requestCode: Int) {

    }

    override fun onRationaleAccepted(requestCode: Int) {

    }

}
