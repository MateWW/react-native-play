package com.reactnativeplay;

public class ChannelData {
  private int maxInputSize;
  private int maxBatchSize;
  private int mappedOutputSize;
  private double[] output;
  private int outputIndex;
  private double currentBatchSum;
  private int currentBatchIndex;


  public ChannelData(int maxInputSize, int mappedOutputSize) {
    maxBatchSize = (int) Math.ceil(maxInputSize / mappedOutputSize);
    this.maxInputSize = maxInputSize;
    this.mappedOutputSize = mappedOutputSize;
    output = new double[mappedOutputSize];
    outputIndex = 0;
    currentBatchSum = 0;
    currentBatchIndex = 0;
  }

  public void add(double value) {
    currentBatchSum += value;
    currentBatchIndex++;
    int currentIndex = outputIndex*maxBatchSize+currentBatchIndex;

    if (maxBatchSize == currentBatchIndex-1 || maxInputSize-1 == currentIndex) {
      output[outputIndex++] = currentBatchSum / currentBatchIndex;
      currentBatchIndex = 0;
      currentBatchSum = 0;
    }

    if(outputIndex == mappedOutputSize-1) {
      onBatchLimitExceeded(FFT.fft(output.clone()));
      output = new double[mappedOutputSize];
      outputIndex = 0;
    }
  }



  public void onBatchLimitExceeded(double[] output) { }
}
