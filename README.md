# bsep-md

## Guides:
The pages listed below contain information that helped us develop the application:
- https://sites.google.com/site/ddmwsst/digital-certificates
- https://www.sslshopper.com/what-is-a-csr-certificate-signing-request.

## Commands:
`openssl crl -inform DER -noout -text -in public/pki_revocations.crl`

`keytool -list -v -keystore pki_keystore.jks -storepass password`

## Java SSL resources:
- Sranja koja se koriste da bi se napravila konfiguracija za **https** i **ssl autenfikaciju** koja se pri pokretanju ubacuje u springov embedded tomcat. Autentifikacija je implementirana u SSLTrustManager klasi cija se putanja ubacuje konfiguraciju koju ce tomcat ucitati. https://tomcat.apache.org/tomcat-9.0-doc/config/http.html
- Sranja koja koristimo da bi smo proverili Certificate Path (sa OCSP) u SSLTrustManager klasi. https://docs.oracle.com/javase/8/docs/technotes/guides/security/certpath/CertPathProgGuide.html

## Java run options:
`-Djava.security.debug=certpath` - enable full certpath debugging



