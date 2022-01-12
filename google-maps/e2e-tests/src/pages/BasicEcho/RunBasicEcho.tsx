import { CapacitorGoogleMaps } from '@capacitor/google-maps';
import { IonButton, IonTextarea } from '@ionic/react';
import { useState } from 'react';
import BaseTestingPage from '../../components/BaseTestingPage';

const SetAndGetValuePage: React.FC = () => {
  const [commandOutput, setCommandOutput] = useState('');

  async function runEcho() {
    try {
      setCommandOutput((await CapacitorGoogleMaps.echo({value: "WOW!"})).value);
    } catch(e: any) {
      setCommandOutput(e.message);
    }
  }

  return (
    <BaseTestingPage pageTitle="Set &amp; Get Value">
      <div>
        <IonButton expand="block" id="runEchoButton" onClick={runEcho}>
          Run Echo
        </IonButton>
      </div>
      <div>
        <IonTextarea id="commandOutput" value={commandOutput}></IonTextarea>
      </div>
    </BaseTestingPage>
  );
};

export default SetAndGetValuePage;
