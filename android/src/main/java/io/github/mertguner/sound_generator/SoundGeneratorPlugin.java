package io.github.mertguner.sound_generator;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.github.mertguner.sound_generator.handlers.getOneCycleDataHandler;
import io.github.mertguner.sound_generator.handlers.isPlayingStreamHandler;
import io.github.mertguner.sound_generator.models.WaveTypes;

/** SoundGeneratorPlugin */
public class SoundGeneratorPlugin implements FlutterPlugin, MethodCallHandler {
  private MethodChannel channel;
  private EventChannel onChangeIsPlayingChannel;
  private EventChannel onOneCycleDataChannel;
  private final SoundGenerator soundGenerator = new SoundGenerator();
  private isPlayingStreamHandler isPlayingHandler;
  private getOneCycleDataHandler oneCycleDataHandler;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "sound_generator");
    channel.setMethodCallHandler(this);

    isPlayingHandler = new isPlayingStreamHandler();
    oneCycleDataHandler = new getOneCycleDataHandler();

    onChangeIsPlayingChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), isPlayingStreamHandler.NATIVE_CHANNEL_EVENT);
    onChangeIsPlayingChannel.setStreamHandler(isPlayingHandler);

    onOneCycleDataChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), getOneCycleDataHandler.NATIVE_CHANNEL_EVENT);
    onOneCycleDataChannel.setStreamHandler(oneCycleDataHandler);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("init")) {
      int sampleRate = call.argument("sampleRate");
      result.success(soundGenerator.init(sampleRate));
    } else if (call.method.equals("release")) {
      soundGenerator.release();
    } else if (call.method.equals("play")) {
      soundGenerator.startPlayback();
    } else if (call.method.equals("stop")) {
      soundGenerator.stopPlayback();
    } else if (call.method.equals("isPlaying")) {
      result.success(soundGenerator.isPlaying());
    } else if (call.method.equals("dB")) {
      result.success(soundGenerator.getDecibel());
    } else if (call.method.equals("volume")) {
      result.success(soundGenerator.getVolume());
    } else if (call.method.equals("setAutoUpdateOneCycleSample")) {
      boolean autoUpdateOneCycleSample = call.argument("autoUpdateOneCycleSample");
      soundGenerator.setAutoUpdateOneCycleSample(autoUpdateOneCycleSample);
    } else if (call.method.equals("setFrequency")) {
      double frequency = call.argument("frequency");
      soundGenerator.setFrequency((float) frequency);
    } else if (call.method.equals("setWaveform")) {
      String waveType = call.argument("waveType");
      soundGenerator.setWaveform(WaveTypes.valueOf(waveType));
    } else if (call.method.equals("setBalance")) {
      double balance = call.argument("balance");
      soundGenerator.setBalance((float) balance);
    } else if (call.method.equals("setVolume")) {
      double volume = call.argument("volume");
      soundGenerator.setVolume((float) volume, true);
    } else if (call.method.equals("setDecibel")) {
      double dB = call.argument("dB");
      soundGenerator.setDecibel((float) dB);
    } else if (call.method.equals("getSampleRate")) {
      result.success(soundGenerator.getSampleRate());
    } else if (call.method.equals("refreshOneCycleData")) {
      soundGenerator.refreshOneCycleData();
    } else if (call.method.equals("setCleanStart")) {
      boolean cleanStart = call.argument("cleanStart");
      soundGenerator.setCleanStart(cleanStart);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
    channel = null;

    onChangeIsPlayingChannel.setStreamHandler(null);
    onChangeIsPlayingChannel = null;

    onOneCycleDataChannel.setStreamHandler(null);
    onOneCycleDataChannel = null;

    isPlayingHandler = null;
    oneCycleDataHandler = null;

    soundGenerator.release();
  }
}
