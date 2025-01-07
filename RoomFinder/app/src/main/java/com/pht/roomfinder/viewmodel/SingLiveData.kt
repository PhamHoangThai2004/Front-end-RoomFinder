package com.pht.roomfinder.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class SingLiveData<T> : MutableLiveData<T>() {
    private val pending = AtomicBoolean(false)

    override fun setValue(value: T?) {
        if (pending.compareAndSet(false, true)) {
            super.setValue(value)
        }
    }

    fun call() {
        value = null
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        // Only notify if the event is not already handled.
        super.observe(owner, Observer { t ->
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }
}