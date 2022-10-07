import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  register(): string;
  play(key: string, url: string): boolean;
  pause(key: string): boolean;
  stop(key: string): boolean;
  release(key: string): boolean;

  // Events
  addListener: (eventName: string) => void;
  removeListeners: (count: number) => void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('Play');
