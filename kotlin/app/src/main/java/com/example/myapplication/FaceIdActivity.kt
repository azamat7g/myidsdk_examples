package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel

class FaceIdActivity : FragmentActivity() {

    companion object {
        private const val FLUTTER_FRAGMENT = "flutter_fragment"
        private const val FLUTTER_ENGINE_ID = "flutter_engine"
        private const val FLUTTER_CHANNEL = "channel/myid"
        private var flutterFragment: FlutterFragment? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_id)

        initFlutterEngine()
        attachFlutterFragment()
    }

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
//            .appendQueryParameter("passport", "AA1234567")
//            .appendQueryParameter("birthday", "01.01.2000")
//            .appendQueryParameter("user_hash", "1234567891234567")

        flutterEngine.navigationChannel.setInitialRoute(uri.toString())
        flutterEngine.getDartExecutor().executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )

        FlutterEngineCache
            .getInstance()
            .put(FLUTTER_ENGINE_ID, flutterEngine)

        setMethodChannels(flutterEngine)
    }

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
}