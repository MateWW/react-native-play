import NativePlayer from './NativePlay';
// @ts-ignore
import resolveAssetSource from 'react-native/Libraries/Image/resolveAssetSource';
import {NativeEventEmitter} from 'react-native';

export class Player {
  private key: string;
  private listener: NativeEventEmitter;

  constructor() {
    if (!NativePlayer) {
      throw new Error('[Player] Failed to load NativePlayer module!');
    }
    this.key = NativePlayer.register();
    this.listener = new NativeEventEmitter(NativePlayer);
  }

  play(source: string | number) {
    const asset = resolveAssetSource(
      typeof source === 'string' ? {uri: source} : source,
    );
    if (!asset) {
      throw new Error('[Player] Failed to resolve source');
    }

    NativePlayer.play(this.key, asset.uri);
  }

  pause() {
    NativePlayer.pause(this.key);
  }

  stop() {
    NativePlayer.stop(this.key);
  }

  release() {
    NativePlayer.release(this.key);
  }

  onBitStream(cb: (bits: number[][]) => void): () => void {
    const listener = this.listener.addListener(`${this.key}_onBitStream`, cb);
    return listener.remove;
  }
}
