package bsep.pki.PublicKeyInfrastructure.config;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmsConfig {
    public static List<String> secureSigningAlgorithms = new ArrayList<>()
    {
        {
            add("Ed25519");
            add("Ed448");
            add("GOST3411withGOST3410 (GOST3411withGOST3410-94)");
            add("GOST3411withECGOST3410 (GOST3411withGOST3410-2001)");
            add("MD2withRSA");
            add("MD5withRSA");
            add("SHA1withRSA");
            add("RIPEMD128withRSA");
            add("RIPEMD160withRSA");
            add("RIPEMD160withECDSA");
            add("RIPEMD256withRSA");
            add("SHA1withDSA");
            add("SHA224withDSA");
            add("SHA256withDSA");
            add("SHA384withDSA");
            add("SHA512withDSA");
            add("SHA3-224withDSA");
            add("SHA3-256withDSA");
            add("SHA3-384withDSA");
            add("SHA3-512withDSA");
            add("SHA1withDDSA");
            add("SHA224withDDSA");
            add("SHA256withDDSA");
            add("SHA384withDDSA");
            add("SHA512withDDSA");
            add("SHA3-224withDDSA");
            add("SHA3-256withDDSA");
            add("SHA3-384withDDSA");
            add("SHA3-512withDDSA");
            add("NONEwithDSA");
            add("SHA1withDetECDSA");
            add("SHA224withECDDSA");
            add("SHA256withECDDSA");
            add("SHA384withECDDSA");
            add("SHA512withECDDSA");
            add("SHA1withECDSA");
            add("NONEwithECDSA");
            add("SHA224withECDSA");
            add("SHA256withECDSA");
            add("SHA384withECDSA");
            add("SHA512withECDSA");
            add("SHA3-224withECDSA");
            add("SHA3-256withECDSA");
            add("SHA3-384withECDSA");
            add("SHA3-512withECDSA");
            add("SHA1withECNR");
            add("SHA224withECNR");
            add("SHA256withECNR");
            add("SHA384withECNR");
            add("SHA512withECNR");
            add("SHA224withRSA");
            add("SHA256withRSA");
            add("SHA384withRSA");
            add("SHA512withRSA");
            add("SHA512(224)withRSA");
            add("SHA512(256)withRSA");
            add("SHA3-224withRSA");
            add("SHA3-256withRSA");
            add("SHA3-384withRSA");
            add("SHA3-512withRSA");
            add("SHA1withRSAandMGF1");
            add("SHA256withRSAandMGF1");
            add("SHA384withRSAandMGF1");
            add("SHA512withRSAandMGF1");
            add("SHA512(224)withRSAandMGF1");
            add("SHA512(256)withRSAandMGF1");
            add("SHA1withRSA/ISO9796-2");
            add("RIPEMD160withRSA/ISO9796-2");
            add("SHA1withRSA/X9.31");
            add("SHA224withRSA/X9.31");
            add("SHA256withRSA/X9.31");
            add("SHA384withRSA/X9.31");
            add("SHA512withRSA/X9.31");
            add("SHA512(224)withRSA/X9.31");
            add("SHA512(256)withRSA/X9.31");
            add("RIPEMD128withRSA/X9.31");
            add("RIPEMD160withRSA/X9.31");
            add("WHIRLPOOLwithRSA/X9.31");
            add("SHA512withSPHINCS256 (BCPQC)");
            add("SHA3-512withSPHINCS256 (BCPQC)");
            add("SHA256withSM2");
            add("SM3withSM2");
            add("LMS");
            add("XMSS-SHA256");
            add("XMSS-SHA512");
            add("XMSS-SHAKE128");
            add("XMSS-SHAKE256");
            add("XMSSMT-SHA256");
            add("XMSSMT-SHA512");
            add("XMSSMT-SHAKE128");
            add("XMSSMT-SHAKE256");
            add("SHA256withXMSS-SHA256");
            add("SHA512withXMSS-SHA512");
            add("SHAKE128withXMSS-SHAKE128");
            add("SHAKE256withXMSS-SHAKE256");
            add("SHA256withXMSSMT-SHA256");
            add("SHA512withXMSSMT-SHA512");
            add("SHAKE128withXMSSMT-SHAKE128");
            add("SHAKE256withXMSSMT-SHAKE256");
            add("qTESLA-I");
            add("qTESLA-III-SIZE");
            add("qTESLA-III-SPEED");
            add("qTESLA-P-I");
            add("qTESLA-P-II");
        }
    };

    public static List<String> secureKeyGenerationAlgorithms = new ArrayList<>()
    {
        {
            add("RSA");
            add("DSA");
        }
    };
}

