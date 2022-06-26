package com.lbbento.pitchuptuner.common

import com.lbbento.pitchuptuner.AppSchedulers
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

internal class StubAppScheduler : AppSchedulers() {

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