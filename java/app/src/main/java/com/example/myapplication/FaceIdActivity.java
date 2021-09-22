package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import io.flutter.embedding.android.FlutterFragment;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodChannel;

public class FaceIdActivity extends FragmentActivity {

    final String FLUTTER_FRAGMENT = "flutter_fragment";
    final String FLUTTER_ENGINE_ID = "flutter_engine";
    final String FLUTTER_CHANNEL = "channel/myid";
    FlutterFragment flutterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_id);

        initFlutterEngine();
        attachFlutterFragment();
    }

    private void initFlutterEngine() {
        FlutterEngine flutterEngine = new FlutterEngine(this);
        Bundle bundle = getIntent().getExtras();

        Uri.Builder uri = new Uri.Builder();
        uri
                .appendPath("login")
                .appendQueryParameter("client_id", __YOUR_CLIENT_ID__)
                .appendQueryParameter("scope", "address,contacts,doc_data,common_data")
                .appendQueryParameter("language", "uz")
                .appendQueryParameter("scan_mode", bundle.getString("mode"));
//                .appendQueryParameter("user_hash", "1234567891234567")
//                .appendQueryParameter("passport", "AA1234567")
//                .appendQueryParameter("birthday", "01.01.2000");

        flutterEngine.getNavigationChannel().setInitialRoute(uri.toString());
        flutterEngine.getDartExecutor().executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
        );

        FlutterEngineCache
                .getInstance()
                .put(FLUTTER_ENGINE_ID, flutterEngine);

        setMethodChannels(flutterEngine);
    }

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
}