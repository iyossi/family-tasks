package application.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "family")
public class Family {

    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private UUID id;
    private String name;


    @OneToMany
    private List<FamilyMember> members;

    @OneToMany
    private List<Task> tasks;


    public Family(String name) {
        this.name = name;
    }
}
