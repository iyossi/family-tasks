package application.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FamilyMember {
    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private UUID id;

    private String name;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    public FamilyMember(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
