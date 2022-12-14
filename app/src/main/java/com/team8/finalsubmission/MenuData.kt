package com.team8.finalsubmission

import android.os.Parcel
import android.os.Parcelable


class MenuData() :Parcelable	{
    var UID:String =""
    var imageURL: String = ""
    var	name:String =	""
    var	price:Int =0
    var quantity:Int =0
    var serving: Int=0
    var select_count: Int=0
    var category: String =""

    constructor(parcel: Parcel) : this() {
        UID = parcel.readString().toString()
        imageURL = parcel.readString().toString()
        name = parcel.readString().toString()
        price = parcel.readInt()
        quantity = parcel.readInt()
        serving = parcel.readInt()
        select_count = parcel.readInt()
        category =parcel.readString().toString()

    }

    constructor(parcel: MenuData) :this(){
        UID = parcel.UID
        imageURL = parcel.imageURL
        name = parcel.name
        price = parcel.price
        quantity = parcel.quantity
        serving = parcel.serving
        select_count = parcel.select_count
        category =parcel.category
    }

    //constructor()	//	파이어베이스에서 데이터 변환을 위해서 필요
    /*constructor(UID:String,name:String,	password:String,price:Int,quantity:Int,serving:Int)	{
        this.UID = UID
        this.imageURL =	password
        this.name =	name
        this.price = price
        this.quantity = quantity
        this.serving = serving
    }*/
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(UID)
        parcel.writeString(imageURL)
        parcel.writeString(name)
        parcel.writeInt(price)
        parcel.writeInt(quantity)
        parcel.writeInt(serving)
        parcel.writeInt(select_count)
        parcel.writeString(category)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MenuData> {
        override fun createFromParcel(parcel: Parcel): MenuData {
            return MenuData(parcel)
        }

        override fun newArray(size: Int): Array<MenuData?> {
            return arrayOfNulls(size)
        }
    }
}