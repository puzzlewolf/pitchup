package com.lbbento.pitchuptuner.service

import android.media.AudioRecord.RECORDSTATE_RECORDING
import be.tarsos.dsp.pitch.Yin
import com.lbbento.pitchupcore.InstrumentType
import com.lbbento.pitchupcore.pitch.PitchHandler
import com.lbbento.pitchuptuner.audio.PitchAudioRecorder
import io.reactivex.Observable


open internal class TunerService(private val pitchAudioRecord: PitchAudioRecorder, private val instrumentType: InstrumentType) {

    open val torsoYin: Yin by lazy { initializeYin(pitchAudioRecord) }
    open val pitchHandler: PitchHandler by lazy { initializePitchHandler(instrumentType) }

    fun getNotes(): Observable<TunerResult> {
        return Observable.create<TunerResult>({
            try {
                pitchAudioRecord.startRecording()

                while (pitchAudioRecord.recordingState == RECORDSTATE_RECORDING && !it.isDisposed) {
                    val buffer = pitchAudioRecord.read()

                    val pitchResult = torsoYin.getPitch(buffer)

                    val result = pitchHandler.handlePitch(pitchResult.pitch)

                    it.onNext(TunerResult(note = result.note,
                            tuningStatus = result.tuningStatus,
                            diffFrequency = result.diffFrequency,
                            expectedFrequency = result.expectedFrequency,
                            diffCents = result.diffCents))
                }

                it.onComplete()

            } catch (e: IllegalStateException) {
                it.onError(IllegalStateException("An error has occurred when trying to record audio. Check your permissions."))
            } catch (e: Exception) {
                it.onError(RuntimeException("Unexpected error"))
            } finally {
                pitchAudioRecord.stopRecording()
            }
        })
    }

    private fun initializeYin(pitchAudioRec: PitchAudioRecorder) = Yin(pitchAudioRec.sampleRateInHz.toFloat(), pitchAudioRec.readSize)

    private fun initializePitchHandler(instrumentType: InstrumentType) = PitchHandler(instrumentType)
}