import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { KeycloakAuthGuard, KeycloakService } from 'keycloak-angular';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationGuard extends KeycloakAuthGuard implements CanActivate {

  constructor(protected router: Router, protected keycloakAngular: KeycloakService) {
    super(router, keycloakAngular);
  }

  isAccessAllowed(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
    
    return new Promise((resolve, reject) => {
      if (!this.authenticated) {
        this.keycloakAngular.login().catch(e => console.error(e));
        return reject(false);
      }

      const requiredRoles: string[] = route.data.roles;
      if (!requiredRoles || requiredRoles.length === 0) {
        return resolve(true);
      } else {
        if (!this.roles || this.roles.length === 0) {
          return resolve(false);
        }

        for (let i = 0; i < requiredRoles.length; i++) {
          if(this.roles.includes(requiredRoles[i])) {
            return resolve(true);
          }
        }

        return resolve(false);
      }

    });
  }

  
}
