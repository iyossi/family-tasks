package model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FamilyMember {
    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private int id;

    private String name;
    private int age;
}
