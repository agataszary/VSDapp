package com.example.vsdapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.example.vsdapp.R
import kotlinx.android.synthetic.main.read_pictogram_view.view.*

class ReadPictogramView: LinearLayout {
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
        val label: String
    )

    lateinit var pictogramData: Data

    private fun initialSetup(context: Context){
        View.inflate(context, R.layout.read_pictogram_view, this)
    }

    fun setDetails(details: Data?) {
        details?.let { update(it) }
    }

    private fun update(data: Data){
        pictogramData = data
        pictogramTitleAtReadPictogramView.text = data.label
    }

    fun setOnClickListener(method: (String) -> Unit) {
        linearLayoutAtReadPictogramView.setOnClickListener {
            method.invoke(pictogramData.label)
        }
    }
}