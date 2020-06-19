import { KeycloakService } from 'keycloak-angular';
import { environment } from 'src/environments/environment';
 
export function initializer(keycloak: KeycloakService): () => Promise<any> {
  return (): Promise<any> => {
    return new Promise(async (resolve, reject) => {
        try {
            await keycloak.init({
              config: {
                url: environment.KEYCLOAK_URL,
                realm: environment.KEYCLOAK_REALM,
                clientId: environment.KEYCLOAK_CLIENTID
              },
              initOptions: {
                onLoad: 'login-required',
                checkLoginIframe: false
              },
              enableBearerInterceptor: true,
              bearerExcludedUrls: []
            });
            resolve();
          } catch (error) {
            reject();
          }
    });
  };
}
