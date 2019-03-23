# LiveBus
LiveData实现Android时间总线
## 用法

### Kotlin
```kotlin
LiveBus.default.subscribe<T>(tag, eventKey).observe(this, Observer { t -> doSomething).show()
```

### Java
```java
LiveBus.Companion.getDefault().<T>subscribe(this,"123").observe(this, new Observer<T>() {
            @Override
            public void onChanged(Object o) {
                //doSomething
            }
        });
```
