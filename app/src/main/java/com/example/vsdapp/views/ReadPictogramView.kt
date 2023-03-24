package com.example.vsdapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.example.vsdapp.R
import com.example.vsdapp.databinding.ReadPictogramViewBinding

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
    lateinit var binding: ReadPictogramViewBinding

    private fun initialSetup(context: Context){
        binding = ReadPictogramViewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setDetails(details: Data?) {
        details?.let { update(it) }
    }

    private fun update(data: Data){
        pictogramData = data
        binding.pictogramTitleAtReadPictogramView.text = data.label
    }

    fun setOnClickListener(method: (String) -> Unit) {
       binding.linearLayoutAtReadPictogramView.setOnClickListener {
            method.invoke(pictogramData.label)
        }
    }

    fun showPictogram() {
        binding.linearLayoutAtReadPictogramView.visibility = View.VISIBLE
    }

    fun hidePictogram() {
        binding.linearLayoutAtReadPictogramView.visibility = View.INVISIBLE
    }
}