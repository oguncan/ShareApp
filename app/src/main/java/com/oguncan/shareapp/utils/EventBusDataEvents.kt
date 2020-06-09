package com.oguncan.shareapp.utils

import android.net.Uri

class EventBusDataEvents {
//    internal class SendTelephoneOrMail(var telNo : String?, var email : String?, var verificationID : String?, var code : String?, var isMail : Boolean?)
//    internal class SendAccountInfo(var account : Account)
    internal class SendShareImage(var image : Uri)
}