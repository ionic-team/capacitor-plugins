import { PatchAPICalls } from './implementation';

export { PatchAPICalls };

declare global {
  interface Window {
    webFetch: any;
    webXMLHttpRequest: any;
  }
}