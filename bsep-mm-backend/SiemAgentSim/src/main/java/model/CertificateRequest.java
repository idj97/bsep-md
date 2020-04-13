package model;

public class CertificateRequest {

    private String commonName;
    private String givenName;
    private String surname;
    private String organisation;
    private String organisationUnit;
    private String city;
    private String country;
    private String email;
    private String publicKey;

    public CertificateRequest() {
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getOrganisationUnit() {
        return organisationUnit;
    }

    public void setOrganisationUnit(String organisationUnit) {
        this.organisationUnit = organisationUnit;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }


    @Override
    public String toString() {
        return "{" +
                "\"commonName\":\"" + commonName + '\"' +
                ",\"givenName\":\"" + givenName + '\"' +
                ",\"surname\":\"" + surname + '\"' +
                ",\"organisation\":\"" + organisation + '\"' +
                ",\"organisationUnit\":\"" + organisationUnit + '\"' +
                ",\"city\":\"" + city + '\"' +
                ",\"country\":\"" + country + '\"' +
                ",\"email\":\"" + email + '\"' +
                ",\"publicKey\":\"" + publicKey + '\"' +
                '}';
    }
}
