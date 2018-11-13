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
    private static final int MAX_MEMBERS_PER_FAMILY = 200;
    private static final int MAX_ITERATIONS = 5;
    private static final long SLEEP_TIME_BETWEEN_ACTIVITIES_MS = 50;//ms
    private Logger log = LoggerFactory.getLogger(MembersManager.class);

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private FamilyManager familyManager;

    public void initialSetup() {
        log.info("-- YOSSI initialSetup --");
        setupFamiliesAndMembers();
//        load();
    }

//    private void load() {
////        System.out.println("-- loading persons --");
//        log.info("Loading");
//
//        Iterable<Family> familiesItr = familyRepository.findAll();
//        for (Family family : familiesItr) {
//            log.info(family.toString());
//        }
//
//    }

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
        log.debug(families.toString());

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
        log.info(" list size is " + listSize);

        Random rand = new Random();
        int pickedNumber = rand.nextInt(listSize);
//        log.info("pickedNumber=" + pickedNumber);
//        UUID taskId = tasks.get(pickedNumber).getId();
//        log.debug("getRandomTask returns " + pickedNumber + " from list size " + listSize);
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
        Family family = familyRepository.getOne(familyMember.getFamily().getId());

        UUID familyId = family.getId();
        int pickedNumber = getRandomTaskIndex(familyMember);
        List<Task> tasks = family.getTasks(); // TODO: get parent with children in one fetch
        Task task = tasks.get(pickedNumber);
        task.setName(task.getName() + "_updated");
        familyManager.updateTask(familyId, pickedNumber, task);
        log.info("UPDATED " + task.getName());
    }

    private void deleteRandomTask(FamilyMember familyMember) throws NoTasksToChooseFrom {
        UUID familyId = familyMember.getFamily().getId();
        int pickedNumber = getRandomTaskIndex(familyMember);
        Task task = familyManager.removeTask(familyId, pickedNumber);
        log.info("DELETED " + task.getName());


    }

}
