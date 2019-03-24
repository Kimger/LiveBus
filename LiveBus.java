package com.mszs.android.suipaoandroid.activity;

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

    public static LiveBus getDefault() {
        return SingletonHolder.instance;
    }

    static class SingletonHolder {
        static LiveBus instance = new LiveBus();
    }


    /**
     * @param tag      用来区分不同的订阅者，必传并且唯一
     * @param eventKey 事件的key
     */
    public <T> BusData<T> subscribe(Object tag, Object eventKey) {
        checkNotNull(tag, "tag不可为空并且唯一");
        checkNotNull(eventKey, "eventKey不可为空");
        if (!events.containsKey(eventKey)) {
            ArrayList<BusData<Object>> observers = new ArrayList<>();
            BusData<Object> busData = new BusData<>(tag);
            observers.add(busData);
            events.put(eventKey, observers);
            return (BusData<T>) busData;
        }
        List<BusData<Object>> observers = events.get(eventKey);
        for (BusData<Object> observer : observers) {
            if (observer.isThis(tag)) {
                return (BusData<T>) observer;
            }
        }
        BusData<Object> busData = new BusData<>(tag);
        observers.add(busData);
        return (BusData<T>) busData;
    }

    public <T> void postValue(Object eventKey, T value) {
        checkNotNull(eventKey, "eventKey不可为空");
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
            return (this.tag.hashCode() == tag.hashCode()) && (this.tag.getClass().equals(tag.getClass()));
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
