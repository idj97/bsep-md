import { Revocation } from './Revocation.dto';

export class Certificate {
    
    commonName: string;
    givenName: string;
    surname: string;
    organisation: string;
    organisationUnit: string;
    country: string;
    email: string;

    validFrom: Date;
    validUnit: Date;

    serialNumber: number;
    certificateType: string;
    revocation: Revocation;

    constructor() {
        
    }
}