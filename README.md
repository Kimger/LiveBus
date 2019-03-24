# LiveBus
LiveData实现Android时间总线
## 用法

### Kotlin
接收
```kotlin
LiveBus.default.subscribe<T>(tag, eventKey).observe(this, Observer { t -> doSomething).show()
```
发送
```kotlin
LiveBus.default.postValue(eventKey, value)
```

### Java
接收
```java
LiveBus.Companion.getDefault().<T>subscribe(tag,eventKey).observe(this, new Observer<T>() {
            @Override
            public void onChanged(T t) {
                //doSomething
            }
        });
```
发送
```java
LiveBus.Companion.getDefault().postValue(eventKey,value);
```

### 如果Activity内的observer(param p1,param p2)方法第一个参数this无法指向Activity的LifecycleOwner，检查你app的build.gradle文件，
    implementation 'androidx.appcompat:appcompat:1.1.0-alpha03' 
   请使用此版本或者更高版本的appcompat依赖，推荐使用最新版
