package application.bl;


import application.Exceptions.ClosedTasksListException;
import application.Exceptions.NoTasksToChooseFrom;
import application.model.Family;
import application.model.FamilyMember;
import application.model.Task;
import application.repository.FamilyMemberRepository;
import application.repository.FamilyRepository;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


@Service
@Transactional
public class MembersManager {
    //TODO move to prepreties file
    private static final int MAX_MEMBERS_PER_FAMILY = 200;
    private static final int MAX_ITERATIONS = 50;// for now. it should be infinite according to the requirements ?
    private static final long SLEEP_TIME_BETWEEN_ACTIVITIES_MS = 50;//ms

    private Logger log = LoggerFactory.getLogger(MembersManager.class);

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private FamilyManager familyManager;

    public void initialSetup() {
        setupFamiliesAndMembers();
    }

    private void setupFamiliesAndMembers() {
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
        log.info("Families and members were added");
        familyRepository.saveAll(families);
    }

    public List<FamilyMember> getAllMembers() {
        return familyMemberRepository.findAll();
    }

    @Async
    public void memberActivity(UUID memberId) {
        log.info("Starting memberActivity for " + memberId);
        for (int counter = 0; counter < MAX_ITERATIONS; counter++) {
            try {
                runOneOperation(memberId);
            } catch (NoTasksToChooseFrom e) {
                log.info("we tried to update or delete, but the list is empty");
            } catch (EntityNotFoundException e) {
                log.error("Invalid member Id " + memberId);
                break; // no point to continue
            } catch (Throwable e) {
                log.error(e.toString());
                e.printStackTrace();
            }
            try {
                Thread.sleep(SLEEP_TIME_BETWEEN_ACTIVITIES_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        Thread.currentThread().interrupt();

    }

    private void runOneOperation(UUID memberId) throws NoTasksToChooseFrom {
        Random rand = new Random();
        int pickedNumber = rand.nextInt(eOperation.values().length);
        FamilyMember familyMember = familyMemberRepository.getOne(memberId);
        Thread.currentThread().setName(familyMember.getName() + "_thread"); // for better logging
        switch (pickedNumber) {
            case 0:
            case 1:
                createTask(familyMember);
                break;
            case 2:
                updateRandomTask(familyMember);
                break;
            case 3:
                deleteRandomTask(familyMember);
                break;
            default:
                log.error("Invalid operation=" + pickedNumber);
                break;
        }
    }


    private int getRandomTaskIndex(FamilyMember familyMember) throws NoTasksToChooseFrom {
        UUID familyId = familyMember.getFamily().getId();
        int listSize = familyManager.getTasksCount(familyId);
        log.debug(" list size is " + listSize);

        Random rand = new Random();
        int pickedNumber = rand.nextInt(listSize);
        return pickedNumber;
    }

    private void createTask(FamilyMember familyMember) {
        Task task = new Task("Task " + familyMember.getName() + "_" + (System.currentTimeMillis()));
        Family family = familyMember.getFamily();
        UUID familyId = family.getId();

        try {
            familyManager.addTask(familyId, task);
            log.info("ADDED " + task.getName());
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            log.error(e.toString());
        } catch (ClosedTasksListException e) {
            log.info("REJECTED " + task.getName());
        }
    }


    private void updateRandomTask(FamilyMember familyMember) throws NoTasksToChooseFrom {
        //TODO use lock (SELECT FOR UPDATE ?)
        Task task = null;
        int pickedNumber = -1;
        try {
            Family family = familyRepository.findOneWithTasksById(familyMember.getFamily().getId());
            UUID familyId = family.getId();

            pickedNumber = getRandomTaskIndex(familyMember);
            List<Task> tasks = family.getTasks();

            task = tasks.get(pickedNumber);
            task.setName(task.getName() + "_updated");
            familyManager.updateTask(familyId, pickedNumber, task);
            log.info("UPDATED " + task.getName());
        } catch (IndexOutOfBoundsException e) {
            // the list was modified in the middle
            log.warn("CONFLICT updating task with index " + pickedNumber);
        } catch (IllegalArgumentException e) {
            //  the list was empty (or there are no tasks, or updated by another thread at the same time ?)
            // TODO better analyze the reason
            log.debug("Possible conflict trying to update task with index=" + pickedNumber);
        } catch (PessimisticLockingFailureException e) {
            log.error("UPDATE failure updating task with index " + pickedNumber + ", possibly: locking timeout");
        }
    }

    private void deleteRandomTask(FamilyMember familyMember) throws NoTasksToChooseFrom {
        //TODO use lock (SELECT FOR UPDATE ?)
        UUID familyId = familyMember.getFamily().getId();
        int pickedNumber = -1;
        try {
            pickedNumber = getRandomTaskIndex(familyMember);
            Task task = familyManager.removeTask(familyId, pickedNumber);
            log.info("DELETED " + task.getName());
        } catch (PessimisticLockingFailureException e) {
            log.error("UPDATE failure deleting  task index " + pickedNumber + ", possibly: locking timeout");
        }
    }
}
