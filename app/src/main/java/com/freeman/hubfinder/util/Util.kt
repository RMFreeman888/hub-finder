package com.freeman.hubfinder.util

import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.freeman.hubfinder.R

fun ImageView.loadImage(uri: String?) {
    val options = RequestOptions()
        .error(R.mipmap.ic_launcher_round)
    Glide.with(this.context)
        .setDefaultRequestOptions(options)
        .load(uri)
        .into(this)
}

fun getErrorStringId(e: Throwable?): Int {
    e?.message?.let {
       if(it.contains("403", true)) {
           return R.string.error_exceeded_api_limit
       } else if (it.contains("unable to resolve host", true)) {
           return R.string.error_unable_to_resolve_host
       } else {
           return R.string.loading_error_text
       }
    }
    return R.string.loading_error_text
}