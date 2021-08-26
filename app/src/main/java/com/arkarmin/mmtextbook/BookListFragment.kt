package com.arkarmin.mmtextbook

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.arkarmin.mmtextbook.adapter.BookRecyclerAdapter
import com.arkarmin.mmtextbook.model.BookData
import com.downloader.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.custom_download_dialog.*
import kotlinx.android.synthetic.main.fragment_book_list.*
import kotlinx.android.synthetic.main.fragment_book_list.view.*
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.*


class BookListFragment : Fragment(), BookRecyclerAdapter.BookOnClickListener {
    lateinit var book: BookData
    private val PERMISSION_STORAGE_CODE: Int = 1000
    lateinit var adapter: BookRecyclerAdapter
  val args :BookListFragmentArgs by navArgs()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_book_list, container, false)

        view.appBarText.text = args.myData.Grade_Eng
        view.btn_back.setOnClickListener {
            findNavController().navigate(BookListFragmentDirections.actionBookListFragmentToGrade())
        }

        adapter = BookRecyclerAdapter(this, requireActivity()!!.applicationContext)
        view.rv_bookList.layoutManager = GridLayoutManager(context, 2)
        view.rv_bookList.adapter = adapter
        adapter.setBookList(args.myData.Book)

        PRDownloader.initialize(context);



        return view
    }

    override fun onClick(book: BookData) {
        this.book = book
        val pdfFile = File(context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), book.pdfName)
        if (pdfFile.exists()){
            Toast.makeText(context, "Please wait a few second", Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, PdfViewerActivity::class.java)
            intent.putExtra("pdfName", book.pdfName)
            startActivity(intent)
        }else{
            //Check internet Connection
            if (isOnline()) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    //request permission if version is greater than or equal Mashmallow
                    if (context?.let { ActivityCompat.checkSelfPermission(it, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) } == PackageManager.PERMISSION_DENIED) {

                        // permission is denied, requset permission
                        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_STORAGE_CODE)

                    } else {
                        //permission is already granted
                        performDownload(book)
                    }

                } else {
                    //version is less than Mashmallow, so don't need to request permission
                    performDownload(book)
                }
            }else{
                val customDialog = context?.let { Dialog(it) }
                customDialog?.setContentView(R.layout.custom_no_internet_dialog)
                customDialog?.window?.setBackgroundDrawable(context?.getDrawable(R.drawable.dialog_background))
                val width = (resources.displayMetrics.widthPixels * 0.70).toInt()
                customDialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
                customDialog?.setCancelable(false)
                customDialog?.show()

                val got_it = customDialog?.findViewById<Button>(R.id.btn_gotit)
                got_it?.setOnClickListener {
                    customDialog?.dismiss()
                }
            }

        }
    }

    fun  performDownload(book: BookData){
        val getUrl = book.link
        val totalMB:Int =book.pdfSize.toInt()
        val customDialog = context?.let { Dialog(it) }
        customDialog?.setContentView(R.layout.custom_download_dialog)
        customDialog?.window?.setBackgroundDrawable(context?.getDrawable(R.drawable.dialog_background))
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        customDialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        customDialog?.setCancelable(false)
        customDialog?.show()

            val downloadId = PRDownloader.download(getUrl,
                    context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(), book.pdfName)
            .build()
            .setOnStartOrResumeListener { object :OnStartOrResumeListener{
                    override fun onStartOrResume() {

                    }
                } }
            .setOnPauseListener { object :OnPauseListener{
                    override fun onPause() {

                    }
                } }
            .setOnCancelListener { object :OnCancelListener{
                    override fun onCancel() {

                    }
                } }
            .setOnProgressListener { progress->

                val MEGABYTE:Long = 1024 * 1024
                val CURRENT_BYTE :Long = progress.currentBytes
                val currentMB = CURRENT_BYTE/MEGABYTE.toInt()

                val progressBar = customDialog?.findViewById<ProgressBar>(R.id.progress_bar)
                var percentg= currentMB*100/totalMB
                progressBar?.setProgress(percentg.toInt())
                val tvPercent = customDialog?.findViewById<TextView>(R.id.tv_percent)
                tvPercent?.text = percentg.toInt().toString()+"%"
                val total = customDialog?.findViewById<TextView>(R.id.tv_total)
                total?.text = totalMB.toString()+"MB"
                val current = customDialog?.findViewById<TextView>(R.id.tv_current)
                current?.text = (CURRENT_BYTE/MEGABYTE).toInt().toString()+"MB"
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    customDialog?.dismiss()
                    Toast.makeText(context, "Please wait a few second", Toast.LENGTH_SHORT).show()
                    view!!.rv_bookList.adapter = adapter
                    adapter.setBookList(args.myData.Book)
                    val intent = Intent(activity, PdfViewerActivity::class.java)
                    intent.putExtra("pdfName", book.pdfName)
                    startActivity(intent)
                }

                override fun onError(error: Error?) {
                    customDialog?.dismiss()
                    val errorDialog = context?.let { Dialog(it) }
                    errorDialog?.setContentView(R.layout.download_fail_dialog)
                    errorDialog?.window?.setBackgroundDrawable(context?.getDrawable(R.drawable.dialog_error))
                    val width = (resources.displayMetrics.widthPixels * 0.70).toInt()
                    errorDialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
                    errorDialog?.setCancelable(false)
                    errorDialog?.show()

                    val got_it = errorDialog?.findViewById<Button>(R.id.btn_gotit)
                    got_it?.setOnClickListener {
                        errorDialog?.dismiss()
                    }

                }
            })

        val cancel = customDialog?.findViewById<TextView>(R.id.btn_cancel)
        cancel?.setOnClickListener(View.OnClickListener {
            PRDownloader.cancel(downloadId)
            customDialog.dismiss()
        })




        //val title = URLUtil.guessFileName(getUrl,null,null)
//        val request = DownloadManager.Request(Uri.parse(getUrl))
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
//        request.setTitle(book.subjectName)
//        request.setDescription("Downloading please wait")
//        request.allowScanningByMediaScanner()
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//        request.setDestinationInExternalFilesDir(context,Environment.DIRECTORY_DOWNLOADS,book.pdfName)
//        val downloadManager:DownloadManager = activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        downloadManager.enqueue(request)



    }






    fun isOnline(): Boolean {
        val connMgr = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){
            PERMISSION_STORAGE_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    performDownload(this.book)
                } else {
                    Toast.makeText(context, "Permission is denised", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




  }
