package com.mszs.android.suipaoandroid

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.*

/**
 * @author Kimger
 * @email kimgerxue@gmail.com
 * @date 2019/3/23 23:36
 * @description LiveData实现消息总线，用法类似EventBus
 * @Power by kotlin
 */
class LiveBus {

    private var events = HashMap<Any, ArrayList<BusData<Any>>>()

    companion object {
        var default = SingletonHolder.holder
    }

    private object SingletonHolder {
        var holder = LiveBus()
    }

    /**
     * @param tag 用来区分不同的订阅者，必传并且唯一
     * @param eventKey 事件的key
     */
    fun <T> subscribe(tag: Any, eventKey: Any): BusData<T> {
        return subscribe(tag, eventKey, Any::class.java) as BusData<T>
    }

    /**
     *
     * @param tag
     * @param eventKey
     * @param tClass 传递的消息类型
     * @param <T>
     * @return
     */
    fun <T> subscribe(tag: Any, eventKey: Any, clazz: Class<T>): BusData<T> {
        checkNotNull(tag, "tag不可为空并且唯一")
        checkNotNull(eventKey, "eventKey不可为空")
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
        checkNotNull(eventKey, "eventKey不可为空")
        val arrayList = events[eventKey]
        if (arrayList != null) {
            for (item in arrayList) {
                item.reset()
                item.postValue(value)
            }
        }
    }

    class BusData<T> constructor(private var tag: Any) : MutableLiveData<T>(), OnChangedListener {

        private var isFirst = true

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

    class ObserverWarpper<T> constructor(
        private var observer: Observer<T>, private var isFirst: Boolean,
        private var listener: OnChangedListener
    ) :
        Observer<T> {

        override fun onChanged(t: T) {
            if (isFirst) {
                observer.onChanged(t)
                listener.onChanged()
            }
        }

    }

    interface OnChangedListener {
        fun onChanged()
    }

    private fun <T> checkNotNull(reference: T, msg: String): T {
        if (reference == null) {
            throw NullPointerException(msg)
        }
        return reference
    }


}