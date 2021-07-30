![alt text](img/logo.png)

[русский](README.ru.md)

# myID SDK
MyID – bu O'zbekistonda masofaviy identifikatsiyalashning eng oson, qulay va ishonchli usuli.

## Talablar
myID SDK ni o'z loyihangizga ulashdan oldin siz bizdan maxsus `clinet_id` va `clinet_secrect` tokenlarini va SDK ni zip arxivini olishingiz kerak.

## Boshlashdan oldin
Pastda keltiriladigan kodlarni to'li ishlaydigan talqinini ushbu repozitoriyadan topish mumkun https://github.com/azamat7g/myidsdk_examples

## SDKni ulash
Modul urevinidagi `build.gradle` faylni ochamiz. Android qismiga maxsus repozitoriyani qo'shamiz va `minSdkVersion` ni 21 qilib qo'yamiz:

```gradle
android {
    ....

    defaultConfig {
        minSdkVersion 21
        ...
    }

    repositories {
        maven {
            url './libs/myid'
        }
        maven {
            url "https://storage.googleapis.com/download.flutter.io"
        }
    }
}
```

"dependencies" qismida SDKni ulaymiz:

```gradle
dependencies {
    ...

    implementation 'uz.uzinfocom.myidsdk:flutter_release:1.0'
}
```

va `app/libs` papkasini ichiga SDKni zip arxivdan yoyib yuboramiz. Fayllar joylashuvi quyidagi ko'rinishga kelishi kerak

```
├── libs
│   └── myid
│       └── uz
│           └── uzinfocom
│               └── myidsdk
│                   ├── flutter_release
│                   │   ├── 1.0
│                   │   │   ├── flutter_release-1.0.aar
│                   │   │   ├── flutter_release-1.0.aar.md5
│                   │   │   ├── flutter_release-1.0.aar.sha1
│                   │   │   ├── flutter_release-1.0.pom
│                   │   │   ├── flutter_release-1.0.pom.md5
│                   │   │   └── flutter_release-1.0.pom.sha1
│                   │   ├── maven-metadata.xml
│                   │   ├── maven-metadata.xml.md5
│                   │   └── maven-metadata.xml.sha1
│                   └── modules
│                       ├── ...
│                       ......
```

## SDKni loyihaga qo'shish
SDKni loyihaga qo'shish uchun maxsus Activity yaratish kerak. Buning uchun "Android studio" dasturida `File > New > Activity > Empty activity` tanlab yangi activity qo'shamiz. Activityga nom beramiz. Misol uchun `FaceIdActivity`. Activityni muvofaqiyatlik qo'shganimizdan keyin uni `FragmentActivity`dan extend olamiz.

Kotlin
```kotlin
class FaceIdActivity : FragmentActivity() {
```

Java
```java
public class FaceIdActivity extends FragmentActivity {
```

Shundan so'ng bir nechta o'zgaruvchilar yasaymiz

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

Fragmentni hamma eventlari SDKga borishi uchun quyidagi kodni qo'shib qo'yamiz:

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

SDKni ishga tushirish va undan kelgan javobni ushlab olish uchun quyidagi kodni ishlatamiz:


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
            "result" -> {
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
                if ("result".equals(call.method)) {
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

SDKni ishga tushirish va unga kerakli parametrlarni berib yuborish uchun qiydagi koddan foydalanamiz:


Kotlin
``` kotlin
private fun initFlutterEngine() {
    val flutterEngine = FlutterEngine(this)
    val bundle = intent.extras

    val uri = Uri.Builder()
    uri
        .appendPath("login")
        .appendQueryParameter("client_id", __YOUR_CLIENT_ID__)
        .appendQueryParameter("redirect_uri", __YOUR_REDIRECT_URL__)
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
            .appendQueryParameter("redirect_uri", __YOUR_REDIRECT_URL__)
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

`initFlutterEngine` funksiyada Uri orqali hamma parametrlarni SDKga berib yuboramiz.
- client_id: siz uchun berilgan mahsus ID
- redirect_uri: ulanish paytida malumot almashish URL manzili
- scope: sizga kerakli bo'lgan ma'lumotlar guruhi ro'yhati
- language: SDKni interfeys tili. Mavjud tillar 
    - uz: O'zbekcha (lotin)
    - ru: Ruscha
    - en: Inglischa
- scan_mode: yuzni skanerlash rejimi. `simple` yoki `strong` bo'lishi mumkun
- user_hash: foydalanuvchini pasport ma'lumoti va tug'ilgan kunining hashi
- passport: foydalanuvchining pasport raqami
- birthday: foydalanuvchini tug'ilgan kuni

! agar (`passport` va `birthday`) yoki `user_hash` to'gri formatda kiritilsa SDK pasport ma'lumotlarini so'rash oynasini ochmaydi.

## SDKni chaqirish
SDKni chaqirish uchun tepada yaratgan activityni chaqirish kerak xalos. Buning uchun:

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

Kelgan ma'lumotni ushlab olish uchun esa `onActivityResult` dan foydalanamiz:

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

Olingan kodni server orqali foydalanuvchini hamma ma'lumotini olish imkoni bor

## Android manifest
AndroidManifest.xml falni ochib ushbu permissionlarni qo'shib qo'yamiz

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.VIBRATE" />
```

Va FaceId Activityimizda klaviatura ishlatishda muommo bo'lmasligi uchun `android:windowSoftInputMode="adjustResize"` ni qo'shib qo'yamiz

```xml
<activity
    android:name=".FaceIdActivity"
    android:label="@string/title_activity_face_id"
    android:windowSoftInputMode="adjustResize"/>
```
