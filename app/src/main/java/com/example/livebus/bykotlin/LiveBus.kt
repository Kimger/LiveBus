import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * @author Kimger
 * @email kimgerxue@gmail.com
 * @date 2019/3/23 23:36
 * @description LiveData实现消息总线，用法类似EventBus
 * @Power by kotlin
 */
class LiveBus {

    private var events = HashMap<Any, ArrayList<BusData<Any>>>()
    private var cacheMsg = HashMap<Any, ArrayList<Any>>()


    companion object {
        var default = SingletonHolder.holder
        private val tag = Exception().stackTrace[1].className + Exception().stackTrace[1].methodName
    }

    private object SingletonHolder {
        var holder = LiveBus()
    }

    /**
     * 普通事件订阅
     */
    fun <T> subscribe(clazz: Class<T>): BusData<T> {
        return subscribe(tag, clazz.canonicalName!!, clazz) as BusData<T>
    }

    /**
     * 普通事件订阅
     */
    fun <T> subscribe(eventKey: Any): BusData<T> {
        return subscribe(tag, eventKey, Any::class.java) as BusData<T>
    }

    /**
     * 普通事件订阅
     */
    fun <T> subscribe(eventKey: Any, clazz: Class<T>): BusData<T> {
        return subscribe(tag, eventKey, clazz, false) as BusData<T>
    }

    /**
     * 普通事件订阅
     */
    fun <T> subscribe(tag: Any, eventKey: Any): BusData<T> {
        return subscribe(tag, eventKey, Any::class.java) as BusData<T>
    }

    /**
     * 普通事件订阅
     */
    fun <T> subscribe(tag: Any, eventKey: Any, clazz: Class<T>): BusData<T> {
        return subscribe(tag, eventKey, clazz, false)
    }

    /**
     * 黏性事件订阅
     */
    fun <T> subscribeSticky(clazz: Class<T>): BusData<T> {
        return subscribeSticky(tag, clazz.canonicalName!!, clazz) as BusData<T>
    }

    /**
     * 黏性事件订阅
     */
    fun <T> subscribeSticky(eventKey: Any): BusData<T> {
        return subscribeSticky(tag, eventKey, Any::class.java) as BusData<T>
    }

    /**
     * 黏性事件订阅
     */
    fun <T> subscribeSticky(eventKey: Any, clazz: Class<T>): BusData<T> {
        return subscribe(tag, eventKey, clazz, true) as BusData<T>
    }

    /**
     * 黏性事件订阅
     */
    fun <T> subscribeSticky(tag: Any, eventKey: Any): BusData<T> {
        return subscribeSticky(tag, eventKey, Any::class.java) as BusData<T>
    }

    /**
     * 黏性事件订阅
     */
    fun <T> subscribeSticky(tag: Any, eventKey: Any, clazz: Class<T>): BusData<T> {
        return subscribe(tag, eventKey, clazz, true) as BusData<T>
    }

    /**
     * 普通事件(只能发送实体类)
     * @param value 要发送的消息
     */
    fun post(value: Any) {
        post(value::class.java.canonicalName!!, value)
    }

    /**
     * 黏性事件(只能发送实体类)
     * @param value 要发送的消息
     */
    fun postSticky(value: Any) {
        postSticky(value::class.java.canonicalName!!, value)
    }

    /**
     * 普通事件
     * @param eventKey 发送事件key
     * @param value 要发送的消息
     */
    fun post(eventKey: Any, value: Any) {
        checkNotNull(eventKey, "eventKey不可为空")
        val observers = events[eventKey]
        if (observers != null) {
            for (item in observers) {
                item.postValue(value)
            }
        }
    }

    /**
     * 黏性事件
     * @param eventKey 发送事件key
     * @param value 要发送的消息
     */
    fun postSticky(eventKey: Any, value: Any) {
        checkNotNull(eventKey, "eventKey不可为空")
        val arrayList = events[eventKey]
        if (!cacheMsg.containsKey(eventKey)) {
            var msgs = ArrayList<Any>()
            msgs.add(value)
            cacheMsg[eventKey] = msgs
        } else {
            val msgs = cacheMsg[eventKey]!!
            msgs.add(value)
        }
        if (arrayList != null) {
            for (item in arrayList) {
                item.postValue(value)
            }
        }
    }

    /**
     * 核心代码
     * @param tag 订阅者tag
     * @param eventKey 事件key
     * @param tClass 传递的消息类型
     * @param isSticky 是否是黏性事件
     * @return
     */
    private fun <T> subscribe(tag: Any, eventKey: Any, clazz: Class<T>, isSticky: Boolean): BusData<T> {
        checkNotNull(tag, "tag不可为空并且唯一")
        checkNotNull(eventKey, "eventKey不可为空")
        if (!events.containsKey(eventKey)) {
            var observers = ArrayList<BusData<Any>>()
            var busData = BusData<Any>(tag)
            observers.add(busData)
            events[eventKey] = observers
            if (isSticky) checkCacheMsg(eventKey)
            return busData as BusData<T>
        }

        var observers = events[eventKey]!!
        for (item in observers) {
            if (item.isThis(tag)) {
                if (isSticky) checkCacheMsg(eventKey)
                return item as BusData<T>
            }
        }
        var busData = BusData<Any>(tag)
        observers.add(busData)
        if (isSticky) checkCacheMsg(eventKey)
        return busData as BusData<T>
    }


    private fun checkCacheMsg(eventKey: Any) {
        if (cacheMsg.containsKey(eventKey)) {
            for (msg in cacheMsg[eventKey]!!) {
                notify(eventKey, msg)
            }
            cacheMsg.remove(eventKey)
        }
    }

    private fun notify(eventKey: Any, value: Any) {
        val observers = events[eventKey]
        if (observers != null) {
            for (item in observers) {
                item.postValue(value)
            }
        }
    }

    class BusData<T> constructor(private var tag: Any) : MutableLiveData<T>(), OnChangedListener {

        private var needChange = true

        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            super.observe(owner, ObserverWarpper(observer, needChange, this))
        }

        override fun setValue(value: T) {
            reset()
            super.setValue(value)
        }

        override fun postValue(value: T) {
            reset()
            super.postValue(value)
        }

        private fun reset() {
            needChange = true
        }

        fun isThis(tag: Any): Boolean {
            if (this.tag === tag) {
                this.tag = tag
                return true
            }
            if (this.tag is LifecycleOwner && tag is LifecycleOwner) {
                if (this.tag.javaClass.canonicalName === tag.javaClass.canonicalName) {
                    this.tag = tag
                    return true
                }
            }
            return false
        }

        override fun onChanged() {
            needChange = false
        }

    }

    class ObserverWarpper<T> constructor(
        private var observer: Observer<T>, private var needChange: Boolean,
        private var listener: OnChangedListener
    ) :
        Observer<T> {

        override fun onChanged(t: T) {
            if (needChange) {
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
