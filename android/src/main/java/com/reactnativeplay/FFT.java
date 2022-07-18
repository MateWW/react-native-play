package com.reactnativeplay;

public class FFT {
  public static double[] dft(double[] items) {
    int itemsCount = items.length;

    double[] result = new double[itemsCount];

    for(int outputIndex = 0; outputIndex<itemsCount; outputIndex++){
      double[] temp = new double[2];
      for(int n = 0; n<itemsCount; n++){
        double angle = 2*Math.PI/itemsCount*outputIndex*n;
        temp[0] += Math.cos(angle)*items[n];
        temp[1] += -1 * Math.sin(angle)*items[n];
      }
      result[outputIndex] = Math.sqrt(Math.pow(temp[0],2)+Math.pow(temp[1],2));
    }
    return result;
  }

  public static double[] fft(double[] input) {
    double[] output = new double[input.length];
    int bits = (int) (Math.log(input.length) / Math.log(2));

    for (int j = 1; j < input.length / 2; j++) {

      int swapPos = bitReverse(j, bits);
      double temp = input[j];
      input[j] = input[swapPos];
      input[swapPos] = temp;
    }
    for (int N = 2; N <= input.length; N <<= 1) {
      for (int i = 0; i < input.length; i += N) {
        for (int k = 0; k < N / 2; k++) {
          int evenIndex = Math.min(i + k, input.length-1);
          int oddIndex = Math.min(i + k + (N / 2), input.length-1);
          double even = input[evenIndex];
          double odd = input[oddIndex];

          double term = (-2 * Math.PI * k) / (double) N;
          double[] exp = {Math.sin(term) * odd, Math.cos(term)* odd};

          output[evenIndex] = Math.sqrt(Math.pow(even+exp[0],2) + Math.pow(exp[1],2));
          output[oddIndex] = Math.sqrt(Math.pow(even-exp[0],2) + Math.pow(-1 * exp[1],2));
        }
      }
    }
    return output;

  }

  public static int bitReverse(int n, int bits) {
    int reversedN = n;
    int count = bits - 1;

    n >>= 1;
    while (n > 0) {
      reversedN = (reversedN << 1) | (n & 1);
      count--;
      n >>= 1;
    }

    return ((reversedN << count) & ((1 << bits) - 1));
  }
}
