package bsep.pki.PublicKeyInfrastructure.config;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmsConfig {

    public static List<String> secureRSASigningAlgorithms = new ArrayList<>()
    {
        {
            add("MD2withRSA");
            add("MD5withRSA");
            add("RIPEMD128withRSA");
            add("RIPEMD160withRSA");
            add("RIPEMD256withRSA");
            add("SHA1withRSA");
            add("SHA224withRSA");
            add("SHA256withRSA");
            add("SHA384withRSA");
            add("SHA512withRSA");
        }
    };


    public static List<String> secureDSASigningAlgorithms = new ArrayList<>()
    {
        {
            add("SHA1withDSA");
            add("SHA224withDSA");
            add("SHA256withDSA");
            add("SHA384withDSA");
            add("SHA512withDSA");
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

