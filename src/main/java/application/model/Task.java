package application.model;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@NoArgsConstructor
//@AllArgsConstructor
@Entity
public class Task {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    public Family getFamily() {
        return family;
    }

    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private UUID id;
    private String name;

    public void setFamily(Family family) {
        this.family = family;
    }

    public Task(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
