package com.lbbento.pitchuptuner

import com.lbbento.pitchupcore.InstrumentType.GUITAR
import com.lbbento.pitchuptuner.audio.PitchAudioRecorder
import com.lbbento.pitchuptuner.service.TunerService
import io.reactivex.disposables.Disposable


class GuitarTuner(pitchAudioRecord: PitchAudioRecorder, private val guitarTunerListener: GuitarTunerListener) {

    private var appSchedulers: AppSchedulers
    private var tunerService: TunerService
    private var disposable: Disposable? = null

    init {
        tunerService = initializeTunerService(pitchAudioRecord)
        appSchedulers = initializeAppSchedulers()
    }

    internal constructor(pitchAudioRecord: PitchAudioRecorder,
                         guitarTunerListener: GuitarTunerListener,
                         tunerService: TunerService,
                         appSchedulers: AppSchedulers) : this(pitchAudioRecord, guitarTunerListener) {
        this.tunerService = tunerService
        this.appSchedulers = appSchedulers
    }

    fun start() {
        disposable = tunerService.getNotes()
                .subscribeOn(appSchedulers.computation())
                .observeOn(appSchedulers.ui())
                .subscribe(
                        { tunerResult -> guitarTunerListener.onNoteReceived(tunerResult) },
                        { throwable -> guitarTunerListener.onError(throwable) })
    }

    fun stop() {
        disposable!!.dispose()
    }

    private fun initializeTunerService(pitchAudioRecord: PitchAudioRecorder) = TunerService(pitchAudioRecord, GUITAR)

    private fun initializeAppSchedulers(): AppSchedulers = AppSchedulers()
}