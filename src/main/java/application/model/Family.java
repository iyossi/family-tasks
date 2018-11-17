package application.model;


import application.Exceptions.ClosedTasksListException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.*;

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
@Entity(name = "family")
public class Family {

    @Transient
    private Logger log = LoggerFactory.getLogger(Family.class);

    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Version
    private int version;

    private String name;


    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "family")
    private List<FamilyMember> members;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "family")
    private List<Task> tasks = new ArrayList<>();

    private boolean blockTaskAddition = false;

    public Family() {
        members = new ArrayList<>();

    }

    public Family(String name) {
        this.name = name;
        members = new ArrayList<>();
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

    public List<FamilyMember> getMembers() {
        return members;
    }


    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks); //  so only Family can modify the list
    }

    public void addTask(Task task) throws ClosedTasksListException {
        if (!blockTaskAddition) {
            tasks.add(task);
        } else {
            log.info("Tasks are blocked for addtion");
            throw new ClosedTasksListException();
        }
    }

    public Task removeTask(int index) {
        Task task = tasks.remove(index);
        if (tasks.isEmpty()) {
            blockTaskAddition = true;
        }
        return task;
    }

    public void updateTask(int taskIndex, Task task) {
        tasks.set(taskIndex, task);
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

//    public void addTask(Task task) {
////        if (tasks==null) {
////            tasks=new ArrayList<Task>();
////        }
//        tasks.add(task);
//    }
}
