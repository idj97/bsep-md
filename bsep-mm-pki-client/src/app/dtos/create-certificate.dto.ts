import { Name } from './name.dto';

export class CreateCertificate {
    keyGenerationAlgorithm: string;
    keySize: number;
    signatureAlgorithm: string;

    validFrom: string;
    validUntil: string;

    selfSigned: boolean;

    serialNumber: string;
    issuingCaSerialNumber: string;

    name: Name = new Name();
    extensions: any[] = [];

    //Used for certificate requests
    csrId: number;
}
