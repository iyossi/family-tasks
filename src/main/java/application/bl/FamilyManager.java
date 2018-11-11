package application.bl;


import application.model.Family;
import application.model.Task;
import application.repository.FamilyRepository;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class FamilyManager {

    private Logger log = LoggerFactory.getLogger(FamilyManager.class);

    @Autowired
    private FamilyRepository familyRepository;


    protected void addTask(UUID familyId, Task task) throws ObjectNotFoundException {
        if (task == null) {
            throw new NullPointerException("Nuul Task");
        }
        Optional<Family> familyOpt = familyRepository.findById(familyId);
        familyOpt.orElseThrow(() -> new ObjectNotFoundException(familyId, "Family"));
        Family family = familyOpt.get();
        family.addTask(task);
        familyRepository.save(family);
        log.info("Task " + task.getName() + " was added to family " + family.getName());
    }

    protected List<Task> getTasks(UUID familyId) {
        Optional<Family> familyOpt = familyRepository.findById(familyId);
        familyOpt.orElseThrow(() -> new ObjectNotFoundException(familyId, "Family"));
        Family family = familyOpt.get();
        return family.getTasks();
    }

    protected void removeTask(UUID familyId, int taskIndex) {
        Optional<Family> familyOpt = familyRepository.findById(familyId);
        familyOpt.orElseThrow(() -> new ObjectNotFoundException(familyId, "Family"));
        Family family = familyOpt.get();
        family.getTasks().remove(taskIndex);
        if (family.getTasks().isEmpty()) {
//       TODO     family.setTasks(null);
        }
        familyRepository.save(family);
        // TODO
        // to be implemented
    }

    protected void updateTask(UUID familyId, int taskIndex, Task task) {
        Optional<Family> familyOpt = familyRepository.findById(familyId);
        familyOpt.orElseThrow(() -> new ObjectNotFoundException(familyId, "Family"));
        Family family = familyOpt.get();
        List<Task> tasks = family.getTasks();
        if (tasks == null || taskIndex >= tasks.size()) {
            throw new ObjectNotFoundException(taskIndex, "Task");
        }
        tasks.set(taskIndex, task);
        familyRepository.save(family);
    }
}
