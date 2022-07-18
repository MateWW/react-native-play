import {useEffect, useState} from 'react';
import {Player} from './Player';

export function usePlayer() {
  const [player] = useState(() => new Player());

  useEffect(() => () => {
    player.release();
  },[]);

  return player;
}
