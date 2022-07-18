package com.reactnativeplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.ReactInvalidPropertyException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.TeeAudioProcessor;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.UUID;

@ReactModule(name = PlayModule.NAME)
public class PlayModule extends NativePlaySpec {
    public static final String NAME = "Play";
    private HashMap<String,ExoPlayer> players = new HashMap<>();

    public PlayModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

  private void sendEvent(String eventName, @Nullable Object data) {
    ReactApplicationContext reactApplicationContext = getReactApplicationContext();

    if (reactApplicationContext == null) {
      return;
    }
    // We don't gain anything interesting from logging here, and it's an extremely common
    // race condition for an AppState event to be triggered as the Catalyst instance is being
    // set up or torn down. So, just fail silently here.
    if (!reactApplicationContext.hasActiveReactInstance()) {
      return;
    }
    reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, data);
  }

  @Override
  public void addListener(String eventName) {
    // iOS only
  }

  @Override
  public void removeListeners(double count) {
    // iOS only
  }


  @Override
  public String register() {
    ReactApplicationContext reactApplicationContext = getReactApplicationContext();
    String id = UUID.randomUUID().toString();
    ExoPlayer player = new ExoPlayer.Builder(reactApplicationContext)
      .setRenderersFactory(new CustomRendererFactory(reactApplicationContext, new TeeAudioProcessor.AudioBufferSink() {
        private int sampleRateHz;
        private int callsPerSecond = 16;
        private int batchSize;
        private int channelCount;
        private Channels channels;
        private double[][] dataSet;
        private int count;

        private void emit(double[][] data) {
          WritableArray args = Arguments.createArray();
          for(double[] channelData: data) {
            args.pushArray(Arguments.fromArray(channelData));
          }
          sendEvent(id+"_onBitStream", args);
        }

        @Override
        public void flush(int sampleRateHz, int channelCount, int encoding) {
          // nothing to do here
          this.sampleRateHz = sampleRateHz;
          this.channelCount = channelCount;
          int nonDividableBatchSize = sampleRateHz/this.callsPerSecond;
          this.batchSize = nonDividableBatchSize - nonDividableBatchSize % channelCount;
          this.channels = new Channels(channelCount, sampleRateHz/8, 128){
            @Override
            public void onEmit(double[][] output) {
              super.onEmit(output);
              emit(output);
            }
          };
        }

        @Override
        public void handleBuffer(ByteBuffer buffer) {
          // TODO: map encoding to buffer
          ShortBuffer shortBuffer = buffer.asShortBuffer();
          this.channels.parseData(shortBuffer);
        }
      }))
      .build();
    player.addListener(new Player.Listener() {
      @Override
      public void onPlayerError(PlaybackException error) {
        Throwable cause = error.getCause();
      }
    });
    players.put(id, player);
    return id;
  }

  @Override
  public boolean play(String id, String url) {
    ExoPlayer player = this.getPlayerOrThrow(id);
    MediaItem mediaItem = MediaItem.fromUri(url);
    player.setMediaItem(mediaItem);
    player.prepare();
    player.play();
    return true;
  }

  @Override
  public boolean pause(String id) {
    ExoPlayer player = this.getPlayerOrThrow(id);
    player.pause();
    return true;
  }

  @Override
  public boolean stop(String id) {
    ExoPlayer player = this.getPlayerOrThrow(id);
    player.stop();
    return true;
  }

  @Override
  public boolean release(String key) {
    ExoPlayer player = getPlayerOrThrow(key);
    player.release();
    players.remove(key);
    return true;
  }

  @Override
  public void onBitStream(Callback callback) {

  }

  private ExoPlayer getPlayerOrThrow(String id) {
    ExoPlayer player = players.get(id);
    if (player == null) {
      throw new ReactInvalidPropertyException("Failed to select player with key", id, players.keySet().toString());
    }
    return player;
  }


  @Override
    @NonNull
    public String getName() {
        return NAME;
    }
}
