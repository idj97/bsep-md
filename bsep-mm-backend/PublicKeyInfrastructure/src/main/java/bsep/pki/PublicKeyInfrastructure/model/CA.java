package bsep.pki.PublicKeyInfrastructure.model;

import bsep.pki.PublicKeyInfrastructure.model.enums.CAType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

//TODO: DELETE (probably)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Certificate certificate;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<CA> childs = new HashSet<>();

    @ManyToOne(cascade = CascadeType.ALL)
    private CA parent;

    @Enumerated(value = EnumType.STRING)
    private CAType type;
}
