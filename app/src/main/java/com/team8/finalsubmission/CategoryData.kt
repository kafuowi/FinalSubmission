package com.team8.finalsubmission


class CategoryData{
    var UID:String =""
    var	name:String =""
    constructor()	//	파이어베이스에서 데이터 변환을 위해서 필요
    constructor(UID:String,name:String)	{
        this.UID = UID
        this.name =	name
    }
}