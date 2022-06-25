package com.lbbento.pitchupapp.ui

import android.support.annotation.CallSuper
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

abstract class BasePresenter<V : BaseView> : Presenter {

    lateinit var mView: V

    open val disposables: CompositeDisposable by lazy { CompositeDisposable() }

    @CallSuper
    override fun onAttachedToWindow(view: BaseView) {
        @Suppress("UNCHECKED_CAST")
        this.mView = view as V
    }

    @CallSuper
    override fun onViewResumed() {
    }

    @CallSuper
    override fun onViewResuming() {
    }

    @CallSuper
    override fun onCreated() {
    }

    @CallSuper
    override fun onStop() {
    }

    @CallSuper
    override fun onDestroy() {
        disposables.dispose()
    }

    fun <T> Observable<T>.subscribeAndManage(onNext: (T) -> Unit = {},
                                             onError: () -> Unit = {},
                                             onComplete: () -> Unit = {}) {
        disposables.add(subscribe(
                { result -> onNext(result) },
                { onError() },
                { onComplete() }))
    }
}