package com.example.zodiacedittextsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import otmanethedev.zodiacedittext.ZodiacEditText
import java.util.*

class MainActivity : AppCompatActivity(), ZodiacEditText.DateChangedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        zodiacEditText.setDateChangedListener(this)
    }

    override fun onDateChanged(date: Date) {
        selectedDate.text = "Selected date : $date"
    }
}