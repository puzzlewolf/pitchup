package com.lbbento.pitchuptuner

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

internal open class AppSchedulers {
    open fun io() = Schedulers.io()

    open fun computation() = Schedulers.computation()

    open fun ui() = AndroidSchedulers.mainThread()!!
}
