package application.bl;


import application.Exceptions.ClosedTasksListException;
import application.model.Family;
import application.model.Task;
import application.repository.FamilyRepository;
import application.repository.TaskRepository;
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

    @Autowired
    private TaskRepository taskRepository;


    protected void addTask(UUID familyId, Task task) throws ObjectNotFoundException, ClosedTasksListException {
        if (task == null) {
            throw new NullPointerException("Null Task");
        }
        Optional<Family> familyOpt = familyRepository.findById(familyId);
        familyOpt.orElseThrow(() -> new ObjectNotFoundException(familyId, "Family"));
        Family family = familyOpt.get();
        task.setFamily(family);
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
        family.removeTask(taskIndex);
        familyRepository.save(family);
    }

    protected void updateTask(UUID familyId, int taskIndex, Task task) {
        Optional<Family> familyOpt = familyRepository.findById(familyId);
        familyOpt.orElseThrow(() -> new ObjectNotFoundException(familyId, "Family"));
        Family family = familyOpt.get();
        family.updateTask(taskIndex, task);
        familyRepository.save(family);
    }
}
