import * as React from 'react';

import { StyleSheet, View, Button } from 'react-native';
import { usePlayer } from 'react-native-play';
import Sample from './sample.mp3'

const URL =
  'https://filesamples.com/samples/audio/mp3/Symphony%20No.6%20(1st%20movement).mp3';

const MAX_BUFFER = 16384

export default function App() {
  const [bitStream, setBitStream] = React.useState<number[][]>([]);
  const player = usePlayer();

  React.useEffect(() => {
    return player.onBitStream((bits) => {
      console.log(bits);
      setBitStream(bits);
    })
  },[])

  return (
    <View style={styles.container}>
      <Button
        title="Play Remote"
        onPress={() => {
          player.play(URL);
        }}
      />
      <Button
        title="Play Local(require)"
        onPress={() => {
          player.play(Sample);
        }}
      />
      <Button
        title="Play Local(assets)"
        onPress={() => {
          player.play(Sample);
        }}
      />
      <Button
        title="Pause"
        onPress={() => {
          player.pause();
        }}
      />

      <View style={styles.jumpBar}>
        {bitStream[0] && bitStream[1] ? bitStream[0].map((item, index) => {
          const height1 = item/MAX_BUFFER * 128;
          const height2 = bitStream[1]![index]!/MAX_BUFFER * 128;
          return (
            <View style={styles.lineWrapper}>
              <View style={styles.topWrapper}>
                <View style={[styles.channelTop, {height: height1}]} />
              </View>
              <View style={styles.bottomWrapper}>
                <View style={[styles.channelBottom, {height: height2}]} />
              </View>
            </View>
          )
        }): null}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
  jumpBar: {
    flexDirection:'row',
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: 24,
  },
  channelTop: {
    width: 2,
    backgroundColor: 'tomato',
  },
  channelBottom: {
    width: 2,
    backgroundColor: 'aquamarine',
  },
  topWrapper: {
    height:128,
    justifyContent: 'flex-end'
  },
  bottomWrapper: {
    height:128,
    justifyContent: 'flex-start'
  },
  lineWrapper: {
    alignItems: 'center',
    justifyContent: 'center',
    height: 256
  }
});
