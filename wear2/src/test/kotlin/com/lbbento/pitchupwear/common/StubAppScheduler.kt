package com.lbbento.pitchupwear.common

import com.lbbento.pitchupwear.AppSchedulers
import io.reactivex.schedulers.Schedulers

class StubAppScheduler : AppSchedulers() {
    override fun io() = Schedulers.trampoline()!!

    override fun ui() = Schedulers.trampoline()!!
}