package com.example.livebus.byjava;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kimger
 * @email kimgerxue@gmail.com
 * @date 2019/3/24 16:56
 * @description LiveData实现消息总线，用法类似EventBus
 * @Power by Java
 */
public class LiveBus {
    private Map<Object, List<BusData<Object>>> events = new HashMap<>();
    private Map<Object, List<Object>> cacheMsg = new HashMap<>();
    private static String tag;

    public static LiveBus getDefault() {
        tag = new Exception().getStackTrace()[1].getClassName() + new Exception().getStackTrace()[1].getMethodName();
        return SingletonHolder.instance;
    }

    static class SingletonHolder {
        static LiveBus instance = new LiveBus();
    }

    /**
     * 普通事件订阅
     */
    @SuppressWarnings("unchecked")
    public <T> BusData<T> subscribe(Class<T> tClass) {
        return (BusData<T>) subscribe(tag, tClass.getCanonicalName(), tClass);
    }

    /**
     * 普通事件订阅
     */
    @SuppressWarnings("unchecked")
    public <T> BusData<T> subscribe(Object eventKey) {
        return (BusData<T>) subscribe(tag, eventKey, Object.class);
    }

    /**
     * 普通事件订阅
     */
    @SuppressWarnings("unchecked")
    public <T> BusData<T> subscribe(Object eventKey, Class<T> tClass) {
        return (BusData<T>) subscribe(tag, eventKey, tClass, false);
    }

    /**
     * 普通事件订阅
     */
    @SuppressWarnings("unchecked")
    public <T> BusData<T> subscribe(Object tag, Object eventKey) {
        return (BusData<T>) subscribe(tag, eventKey, Object.class);
    }

    /**
     * 普通事件订阅
     */
    @SuppressWarnings("unchecked")
    public <T> BusData<T> subscribe(Object tag, Object eventKey, Class<T> tClass) {
        return (BusData<T>) subscribe(tag, eventKey, tClass, false);
    }

    /**
     * 黏性事件订阅
     */
    @SuppressWarnings("unchecked")
    public <T> BusData<T> subscribeSticky(Class<T> tClass) {
        return (BusData<T>) subscribeSticky(tag, tClass.getCanonicalName(), tClass);
    }

    /**
     * 黏性事件订阅
     */
    @SuppressWarnings("unchecked")
    public <T> BusData<T> subscribeSticky(Object eventKey) {
        return (BusData<T>) subscribeSticky(tag, eventKey, Object.class);
    }

    /**
     * 黏性事件订阅
     */
    @SuppressWarnings("unchecked")
    public <T> BusData<T> subscribeSticky(Object tag, Object eventKey) {
        return (BusData<T>) subscribeSticky(tag, eventKey, Object.class);
    }

    /**
     * 黏性事件订阅
     */
    @SuppressWarnings("unchecked")
    public <T> BusData<T> subscribeSticky(Object eventKey, Class<T> tClass) {
        return (BusData<T>) subscribe(tag, eventKey, tClass, true);
    }

    /**
     * 黏性事件订阅
     */
    @SuppressWarnings("unchecked")
    public <T> BusData<T> subscribeSticky(Object tag, Object eventKey, Class<T> tClass) {
        return (BusData<T>) subscribe(tag, eventKey, tClass, true);
    }

    /**
     * 普通事件(只能发送实体类)
     *
     * @param value 要发送的消息
     */
    public void post(Object value) {
        post(value.getClass().getCanonicalName(), value);
    }

    /**
     * 黏性事件(只能发送实体类)
     *
     * @param value 要发送的消息
     */
    public void postSticky(Object value) {
        postSticky(value.getClass().getCanonicalName(), value);
    }

    /**
     * 普通事件
     *
     * @param eventKey 发送事件key
     * @param value    要发送的消息
     */
    public void post(Object eventKey, Object value) {
        checkNotNull(eventKey, "eventKey不可为空");
        List<BusData<Object>> observers = events.get(eventKey);
        if (observers != null) {
            for (BusData<Object> observer : observers) {
                observer.reset();
                observer.postValue(value);
            }
        }
    }

