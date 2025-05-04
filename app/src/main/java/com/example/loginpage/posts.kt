package com.example.loginpage

import java.io.Serializable


data class posts (
    val Brand:String,
    val type:String,
    val model:String,
    val phone:String,
    val price:Int,
    val city:String,
    val notes:String,
    val picture: String,
    var Auction_day:Int,
    val Auction_hour:String,
    val Auction_minutes:String,
    val carNumber:String,
    val currentPrice:Int
):Serializable