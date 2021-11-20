package com.example.vsdapp.views

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.MotionEventCompat
import androidx.databinding.BindingAdapter
import com.example.vsdapp.R
import kotlinx.android.synthetic.main.pictogram_view.view.*

class PictogramView: LinearLayout {

    constructor(context: Context): super(context) {
        initialSetup(context)
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        initialSetup(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr){
        initialSetup(context)
    }

    data class Data(
        val id: Int,
        val imageUrl: String
    )

    lateinit var pictogramData: Data
    val label: EditText
        get() = pictogramTitleAtPictogramView

    private fun initialSetup(context: Context) {
        View.inflate(context, R.layout.pictogram_view, this)
    }

    fun setDeleteButtonListener(method: ((Int) -> Unit)?) {
        deleteButtonAtPictogramView.setOnClickListener {
            linearLayoutAtPictogramView.visibility = View.GONE
            method?.invoke(pictogramData.id)
        }
    }

    fun setDetails(details: Data?){
        details?.let { update(it) }
    }

    private fun update(data: Data) {
        pictogramData = data
    }

    fun setLabelEditTextListener(method: ((Int, String) -> Unit)?) {
        pictogramTitleAtPictogramView.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                method?.invoke(pictogramData.id, text.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setupMoveView(method: ((Int, Int, Int) -> Unit)?) {
        linearLayoutAtPictogramView.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    true
                }
                DragEvent.ACTION_DROP -> {
                    val item: ClipData.Item = event.clipData.getItemAt(0)
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    when(event.result) {
                        true ->
                            Toast.makeText(context, "The drop was handled.", Toast.LENGTH_LONG)
                        else ->
                            Toast.makeText(context, "The drop didn't work.", Toast.LENGTH_LONG)
                    }.show()
                    true
                }
                else -> {
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.")
                    false
                }
            }

            true
        }
    }
}