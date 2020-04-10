import { Certificate } from './Certificate.dto';

export class CertificateAuthority {
    
    id: number;
    caIssuerId: number;
    caType: string;
    certificateDto: Certificate;

    constructor() {
        this.certificateDto = new Certificate();
    }
}