package com.oguncan.shareapp.Activity

class Constant {
    companion object{
    var URL : String = "http://192.168.43.130/"
    var HOME : String = URL +"api"
    var LOGIN : String = HOME +"/login"
    val LOGOUT = HOME +"/logout"
    var SAVE_USER_INFO : String = HOME +"/save_user_info"
    var REGISTER : String = HOME +"/register"
    var POSTS : String = HOME +"/posts"
    var CREATE_POSTS : String = HOME +"/posts/create"
    var DELETE_POSTS : String = HOME +"/posts/delete"
    var UPDATE_POSTS : String = HOME +"/posts/update"
    var MY_POST : String = HOME +"/posts/myPost"
    var CREATE_SCORE : String = HOME +"/scores/create"

    }
}