    /**
     * 黏性事件
     *
     * @param eventKey 发送事件key
     * @param value    要发送的消息
     */
    public void postSticky(Object eventKey, Object value) {
        checkNotNull(eventKey, "eventKey不可为空");
        List<BusData<Object>> observers = events.get(eventKey);
        if (!cacheMsg.containsKey(eventKey)) {
            ArrayList<Object> msgs = new ArrayList<>();
            msgs.add(value);
            cacheMsg.put(eventKey, msgs);
        } else {
            List<Object> msgs = cacheMsg.get(eventKey);
            msgs.add(value);
        }
        if (observers != null) {
            for (BusData<Object> observer : observers) {
                observer.reset();
                observer.postValue(value);
            }
        }
    }


    /**
     * 核心代码
     *
     * @param tag      订阅者tag
     * @param eventKey 事件key
     * @param tClass   传递的消息类型
     * @param isSticky 是否是黏性事件
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> BusData<T> subscribe(Object tag, Object eventKey, Class<T> tClass, boolean isSticky) {
        checkNotNull(tag, "tag不可为空并且唯一");
        checkNotNull(eventKey, "eventKey不可为空");
        if (!events.containsKey(eventKey)) {
            ArrayList<BusData<Object>> observers = new ArrayList<>();
            BusData<Object> busData = new BusData<>(tag);
            observers.add(busData);
            events.put(eventKey, observers);
            if (isSticky) checkCacheMsg(eventKey);
            return (BusData<T>) busData;
        }
        List<BusData<Object>> observers = events.get(eventKey);
        assert observers != null;
        for (BusData<Object> observer : observers) {
            if (observer.isThis(tag)) {
                if (isSticky) checkCacheMsg(eventKey);
                return (BusData<T>) observer;
            }
        }
        BusData<Object> busData = new BusData<>(tag);
        observers.add(busData);
        if (isSticky) checkCacheMsg(eventKey);
        return (BusData<T>) busData;
    }

    private void checkCacheMsg(Object eventKey) {
        if (cacheMsg.containsKey(eventKey)) {
            for (Object o : cacheMsg.get(eventKey)) {
                notify(eventKey, o);
            }
            cacheMsg.remove(eventKey);
        }
    }

    private void notify(Object eventKey, Object value) {
        List<BusData<Object>> observers = events.get(eventKey);
        if (observers != null) {
            for (BusData<Object> observer : observers) {
                observer.reset();
                observer.postValue(value);
            }
        }
    }


    public class BusData<T> extends MutableLiveData<T> implements OnChangedListener {
        private boolean isFitst = true;
        private Object tag;

        BusData(Object tag) {
            this.tag = tag;
        }

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
            super.observe(owner, new ObserverWarpper<>(isFitst, observer, this));
        }

        boolean isThis(Object tag) {
            if (tag instanceof String) {
                if (this.tag.equals(tag)) {
                    return true;
                }
                if (this.tag.getClass().getCanonicalName().equals(tag.getClass().getCanonicalName())) {
                    this.tag = tag;
                    return true;
                }
            }
            return false;
        }

        void reset() {
            isFitst = true;
        }

        @Override
        public void onChanged() {
            isFitst = false;
        }
    }

    private class ObserverWarpper<T> implements Observer<T> {

        private boolean isFirst;
        private Observer observer;
        private OnChangedListener listener;

        ObserverWarpper(boolean isFirst, Observer observer, OnChangedListener listener) {
            this.isFirst = isFirst;
            this.observer = observer;
            this.listener = listener;
        }

        @Override
        public void onChanged(T t) {
            if (isFirst) {
                observer.onChanged(t);
                listener.onChanged();
            }
        }
    }

    interface OnChangedListener {
        void onChanged();
    }

    private <T> T checkNotNull(T reference, String msg) {
        if (reference == null) {
            throw new NullPointerException(msg);
        }
        return reference;
    }


}
