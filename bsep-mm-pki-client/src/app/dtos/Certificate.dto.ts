import { Revocation } from './Revocation.dto';

export class Certificate {
    
    algorithm: string;
    keySize: number;
    periodType: string;
    signWith: number;
    signatureAlgorithm: string;
    commonName: string;
    givenName: string;
    surname: string;
    organisation: string;
    organisationUnit: string;
    state: string;
    country: string;
    email: string;
    localityName: string;
    domainComponent: string;

    validFrom: string;
    validityInMonths: number;
    validUntil: string;

    serialNumber: number;
    certificateType: string;
    revocation: Revocation;

    constructor() {
        
    }
}