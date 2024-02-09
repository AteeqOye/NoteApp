package com.example.notesapp.fragments

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.adapters.NotesAdapter
import com.example.notesapp.database.NotesDatabase
import com.example.notesapp.entities.Notes
import com.google.android.material.floatingactionbutton.FloatingActionButton
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.coroutines.launch
import java.util.Locale

class HomeFragment : BaseFragment() {

    private var arrNotes = ArrayList<Notes>()
    private var notesAdapter: NotesAdapter = NotesAdapter()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var gridLayoutManager: GridLayoutManager
    private var isGridLayout = false

    private lateinit var sharedPreferences: SharedPreferences
    private val LAYOUT_PREF_KEY = "layout_preference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val recycler_view = view.findViewById<RecyclerView>(R.id.recycler_view)
        val switchLayoutButton = view.findViewById<ImageButton>(R.id.switch_button)
        val search_view = view.findViewById<EditText>(R.id.search_view)
        val fabBtnCreateNote = view.findViewById<FloatingActionButton>(R.id.fabBtnCreateNote)

        recycler_view.setHasFixedSize(true)

        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        gridLayoutManager = GridLayoutManager(context, 2)

        // Retrieve the layout state from SharedPreferences
        isGridLayout = sharedPreferences.getBoolean(LAYOUT_PREF_KEY, false)
        setLayoutManager(isGridLayout, recycler_view, switchLayoutButton)

        switchLayoutButton.setOnClickListener {
            // Toggle between GridLayout and LinearLayout
            isGridLayout = !isGridLayout

            // Save the layout state to SharedPreferences
            sharedPreferences.edit().putBoolean(LAYOUT_PREF_KEY, isGridLayout).apply()

            // Set the layout manager based on the toggle state
            setLayoutManager(isGridLayout, recycler_view, switchLayoutButton)

        }

        launch {
            context?.let {
                arrNotes = ArrayList(NotesDatabase.getDatabase(it).noteDao().getAllNotes())
                notesAdapter.setData(arrNotes)
                recycler_view.adapter = notesAdapter

                // Logging the size of the notes list
                Log.d("HomeFragment", "Number of notes: ${arrNotes.size}")

                val position = 1

                // Check if the position is within the bounds of the list
                if (position >= 0 && position < arrNotes.size) {
                    // Access the image path of the note at the specified position
                    val imagePath = arrNotes[position].imgPath

                    // Log the image path
                    Log.d("HomeFragment", "Image path at position $position: $imagePath")
                } else {
                    Log.d("HomeFragment", "Invalid position or empty list")
                }
            }
        }


        notesAdapter.setOnClickListener(onClick)

        fabBtnCreateNote.setOnClickListener {
            replaceFragment(CreateNotesFragment.newInstance(), true)
        }



        search_view.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for your case
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed for your case
            }

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString().toLowerCase(Locale.getDefault())

                val tempArr = ArrayList<Notes>()

                for (arr in arrNotes) {
                    if (arr.title?.toLowerCase(Locale.getDefault())?.contains(searchText) == true) {
                        tempArr.add(arr)
                    }
                }

                notesAdapter.setData(tempArr)
                notesAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun setLayoutManager(
        isGridLayout: Boolean,
        recyclerView: RecyclerView,
        switchButton: ImageButton
    ) {
        if (isGridLayout) {
            recyclerView.layoutManager = gridLayoutManager
            switchButton.setImageResource(R.drawable.ic_grid_view)
        } else {
            recyclerView.layoutManager = linearLayoutManager
            switchButton.setImageResource(R.drawable.ic_linear_list)

        }
    }


    private val onClick = object :NotesAdapter.OnItemClickListener{
        override fun onClicked(noteId: Int) {

            var fragment: Fragment
            var bundle = Bundle()
            bundle.putInt("noteId",noteId)
            fragment = CreateNotesFragment.newInstance()
            fragment.arguments = bundle

            replaceFragment(fragment, false)
        }

    }

    private fun replaceFragment(fragment: Fragment, isTransition: Boolean) {
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()

        if (isTransition) {
            fragmentTransition.setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        }
        fragmentTransition.replace(R.id.frame_layout, fragment)
            .addToBackStack(fragment.javaClass.simpleName).commit()
    }
}
