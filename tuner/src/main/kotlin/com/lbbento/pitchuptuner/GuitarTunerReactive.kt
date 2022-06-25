package com.lbbento.pitchuptuner

import com.lbbento.pitchupcore.InstrumentType.GUITAR
import com.lbbento.pitchuptuner.audio.PitchAudioRecorder
import com.lbbento.pitchuptuner.service.TunerResult
import com.lbbento.pitchuptuner.service.TunerService
import io.reactivex.Observable


class GuitarTunerReactive(pitchAudioRecord: PitchAudioRecorder) {

    private var tunerService: TunerService

    init {
        tunerService = initializeTunerService(pitchAudioRecord)
    }

    internal constructor(pitchAudioRecord: PitchAudioRecorder, tunerService: TunerService) : this(pitchAudioRecord) {
        this.tunerService = tunerService
    }

    fun listenToNotes(): Observable<TunerResult> = tunerService.getNotes()
            .distinctUntilChanged { l, r -> l.note == r.note && l.tuningStatus.name == r.tuningStatus.name }

    private fun initializeTunerService(pitchAudioRecord: PitchAudioRecorder) = TunerService(pitchAudioRecord, GUITAR)
}