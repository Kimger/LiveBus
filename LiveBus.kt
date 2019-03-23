package com.mszs.android.suipaoandroid

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * @author Kimger
 * @email kimgerxue@gmail.com
 * @date 2019/3/23 23:36
 * @description LiveData实现消息总线，用法类似EventBus
 */
class LiveBus {

    private var events = HashMap<Any, ArrayList<BusData<Any>>>()

    companion object {
        var default = SingletonHolder.holder
    }

    private object SingletonHolder {
        var holder = LiveBus()
    }

    fun <T> subscribe(tag: Any, eventKey: Any): BusData<T> {
        checkNotNull(tag)
        checkNotNull(eventKey)
        if (!events.containsKey(eventKey)) {
            var observers = ArrayList<BusData<Any>>()
            var busData = BusData<Any>(tag)
            observers.add(busData)
            events[eventKey] = observers
            return busData as BusData<T>
        }
        var observers = events[eventKey]!!
        for (item in observers) {
            if (item.isThis(tag)) {
                return item as BusData<T>
            }
        }
        var busData = BusData<Any>(tag)
        observers.add(busData)
        return busData as BusData<T>
    }

    fun <T> postValue(eventKey: Any, value: T) {
        checkNotNull(eventKey)
        val arrayList = events[eventKey]
        if (arrayList != null) {
            for (item in arrayList) {
                item.reset()
                item.postValue(value)
            }
        }
    }

    inner class BusData<T> constructor(tag: Any) : MutableLiveData<T>(), onChangedListener {

        private var isFirst = true

        private var tag = tag

        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            super.observe(owner, ObserverWarpper(observer, isFirst, this))
        }

        fun reset() {
            isFirst = true
        }

        fun isThis(tag: Any): Boolean {
            return this.tag === tag
        }

        override fun onChanged() {
            isFirst = false
        }
    }

    inner class ObserverWarpper<T> constructor(
        private var observer: Observer<T>, private var isFirst: Boolean,
        private var listener: onChangedListener
    ) :
        Observer<T> {

        override fun onChanged(t: T) {
            if (isFirst) {
                observer.onChanged(t)
                listener.onChanged()
            }
        }

    }

    interface onChangedListener {
        fun onChanged()
    }

}