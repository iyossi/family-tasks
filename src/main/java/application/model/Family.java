package application.model;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "family")
    private List<FamilyMember> members;

    public UUID getId() {
        return id;
    }


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

    @Override
    public String toString() {
        return "Family{" +
                "id=" + id +
                ", name='" + name + '\'' +
//                ", members=" + members +
//                ", tasks=" + tasks +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Family family = (Family) o;
        return Objects.equals(id, family.id) &&
                Objects.equals(name, family.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public void addTask(Task task) {
//        if (tasks==null) {
//            tasks=new ArrayList<Task>();
//        }
        tasks.add(task);
    }
}
