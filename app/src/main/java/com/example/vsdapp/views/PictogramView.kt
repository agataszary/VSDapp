package com.example.vsdapp.views

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.view.size
import androidx.core.view.updateLayoutParams
import com.example.vsdapp.R
import com.example.vsdapp.databinding.PictogramViewBinding

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
    lateinit var binding: PictogramViewBinding
    val label: EditText
        get() = binding.pictogramTitleAtPictogramView

    private var readyToMove = true

    private fun initialSetup(context: Context) {
        binding = PictogramViewBinding.inflate(LayoutInflater.from(context), this, true)
        this.setOnLongClickListener { v ->
            if (readyToMove) {
                val item = ClipData.Item(v.tag as? CharSequence)
                val dragData = ClipData(
                    v.tag as? CharSequence,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    item
                )

                val myShadow = DragShadowBuilder(this)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    v.startDragAndDrop(dragData, myShadow, v, 0)
                } else {
                    v.startDrag(dragData, myShadow, v, 0)
                }

                v.visibility = View.INVISIBLE
            }
            true
        }

    }

    private var mScaleFactor = 1f

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            println("Dupa ${detector.scaleFactor}")
            mScaleFactor *= detector.scaleFactor

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.8f, Math.min(mScaleFactor, 1.1f))

            println(mScaleFactor)

//            binding.linearLayoutAtPictogramView.updateLayoutParams {
//                width = (width * mScaleFactor).toInt()
//                height = (height * mScaleFactor).toInt()
//            }

            if (mScaleFactor < 1f && binding.linearLayoutAtPictogramView.width >= 230 || mScaleFactor > 1f && binding.linearLayoutAtPictogramView.width <= 350){
                println("Zmieniam rozmiar")
                binding.linearLayoutAtPictogramView.layoutParams =
                    LayoutParams((width * mScaleFactor).toInt(), (height * mScaleFactor).toInt())
//                binding.imageAtPictogramView.layoutParams =
//                    LayoutParams((width * mScaleFactor).toInt(), (height * mScaleFactor).toInt())
//                binding.pictogramTitleAtPictogramView.layoutParams =
//                    LayoutParams((width * mScaleFactor).toInt(), height)
            }
            println(binding.linearLayoutAtPictogramView.width)

            invalidate()
            return true
        }
    }

    private val gestureListener = object: GestureDetector.SimpleOnGestureListener() {
        override fun onLongPress(e: MotionEvent) {
//            super.onLongPress(e)
            val item = ClipData.Item(this@PictogramView.tag as? CharSequence)
            val dragData = ClipData(
                this@PictogramView.tag as? CharSequence,
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item
            )

            val myShadow = DragShadowBuilder(this@PictogramView)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                this@PictogramView.startDragAndDrop(dragData, myShadow, this@PictogramView, 0)
            } else {
                this@PictogramView.startDrag(dragData, myShadow, this@PictogramView, 0)
            }

            this@PictogramView.visibility = View.INVISIBLE
        }
    }

    private val mScaleDetector = ScaleGestureDetector(context, scaleListener)
    private val mGestureDetector = GestureDetector(context, gestureListener)


    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        readyToMove = event?.pointerCount != 2
//        println(readyToMove)
//        performLongClick()
//        return true

//        if (event?.pointerCount == 2){
//            readyToMove = false
//            mScaleDetector.onTouchEvent(event)
//        } else if (event?.pointerCount == 1) {
////           if (readyToMove) readyToMove = true
//            super.onTouchEvent(event)
//        } else if (event?.action == MotionEvent.ACTION_UP) {
//            readyToMove = true
//        }
//        if (event?.pointerCount == 2) mScaleDetector.onTouchEvent(event) else super.onTouchEvent(event)
        event?.let {
            mScaleDetector.onTouchEvent(event)
            mGestureDetector.onTouchEvent(event)
//            super.onTouchEvent(event)
        }
//        if (event != null) {
//            mScaleDetector.onTouchEvent(event)
//        }

        return true
    }

    fun setDeleteButtonListener(method: ((Int) -> Unit)?) {
        binding.deleteButtonAtPictogramView.setOnClickListener {
            binding.linearLayoutAtPictogramView.visibility = View.GONE
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
        binding.pictogramTitleAtPictogramView.addTextChangedListener(object: TextWatcher {
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