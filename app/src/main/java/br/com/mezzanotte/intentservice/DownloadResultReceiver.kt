package br.com.mezzanotte.intentservice

import android.os.Bundle
import android.os.ResultReceiver
import android.os.Handler

/**
 * Created by logonrm on 17/02/2018.
 */
class DownloadResultReceiver(handler: Handler): ResultReceiver(handler) {

    lateinit var mReceiver: Receiver

    fun setReceiver(receiver: Receiver) {
        mReceiver = receiver
    }

    interface Receiver {
        fun onReciveResult(resultCode: Int, resultData: Bundle)
    }

    override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
        if (mReceiver != null) {
            mReceiver!!.onReciveResult(resultCode, resultData)
        }
    }
}