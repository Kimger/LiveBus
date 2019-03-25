# LiveBus

LiveData实现Android时间总线
LiveBus.java使用Java实现
LiveBus.kt使用Kotlin实现

## 调用方法略有差异

# 用法

* ## post(param eventKey,param value)
   - eventKey:事件key
   - value:事件
* ## postSticky(param eventKey,param value)
同上，支持黏性事件（先发送，再订阅）

* ## subscribe(param tag,param eventKey)

   - tag:事件订阅者唯一标识，不可为空并且唯一
   - eventKey:事件key

Kotlin 在方法名后传递消息类型泛型 subscribe<T>()
Java 在方法名前传递消息类型泛型 <T>subscribe()

* ## subscribe(param tag,param eventKey,param class)

<font color=#bc261a size = 4>使用此方法无需传入泛型，返回类型的判断依照传入的class参数来判断</font>

   - tag:事件订阅者唯一标识，不可为空并且唯一
   - eventKey:事件key
   - class:事件消息类型

* ## subscribeSticky
黏性事件订阅，使用方法同上


------

### Kotlin

接收

```kotlin
LiveBus.default.subscribe<T>(tag, eventKey).observe(this, Observer { t -> doSomething).show()
```

发送

```kotlin
LiveBus.default.postValue(eventKey, value)
```

## subcribe泛型为要传递的事件的类型，Tag为必传，并且不同接收者不能相同

------

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

```
implementation 'androidx.appcompat:appcompat:1.1.0-alpha03' 
```

   请使用此版本或者更高版本的appcompat依赖，推荐使用最新版
