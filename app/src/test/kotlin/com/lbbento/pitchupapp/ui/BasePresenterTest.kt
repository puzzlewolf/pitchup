package com.lbbento.pitchupapp.ui

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import io.reactivex.disposables.CompositeDisposable

class TestBasePresenter(override val disposables: CompositeDisposable) : BasePresenter<BaseView>()

class BasePresenterTest {

    val mockSubscription: CompositeDisposable = mock()
    val presenter = TestBasePresenter(mockSubscription)

    @Test
    fun shouldUnsubscribeOnDestroy() {
        presenter.onDestroy()

        verify(mockSubscription).dispose()
    }
}