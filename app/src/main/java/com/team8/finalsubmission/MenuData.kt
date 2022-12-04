package com.team8.finalsubmission


class MenuData{
    var imageURL: String = ""
    var	name:String =	""
    var	price:Int =0
    var quantity:Int =0
    var serving: Int=0
    constructor()	//	파이어베이스에서 데이터 변환을 위해서 필요
    constructor(name:String,	password:String,price:Int,quantity:Int,serving:Int)	{
        this.imageURL =	password
        this.name =	name
        this.price = price
        this.quantity = quantity
        this.serving = serving
    }
}