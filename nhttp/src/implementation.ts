import { registerPlugin } from '@capacitor/core';

import type { FetchOptions } from './definitions';
import { CapacitorNHttpPluginWeb } from './web';


export interface CapacitorNHttpPlugin {
    fetch(options: FetchOptions): Promise<Response>;
}

export const CapacitorNHttp = registerPlugin<CapacitorNHttpPlugin>(
    'CapacitorNHttp',
    {
        web: () => new CapacitorNHttpPluginWeb(),
    }
);

export function PatchAPICalls(): void {
    window['webFetch'] = window.fetch;
    window['webXMLHttpRequest'] = window.XMLHttpRequest;
    
    window.fetch = async (resource: RequestInfo, config?: RequestInit) => {
        try {
            // intercept request & pass to the bridge
            const response = await CapacitorNHttp.fetch({
                resource: resource,
                config: config,
            });
        
            // intercept & parse response before returning
            if (!(response instanceof Response)) {
                const blob = new Blob([(response as any).data], {type: "application/json"});
                const opts = {
                    headers: (response as any).headers,
                    status: (response as any).status
                }
                return new Response(blob, opts);
            }
        
            return response;
        }
        catch (error) {
            return Promise.reject(error);
        }
    };

    // window.XMLHttpRequest.prototype.open =
    //     async (method: string, url: string | URL, ...opts: any) => {
    //     return open.call(this, method, url, ...opts);
    // };
}