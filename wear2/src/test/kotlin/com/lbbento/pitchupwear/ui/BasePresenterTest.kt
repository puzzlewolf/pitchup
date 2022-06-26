package com.lbbento.pitchupwear.ui

import com.lbbento.pitchuptuner.service.TunerResult
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable


class TestBasePresenter(override val disposables: CompositeDisposable) : BasePresenter<BaseView>() {

    fun testSubscriber() {
        val obs: Observable<TunerResult> = Observable.create({})
        obs.subscribeAndManage {}
    }
}

class BasePresenterTest {

    val mockSubscription: CompositeDisposable = mock()
    val presenter = TestBasePresenter(mockSubscription)

    @Test
    fun shouldUnsubscribeOnStop() {
        presenter.onStop()

        verify(mockSubscription).dispose()
    }

    @Test
    fun shouldSubscribeAndManageSubscription() {
        presenter.testSubscriber()

        verify(mockSubscription).add(any())
    }

}