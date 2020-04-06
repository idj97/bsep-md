package com.bsep.mm.MilitaryMonitoring.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "certificateAuthority", cascade = CascadeType.ALL)
    private Set<Certificate> issuedCertificates = new HashSet<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<CertificateAuthority> childs = new HashSet<>();

    @ManyToOne(cascade = CascadeType.ALL)
    private CertificateAuthority parent;
}
