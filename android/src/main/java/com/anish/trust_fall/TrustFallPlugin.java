package com.anish.trust_fall;

import android.content.Context;
import android.location.Location;

import com.anish.trust_fall.Emulator.EmulatorCheck;
import com.anish.trust_fall.ExternalStorage.ExternalStorageCheck;
import com.anish.trust_fall.MockLocation.MockLocationCheck;
import com.anish.trust_fall.Rooted.RootedCheck;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** TrustFallPlugin */
public class TrustFallPlugin implements MethodCallHandler,  FlutterPlugin{
  /** Plugin registration. */

  private final Context context;
  private MethodChannel channel;
  private FlutterPluginBinding binding;

  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "trust_fall");
    channel.setMethodCallHandler(new TrustFallPlugin(registrar.context()));
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    channel = new MethodChannel(binding.getBinaryMessenger(), "trust_fall");
    this.binding = binding;
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  private TrustFallPlugin(Context context){
    this.context = context;
  }

  @Override
  public void onMethodCall(MethodCall call, final Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("isJailBroken")) {
      result.success(RootedCheck.isJailBroken(context));
    } else if (call.method.equals("canMockLocation")) {
      MockLocationCheck.LocationResult locationResult = new MockLocationCheck.LocationResult(){
        @Override
        public void gotLocation(Location location){
          //Got the location!
          if(location != null){
            result.success(location.isFromMockProvider());
          }else {
            result.success(false);
          }
        }
      };
      MockLocationCheck mockLocationCheck = new MockLocationCheck();
      mockLocationCheck.getLocation(context, locationResult);
    }else if (call.method.equals("isRealDevice")) {
      result.success(!EmulatorCheck.isEmulator());
    }else if (call.method.equals("isOnExternalStorage")) {
      result.success(ExternalStorageCheck.isOnExternalStorage(context));
    }
    else {
      result.notImplemented();
    }
  }
}
