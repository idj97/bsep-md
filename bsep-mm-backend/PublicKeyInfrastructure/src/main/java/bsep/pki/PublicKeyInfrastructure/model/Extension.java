package bsep.pki.PublicKeyInfrastructure.model;

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
public class Extension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "extension", cascade = CascadeType.ALL)
    private Set<ExtensionAttribute> attributes = new HashSet<>();

    @ManyToOne(cascade = CascadeType.ALL)
    private Certificate certificate;

}
