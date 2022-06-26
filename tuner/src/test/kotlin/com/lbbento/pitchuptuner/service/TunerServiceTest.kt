package com.lbbento.pitchuptuner.service

import android.media.AudioRecord.RECORDSTATE_RECORDING
import android.media.AudioRecord.RECORDSTATE_STOPPED
import be.tarsos.dsp.pitch.PitchDetectionResult
import be.tarsos.dsp.pitch.Yin
import com.lbbento.pitchupcore.InstrumentType.GUITAR
import com.lbbento.pitchupcore.TuningStatus.TUNED
import com.lbbento.pitchupcore.pitch.PitchHandler
import com.lbbento.pitchupcore.pitch.PitchResult
import com.lbbento.pitchuptuner.audio.PitchAudioRecorder
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Test
import io.reactivex.observers.TestObserver

private class TestableTunerService(pitchAudioRecorder: PitchAudioRecorder, override val torsoYin: Yin, override val pitchHandler: PitchHandler) : TunerService(pitchAudioRecorder, GUITAR)

class TunerServiceTest {
    val mockTorsoYin: Yin = mock()
    val pitchHandler: PitchHandler = mock()
    val mockPitchAudioRecord: PitchAudioRecorder = mock()

    private val tunerService = TestableTunerService(mockPitchAudioRecord, mockTorsoYin, pitchHandler)

    @Test
    fun shouldCallOnCompleteWhenStoppedOrFailedToStartRecording() {
        whenever(mockPitchAudioRecord.recordingState).thenReturn(RECORDSTATE_STOPPED)

        val testObserver = TestObserver<TunerResult>()
        tunerService.getNotes().subscribe(testObserver)
        testObserver.awaitTerminalEvent()

        verify(mockPitchAudioRecord).startRecording()
        verify(mockPitchAudioRecord).recordingState

        testObserver.assertNoErrors().assertComplete()
    }

    @Test
    fun shouldReturnCorrectResultWhenRecordingSuccessfully() {
        val buffer = FloatArray(0)
        val pitchDetectionResult = mock<PitchDetectionResult>()
        val pitchResult = mock<PitchResult>()

        whenever(mockPitchAudioRecord.recordingState).thenReturn(RECORDSTATE_RECORDING)
        whenever(mockPitchAudioRecord.read()).thenReturn(buffer)
        whenever(mockTorsoYin.getPitch(buffer)).thenReturn(pitchDetectionResult)
        whenever(pitchDetectionResult.pitch).thenReturn(123F)
        whenever(pitchHandler.handlePitch(123F)).thenReturn(pitchResult)
        whenever(pitchResult.note).thenReturn("E")
        whenever(pitchResult.tuningStatus).thenReturn(TUNED)
        whenever(pitchResult.diffFrequency).thenReturn(3.3)
        whenever(pitchResult.expectedFrequency).thenReturn(3.0)
        whenever(pitchResult.diffCents).thenReturn(10.0)

        val testObserver = TestObserver<TunerResult>()
        tunerService.getNotes()
                .doOnNext({ whenever(mockPitchAudioRecord.recordingState).thenReturn(RECORDSTATE_STOPPED) })
                .subscribe(testObserver)
        testObserver.awaitTerminalEvent()

        verify(mockPitchAudioRecord).startRecording()
        verify(mockPitchAudioRecord).read()
        verify(mockTorsoYin).getPitch(buffer)
        verify(pitchHandler).handlePitch(123F)
        testObserver.assertValue(TunerResult("E", TUNED, 3.0, 3.3, 10.0))
    }

    @Test
    fun shouldReturnOnErrorWhenIllegalStateExceptionOccurredDuringRecording() {
        whenever(mockPitchAudioRecord.startRecording()).thenThrow(IllegalStateException())

        val testObserver = TestObserver<TunerResult>()
        tunerService.getNotes().subscribe(testObserver)
        testObserver.awaitTerminalEvent()

        testObserver.assertError(IllegalStateException::class.java)

        // assertTerminalEvent()
        // Assert that a single terminal event occurred, either onCompleted() or onError(java.lang.Throwable).
        // After assertError, this is just a less specific assertError?
        // testObserver.assertTerminalEvent()
        verify(mockPitchAudioRecord).startRecording()
    }

    @Test
    fun shouldReturnUnexpectedErrorWhenExceptionOccurredDuringRecording() {
        val buffer = FloatArray(0)
        whenever(mockPitchAudioRecord.read()).thenReturn(buffer)
        whenever(mockTorsoYin.getPitch(buffer)).thenThrow(NumberFormatException())
        whenever(mockPitchAudioRecord.recordingState).thenReturn(RECORDSTATE_RECORDING)

        val testObserver = TestObserver<TunerResult>()
        tunerService.getNotes().subscribe(testObserver)
        testObserver.awaitTerminalEvent()

        testObserver.assertError(RuntimeException::class.java)

        verify(mockPitchAudioRecord).startRecording()
    }

}
