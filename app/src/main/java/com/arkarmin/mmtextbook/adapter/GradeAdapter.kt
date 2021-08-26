package com.arkarmin.mmtextbook.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arkarmin.mmtextbook.R
import com.arkarmin.mmtextbook.model.BookData
import com.arkarmin.mmtextbook.model.MyData
import kotlinx.android.synthetic.main.grade_item.view.*

class GradeAdapter(private val gradeRecyclerOnClickListener: GradeRecyclerOnClickListener):RecyclerView.Adapter<GradeAdapter.MyViewHolder>() {
    private var dataList = emptyList<MyData>()



    class MyViewHolder(itemView:View) :RecyclerView.ViewHolder(itemView){

    }
    interface GradeRecyclerOnClickListener {
        fun onClick(myData: MyData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grade_item,parent,false)
        val holder = MyViewHolder(view)
        holder.itemView.cv_grade.setOnClickListener(){
            val position = holder.adapterPosition
            if(!(position == RecyclerView.NO_POSITION)){
                 gradeRecyclerOnClickListener.onClick(dataList[position])
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.itemView.tv_grade_eng.text = dataList[position].Grade_Eng
        holder.itemView.tv_grade_mm.text = dataList[position].Grade_MM

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(data: List<MyData>){
        this.dataList =data
        notifyDataSetChanged()
    }
}