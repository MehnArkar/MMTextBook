package com.arkarmin.mmtextbook

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import kotlinx.android.synthetic.main.activity_pdf_viewer.*
import java.io.File

class PdfViewerActivity : AppCompatActivity() {
    lateinit var pdfName:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)


        pdfName = intent.getStringExtra("pdfName").toString()
        val pdfFile = File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),pdfName)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val pageNumber = sharedPref.getInt(pdfName, 0)

        pdfView.fromFile(pdfFile)
            .defaultPage(pageNumber)
            .scrollHandle(DefaultScrollHandle(this))
            .enableSwipe(true)
            .swipeHorizontal(true)
            .autoSpacing(true)
            .enableAntialiasing(true)
            .pageFling(true)
            .load()

        pdfView.currentPage

    }

    override fun onPause() {
        super.onPause()

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putInt(pdfName, pdfView.currentPage)
            apply()
        }
    }

}