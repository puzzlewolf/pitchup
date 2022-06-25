package com.lbbento.pitchupapp

import com.lbbento.pitchupapp.di.ForApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ForApplication
open class AppSchedulers @Inject constructor() {
    open fun io() = Schedulers.io()!!

    open fun computation() = Schedulers.computation()!!

    open fun ui() = AndroidSchedulers.mainThread()!!
}