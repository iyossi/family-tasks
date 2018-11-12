package application.bl;


import application.Exceptions.ClosedTasksListException;
import application.model.Family;
import application.model.FamilyMember;
import application.model.Task;
import application.repository.FamilyMemberRepository;
import application.repository.FamilyRepository;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


@Service
@Transactional
public class MembersManager {
    private static final int MAX_MEMBERS_PER_FAMILY = 2;
    private Logger log = LoggerFactory.getLogger(MembersManager.class);

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private FamilyManager familyManager;

    public void initialSetup() {
        log.info("-- YOSSI initialSetup --");
        persist();
        load();
    }

    private void load() {
//        System.out.println("-- loading persons --");
        log.info("Loading");

        Iterable<Family> familiesItr = familyRepository.findAll();
        for (Family family : familiesItr) {
            log.info(family.toString());
        }

    }

    private void persist() {
        List<Family> families = new ArrayList<>();
        families.add(new Family("Cohen"));
        families.add(new Family("Levi"));
        families.add(new Family("Israel"));

        families.forEach(family -> {
            for (int index = 0; index < MAX_MEMBERS_PER_FAMILY; index++) {
                FamilyMember member = new FamilyMember(family.getName() + "_member_" + index, (index + 1) * 3);
                member.setFamily(family);
                family.getMembers().add(member);
            }
        });
        log.info("-- persisting persons --");
        log.debug(families.toString());
        familyRepository.saveAll(families);
    }

    public List<FamilyMember> getAllMembers() {
        return familyMemberRepository.findAll();
    }

    @Async
    public void memberActivity(FamilyMember familyMember) {
        log.info("Starting memberActivity for " + familyMember);

        try {
            createTask(familyMember);
            createTask(familyMember);
            updateRandomTask(familyMember);
            deleteRandomTask(familyMember);
            deleteRandomTask(familyMember);
            createTask(familyMember);
        } catch (Throwable e) {
            log.error(e.toString());
            e.printStackTrace();
        }

        Thread.currentThread().interrupt();

    }


    private int getRandomTaskIndex(FamilyMember familyMember) {
        UUID familyId = familyMember.getFamily().getId();
        List<Task> tasks = familyManager.getTasks(familyId);
        if (tasks == null) {
            log.debug("No tasks to delete");
            return -1;
        }
        int listSize = tasks.size();

        Random rand = new Random();
        int pickedNumber = rand.nextInt(listSize);
        log.info("pickedNumber=" + pickedNumber);
        UUID taskId = tasks.get(pickedNumber).getId();
        log.info("getRandomTask task id " + taskId.toString() + " in index " + pickedNumber + " from list size " + listSize);
        return pickedNumber;
    }

    @Transactional
    private void createTask(FamilyMember familyMember) {
        Task task = new Task("Task created by " + familyMember.getName() + " on " + (System.currentTimeMillis()));
        UUID familyId = familyMember.getFamily().getId();

        try {
            familyManager.addTask(familyId, task);
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            log.error(e.toString());
        } catch (ClosedTasksListException e) {
            log.info("can't add task '" + task.getName() + "' to " + familyId);
        }
    }


    @Transactional
    private void updateRandomTask(FamilyMember familyMember) {
        Family family = familyRepository.getOne(familyMember.getFamily().getId());
        log.info(family.toString());
        UUID familyId = family.getId();
        int pickedNumber = getRandomTaskIndex(familyMember);
        List<Task> tasks = family.getTasks();
        Task task = tasks.get(pickedNumber);
        task.setName(task.getName() + "_updated");
        familyManager.updateTask(familyId, pickedNumber, task);
    }

    @Transactional
    private void deleteRandomTask(FamilyMember familyMember) {
        UUID familyId = familyMember.getFamily().getId();
        int pickedNumber = getRandomTaskIndex(familyMember);
        familyManager.removeTask(familyId, pickedNumber);

    }

}
