package bsep.pki.PublicKeyInfrastructure.model.enums;

public enum RevokeReason {
    UNSPECIFIED(0),
    KEY_COMPROMISE(1),
    CA_COMPROMISE(2),
    PRIVILEGE_WITHDRAWN(9);

    Integer key;

    RevokeReason(Integer key) {
        this.key = key;
    }

    public Integer getKey() {
        return key;
    }
}
