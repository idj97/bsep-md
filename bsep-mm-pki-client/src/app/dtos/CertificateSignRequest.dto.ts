
export class CertificateSignRequest {

    id: number;
    commonName: string;
    organisation: string;
    organisationUnit: string;
    city: string;
    country: string;
    email: string;
    publicKey: string;

    constructor() {
        
    }
}