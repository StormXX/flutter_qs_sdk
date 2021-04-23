package com.stormxx.qs_sdk;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import com.qs.wiget.PrintUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.EventSink;
import io.flutter.plugin.common.EventChannel.StreamHandler;

/** QsSdkPlugin */
public class QsSdkPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  private EventChannel eventChannel;

  private static final String Scan_CHANNEL = "com.beautinow.qs.scan";
  IntentFilter intentFilter;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "qs_sdk");
    channel.setMethodCallHandler(this);

    PrintUtils.initPrintUtils(this);
    intentFilter = new IntentFilter();
		intentFilter.addAction("com.qs.scancode");

    eventChannel = EventChannel(flutterPluginBinding.getBinaryMessenger(), Scan_CHANNEL);

    eventChannel.setStreamHandler(
      new StreamHandler() {
        private BroadcastReceiver scanBroadcastReceiver;
        @Override
        public void onListen(Object arguments, EventSink events) {
          scanBroadcastReceiver = createScanBroadcastReceiver(events);
          registerReceiver(
              scanBroadcastReceiver, intentFilter);
        }

        @Override
        public void onCancel(Object arguments) {
          unregisterReceiver(scanBroadcastReceiver);
          scanBroadcastReceiver = null;
        }
      }
    );
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("initSDK")) {
      PrintUtils.initPrintUtils(this);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  private BroadcastReceiver createScanBroadcastReceiver(final EventSink events) {
    return new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
			
			String code = intent.getExtras().getString("data");			
      events.success(code);
		}
    };
  }
}
