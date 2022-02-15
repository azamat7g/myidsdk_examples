![logo](../img/logo.png)

[o'zbekcha](README.md)

# myID SDK
MyID - самый простой, удобный и надежный способ удаленной идентификации в Узбекистане. 

## Требования 
Перед подключением myID SDK к вашему проекту, вам необходимо получить от нас специальные токены `clinet_id` и `clinet_secrect` и zip-архив SDK. 

## Прежде чем вы начнете 
Полнофункциональную версию приведенных ниже кодов можно найти в этом [репозитории](https://github.com/azamat7g/myidsdk_examples)

## Подключите SDK
Откройте файл `build.gradle` на уровне модуля. Добавьте специальный репозиторий в раздел Android и установите `minSdkVersion` на 21: 

```gradle
android {
    ....

    defaultConfig {
        minSdkVersion 21
        ...
    }

    repositories {
        maven {
            url 'https://storage.uzinfocom.uz/libs/android/myid'
        }
        maven {
            url "https://storage.googleapis.com/download.flutter.io"
        }
    }
}
```
! Если в `settings.gradle` исползуется `dependencyResolutionManagement` тогда вместо `build.gradle` все `repositories` добавим в `dependencyResolutionManagement`

В разделе `dependencies` подключаем SDK:

```gradle
dependencies {
    ...

    implementation 'uz.uzinfocom.myidsdk:flutter_release:$VERSION'
}
```

и раскапуйте SDK из zip-архива в папку `app/libs`. Расположение файлов должно выглядеть следующим образом: 

```
├── libs
│   └── myid
│       └── uz
│           └── uzinfocom
│               └── myidsdk
│                   ├── flutter_release
│                   │   ├── 1.0
│                   │   │   ├── flutter_release-1.0.aar
│                   │   │   ├── flutter_release-1.0.aar.md5
│                   │   │   ├── flutter_release-1.0.aar.sha1
│                   │   │   ├── flutter_release-1.0.pom
│                   │   │   ├── flutter_release-1.0.pom.md5
│                   │   │   └── flutter_release-1.0.pom.sha1
│                   │   ├── maven-metadata.xml
│                   │   ├── maven-metadata.xml.md5
│                   │   └── maven-metadata.xml.sha1
│                   └── modules
│                       ├── ...
│                       ......
```

## Добавить SDK в проект 
SDKni loyihaga qo'shish uchun maxsus Activity yaratish kerak. Buning uchun "Android studio" dasturida `File > New > Activity > Empty activity` tanlab yangi activity qo'shamiz. Activityga nom beramiz. Misol uchun `FaceIdActivity`. Activityni muvofaqiyatlik qo'shganimizdan keyin uni `FragmentActivity`dan extend olamiz.

Чтобы добавить SDK в проект, необходимо создать специальное действие. Для этого в программе Android studio выберите `File > New > Activity > Empty activity` и добавьте новое действие. Мы называем деятельность. Например, `FragmentActivity`. После того, как мы успешно добавили действие, мы можем расширить его из `FragmentActivity`. 


Kotlin
```kotlin
class FaceIdActivity : FragmentActivity() {
```

Java
```java
public class FaceIdActivity extends FragmentActivity {
```

После этого делаем несколько переменных 

Kotlin
```kotlin
...

companion object {
    private const val FLUTTER_FRAGMENT = "flutter_fragment"
    private const val FLUTTER_ENGINE_ID = "flutter_engine"
    private const val FLUTTER_CHANNEL = "channel/myid"
    private var flutterFragment: FlutterFragment? = null
}

...
```

Java
```java
...

final String FLUTTER_FRAGMENT = "flutter_fragment";
final String FLUTTER_ENGINE_ID = "flutter_engine";
final String FLUTTER_CHANNEL = "channel/myid";
FlutterFragment flutterFragment;

...
```

Мы добавим следующий код, чтобы все события фрагмента попадали в SDK:

Kotlin
```kotlin
...

override fun onPostResume() {
    super.onPostResume()
    flutterFragment!!.onPostResume()
}

override fun onNewIntent(@NonNull intent: Intent) {
    super.onNewIntent(intent)
    flutterFragment!!.onNewIntent(intent)
}

override fun onBackPressed() {
    flutterFragment!!.onBackPressed()
}

override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String?>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    flutterFragment!!.onRequestPermissionsResult(
        requestCode,
        permissions,
        grantResults
    )
}

override fun onUserLeaveHint() {
    flutterFragment!!.onUserLeaveHint()
}

override fun onTrimMemory(level: Int) {
    super.onTrimMemory(level)
    flutterFragment!!.onTrimMemory(level)
}
```

Java
```java
...

@Override
public void onPostResume() {
    super.onPostResume();
    flutterFragment.onPostResume();
}

@Override
protected void onNewIntent(@NonNull Intent intent) {
    super.onNewIntent(intent);
    flutterFragment.onNewIntent(intent);
}

@Override
public void onBackPressed() {
    flutterFragment.onBackPressed();
}

@Override
public void onRequestPermissionsResult(
        int requestCode,
        @NonNull String[] permissions,
        @NonNull int[] grantResults
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    flutterFragment.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
    );
}

@Override
public void onUserLeaveHint() {
    flutterFragment.onUserLeaveHint();
}

@Override
public void onTrimMemory(int level) {
    super.onTrimMemory(level);
    flutterFragment.onTrimMemory(level);
}
```

Мы используем следующий код для запуска SDK и получения от него ответа:


Kotlin 

```kotlin

private fun attachFlutterFragment() {
    flutterFragment =
        supportFragmentManager.findFragmentByTag(FLUTTER_FRAGMENT) as FlutterFragment?
    if (null == flutterFragment) {
        flutterFragment =
            FlutterFragment
                .withCachedEngine(FLUTTER_ENGINE_ID)
                .shouldAttachEngineToActivity(true)
                .build() as FlutterFragment
    }

    supportFragmentManager
        .beginTransaction()
        .add(
            R.id.fragmentContainerView,
            flutterFragment!!,
            FLUTTER_FRAGMENT
        )
        .commit()
}

private fun setMethodChannels(flutterEngine: FlutterEngine) {
    MethodChannel(
        flutterEngine.dartExecutor.binaryMessenger,
        FLUTTER_CHANNEL
    ).setMethodCallHandler { call, result ->
        when (call.method) {
            "resultJson" -> {
                val resultIntent = Intent()
                resultIntent.putExtra("code", call.arguments.toString())
                setResult(RESULT_OK, resultIntent)

                result.success(true)
                finish()
            }
        }
    }
}

```

Java

```java

private void attachFlutterFragment() {
    flutterFragment =
            (FlutterFragment) getSupportFragmentManager().findFragmentByTag(FLUTTER_FRAGMENT);
    if (null == flutterFragment) {
        flutterFragment =
                (FlutterFragment) FlutterFragment
                        .withCachedEngine(FLUTTER_ENGINE_ID)
                        .shouldAttachEngineToActivity(true)
                        .build();
    }

    getSupportFragmentManager()
            .beginTransaction()
            .add(
                    R.id.fragmentContainerView,
                    flutterFragment,
                    FLUTTER_FRAGMENT
            )
            .commit();
}

private void setMethodChannels(FlutterEngine flutterEngine) {
    new MethodChannel(
            flutterEngine.getDartExecutor().getBinaryMessenger(),
            FLUTTER_CHANNEL
    ).setMethodCallHandler(
            (call, result) -> {
                if ("resultJson".equals(call.method)) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("code", call.arguments.toString());
                    setResult(RESULT_OK, resultIntent);

                    result.success(true);
                    finish();
                }
            }
    );
}

```

Чтобы запустить SDK и отправить ему необходимые параметры, мы используем следующий код:

Kotlin
``` kotlin
private fun initFlutterEngine() {
    val flutterEngine = FlutterEngine(this)
    val bundle = intent.extras

    val uri = Uri.Builder()
    uri
        .appendPath("login")
        .appendQueryParameter("client_id", __YOUR_CLIENT_ID__)
        .appendQueryParameter("scope", "address,contacts,doc_data,common_data")
        .appendQueryParameter("language", "uz")
        .appendQueryParameter("scan_mode", bundle?.getString("mode"))
        .appendQueryParameter("passport", "AA1234567")
        .appendQueryParameter("birthday", "01.01.2000")
        //.appendQueryParameter("user_hash", "1234567891234567")

    flutterEngine.navigationChannel.setInitialRoute(uri.toString())
    flutterEngine.getDartExecutor().executeDartEntrypoint(
        DartExecutor.DartEntrypoint.createDefault()
    )

    FlutterEngineCache
        .getInstance()
        .put(FLUTTER_ENGINE_ID, flutterEngine)

    setMethodChannels(flutterEngine)
}
```


Java
```java
private void initFlutterEngine() {
    FlutterEngine flutterEngine = new FlutterEngine(this);
    Bundle bundle = getIntent().getExtras();

    Uri.Builder uri = new Uri.Builder();
    uri
            .appendPath("login")
            .appendQueryParameter("client_id", __YOUR_CLIENT_ID__)
            .appendQueryParameter("scope", "address,contacts,doc_data,common_data")
            .appendQueryParameter("language", "uz")
            .appendQueryParameter("scan_mode", bundle.getString("mode"))
            //.appendQueryParameter("user_hash", "1234567891234567")
            .appendQueryParameter("passport", "AA1234567")
            .appendQueryParameter("birthday", "01.01.2000");

    flutterEngine.getNavigationChannel().setInitialRoute(uri.toString());
    flutterEngine.getDartExecutor().executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
    );

    FlutterEngineCache
            .getInstance()
            .put(FLUTTER_ENGINE_ID, flutterEngine);

    setMethodChannels(flutterEngine);
}
```

В функции `initFlutterEngine` мы передаем все параметры в SDK через Uri.
- `client_id`: специальный идентификатор, предоставленный вам
- `scope`: список необходимых вам групп данных
- `language`: язык интерфейса SDK. Доступные языки
     - `en`: Узбекский (латынь)
     - `ru`: Русский
     - `en`: Английский
- `scan_mode`: режим сканирования лица. Он может быть `simple` или `strong`.
- `user_hash`: паспортные данные пользователя и хеш дня рождения
- `passport`: номер паспорта пользователя
- `birthday`: день рождения пользователя


! если ввести (`passport` и `birthday`) или `user_hash` в правильном формате, SDK не откроет окно запроса паспортной информации. 


## Загрузка SDK 
Чтобы вызвать SDK, вам просто нужно вызвать действие, которое вы создали вверху. Для этого:


Kotlin
```kotlin
...

button.setOnClickListener {
    val intent = Intent(this, FaceIdActivity::class.java)
    intent.putExtra("mode", "simple")
    startActivityForResult(intent, activityRequestCode)
}

...
```

Java
```java
...

button.setOnClickListener(new View.OnClickListener() {
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, FaceIdActivity.class);
        intent.putExtra("mode", "simple");
        startActivityForResult(intent, activityRequestCode);
    }
});

...
```

Чтобы зафиксировать полученную информацию, мы используем `onActivityResult`:

Kotlin
```kotlin
...

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == activityRequestCode) {
        if (resultCode == RESULT_OK) {
            textView.setText(data?.getStringExtra("code"));
        }
    }
}
```

Java
```java
...
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == activityRequestCode) {
        if (resultCode == RESULT_OK) {
            assert data != null;
            textView.setText(data.getStringExtra("code"));
        }
    }
}
```

Полученный код позволяет получить всю информацию о пользователе через сервер:

## Android manifest
Открываем файл AndroidManifest.xml и добавляем следующий код

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.VIBRATE" />
```

И мы добавляем `android:windowSoftInputMode="adjustResize"` к нашему FaceId Activity, чтобы не возникало проблем с использованием клавиатуры.

```xml
<activity
    android:name=".FaceIdActivity"
    android:label="@string/title_activity_face_id"
    android:windowSoftInputMode="adjustResize"/>
```


