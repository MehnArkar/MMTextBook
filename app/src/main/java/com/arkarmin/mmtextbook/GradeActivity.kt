package com.arkarmin.mmtextbook

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.arkarmin.mmtextbook.adapter.GradeAdapter
import com.arkarmin.mmtextbook.model.BookData
import com.arkarmin.mmtextbook.model.JsonData
import com.arkarmin.mmtextbook.model.MyData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_grade.*
import kotlinx.android.synthetic.main.fragment_grade.view.*


class GradeActivity : Fragment(), GradeAdapter.GradeRecyclerOnClickListener {
lateinit var adapter: GradeAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view=  inflater.inflate(R.layout.fragment_grade, container, false)

        val jsonString= context?.assets?.open("jsonData.json")?.bufferedReader().use { it?.readText() }
        val gson = Gson()
        val type = object : TypeToken<JsonData>() {}.type
        val jsonData:JsonData= gson.fromJson(jsonString, type)
        val dataList:List<MyData> = jsonData.Data

        adapter = GradeAdapter(this)
        view.rv_grade.adapter = adapter
        adapter.setData(dataList)


        return view
    }

    override fun onClick(myData:MyData) {
        val action = GradeActivityDirections.actionGradeToBookListFragment(myData)
        findNavController().navigate(action)
    }


}