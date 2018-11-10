package model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "family")
public class Family {

    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private int id;
    private String name;


    @OneToMany
    private List<FamilyMember> members;

    @OneToMany
    private List<Task> tasks;


    public Family(String name, int id) {
        this.id = id;
        this.name = name;
    }
}
