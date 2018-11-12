package application.model;


import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

//@Data
@NoArgsConstructor
//@AllArgsConstructor
@Entity(name = "FamilyMember")
public class FamilyMember {
    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private UUID id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    private String name;
    private int age;

    public Family getFamily() {
        return family;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;


    public FamilyMember(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void setFamily(Family family) {
        this.family = family;
    }
}
