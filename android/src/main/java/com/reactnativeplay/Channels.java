package com.reactnativeplay;
import java.nio.ShortBuffer;

public class Channels {
  private ChannelData[] channels;
  private double[][] dataToEmit;
  private int registered = 0;

  public Channels(int channels, int maxInputSize, int mappedOutputSize) {
    this.channels = new ChannelData[channels];
    this.dataToEmit = new double[channels][];
    for(int i = 0; i<channels; i++){
      final int channelIndex = i;
      this.channels[i] = new ChannelData(maxInputSize, mappedOutputSize){
        @Override
        public void onBatchLimitExceeded(double[] output) {
          super.onBatchLimitExceeded(output);
          registerEmit(channelIndex, output);
        }
      };
    }
  }

  private void registerEmit(int channel, double[] data){
    if (dataToEmit[channel] == null){
      registered++;
    }
    dataToEmit[channel] = data;

    if (registered == channels.length) {
      this.onEmit(dataToEmit);
      dataToEmit = new double[channels.length][];
      registered = 0;
    }

  }

  public void onEmit(double[][] output) {}

  public void parseData(ShortBuffer data) {
    for(int i = 0; i<data.limit(); i++){
      int channel = i % channels.length;
      channels[channel].add(data.get(i));
    }
  }
}
