package application.model;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
@Entity(name = "family")
public class Family {

    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private UUID id;

    private String name;


    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "family")
    private List<FamilyMember> members;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Task> tasks;


    public Family() {
        members = new ArrayList<>();

    }

    public Family(String name) {
        this.name = name;
        members = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FamilyMember> getMembers() {
        return members;
    }

    public void setMembers(List<FamilyMember> members) {
        this.members = members;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
