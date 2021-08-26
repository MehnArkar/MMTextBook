package com.arkarmin.mmtextbook.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class JsonData(
val Data:List<MyData>
):Parcelable


@Parcelize
data class MyData (
val Grade_Eng:String,
val Grade_MM:String,
val Book:List<BookData>
):Parcelable

@Parcelize
data class BookData(
val subjectName:String,
val category:String,
val pdfName:String,
val pdfSize:String,
val link:String
):Parcelable
