package com.lbbento.pitchupapp.common

import com.lbbento.pitchupapp.AppSchedulers
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class StubAppScheduler : AppSchedulers() {

    override fun io(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun ui(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun computation(): Scheduler {
        return Schedulers.trampoline()
    }
}