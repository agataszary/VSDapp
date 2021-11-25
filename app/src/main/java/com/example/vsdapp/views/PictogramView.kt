package com.example.vsdapp.views

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.os.Build
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
import android.widget.RelativeLayout
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
        this.setOnLongClickListener { v ->
            val item = ClipData.Item(v.tag as? CharSequence)
            val dragData = ClipData(
                v.tag as? CharSequence,
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item)

            val myShadow = DragShadowBuilder(this)


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                v.startDragAndDrop(dragData, myShadow, v, 0)
            } else {
                v.startDrag(dragData, myShadow, v, 0)
            }

            v.visibility = View.INVISIBLE
            true
        }
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
}