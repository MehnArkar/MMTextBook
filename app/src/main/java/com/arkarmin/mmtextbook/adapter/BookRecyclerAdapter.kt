package com.arkarmin.mmtextbook.adapter

import android.content.Context
import android.graphics.Color
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arkarmin.mmtextbook.R
import com.arkarmin.mmtextbook.model.BookData
import kotlinx.android.synthetic.main.book_item.view.*
import java.io.File

class BookRecyclerAdapter(private val bookOnClickListener: BookOnClickListener,private val applicationContext: Context):RecyclerView.Adapter<BookRecyclerAdapter.MyViewHolder>() {

    private var bookList = emptyList<BookData>()

    class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item,parent,false)
        val holder = MyViewHolder(view)
        holder.itemView.btn_download.setOnClickListener(){
            val position = holder.adapterPosition
            if (!(position==RecyclerView.NO_POSITION)){
                bookOnClickListener.onClick(bookList[position])
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pdfFile = File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),bookList[position].pdfName)
        if (!pdfFile.exists()){
            holder.itemView.btn_download.setText("Download")
        }
        holder.itemView.tv_bookName.text = bookList[position].subjectName
        holder.itemView.tv_bookType.text = bookList[position].category
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    fun setBookList(listbook:List<BookData>){
        this.bookList = listbook
        notifyDataSetChanged()
    }

    interface BookOnClickListener{
        fun onClick(book:BookData)
    }
}