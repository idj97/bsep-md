# bsep-md

## Guides:
The pages listed below contain information that helped us develop the application:
- https://sites.google.com/site/ddmwsst/digital-certificates
- https://www.sslshopper.com/what-is-a-csr-certificate-signing-request.

## Commands:
`openssl crl -inform DER -noout -text -in public/pki_revocations.crl`

`keytool -list -v -keystore pki_keystore.jks -storepass password`


