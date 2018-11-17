package application.bl;


import application.Exceptions.ClosedTasksListException;
import application.Exceptions.EmptyListUpdateException;
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
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


@Service
//@Transactional
public class MembersManager {
    //TODO move to prepreties file
    private static final int MAX_MEMBERS_PER_FAMILY = 13;
    private static final int MAX_ITERATIONS = 10;// for now. it should be infinite according to the requirements ?
    private static final long SLEEP_TIME_BETWEEN_ACTIVITIES_MS = 50;//ms

    private Logger log = LoggerFactory.getLogger(MembersManager.class);

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private FamilyManager familyManager;

    @Autowired
    private TransactionHelper helper;

    public void initialSetup() {
        setupFamiliesAndMembers();
    }

    @Transactional
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
//                MemberStats memberStats=new MemberStats(); //TODO should be in a separate trx since we need the memberId
            }
        });
        log.info("Families and members were added");
        familyRepository.saveAll(families);
    }

    @Transactional
    public List<FamilyMember> getAllMembers() {
        return familyMemberRepository.findAll();
    }

    @Async
    public void memberActivity(UUID memberId, String memberName) {
        Thread.currentThread().setName(memberName);
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


    public void runOneOperation(UUID memberId) throws NoTasksToChooseFrom {
        Random rand = new Random();
        int pickedNumber = rand.nextInt(eOperation.values().length);

        try {
            String familyMemberName = "KKKK";
//        UUID familyId;
            FamilyMember familyMember2 = null;
//        familyMember2 =
            CreateTaskInputDTO createTaskInputDTO = helper.withTransactionRO(() -> {
                FamilyMember member = familyMemberRepository.getOne(memberId);
                return new CreateTaskInputDTO(member.getFamily().getId(), member.getName());
            });
            final FamilyMember familyMember = familyMember2;

//        FamilyMember familyMember = familyMemberRepository.getOne(memberId);
//        Thread.currentThread().setName(familyMember.getName() + "_thread"); // for better logging
//        pickedNumber=0;
            switch (pickedNumber) {
                case 0:
                case 1:
//                helper.withTransaction(() ->createTask(createTaskInputDTO));
                    createTask(createTaskInputDTO);
                    break;
                case 2:
                    updateRandomTask(createTaskInputDTO.getFamilyID());
                    break;
                case 3:
                    deleteRandomTask(createTaskInputDTO.getFamilyID());
                    break;
                default:
                    log.error("Invalid operation=" + pickedNumber);
                    break;
            }
        } catch (EmptyListUpdateException e) {

        }
    }


    private int getRandomTaskIndex(UUID familyId) throws NoTasksToChooseFrom {
        int listSize = familyManager.getTasksCount(familyId);
        log.debug(" list size is " + listSize);

        Random rand = new Random();
        int pickedNumber = rand.nextInt(listSize);
        return pickedNumber;
    }

    //    @Transactional
    public void createTask(CreateTaskInputDTO createTaskInputDTO) {

        Task task = new Task("Task " + createTaskInputDTO.getMemberName() + "_" + (System.currentTimeMillis()));

        try {
            helper.withTransaction(() -> {
                try {
                    familyManager.addTask(createTaskInputDTO.getFamilyID(), task);
                } catch (ClosedTasksListException e) {
                    e.printStackTrace();
                }
            });
            log.info("ADDED " + task.getName());
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            log.error(e.toString());
        }
//        catch (ClosedTasksListException e) {
//            log.info("REJECTED " + task.getName());
//        }
    }

    @Retryable(value = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
            maxAttempts = 3)
    public void updateRandomTask(UUID familyId) throws NoTasksToChooseFrom, EmptyListUpdateException {
        //TODO use lock (SELECT FOR UPDATE ?)
//        Task task = null;
        int pickedNumber2 = -2;
        boolean retry = false;

        helper.withTransaction(() -> {
            try {
                helper.withTransaction(() -> {
                    Family family = familyRepository.findOneWithTasksById(familyId);

                    int pickedNumber = 0;
                    try {
                        pickedNumber = getRandomTaskIndex(familyId);
                    } catch (NoTasksToChooseFrom noTasksToChooseFrom) {
                        noTasksToChooseFrom.printStackTrace();
                    }
                    List<Task> tasks = family.getTasks();
                    log.info("got tasks for " + pickedNumber + ", size=" + tasks.size());
//                    pickedNumber2 = pickedNumber;
                    Task task = tasks.get(pickedNumber);
                    task.setName(task.getName() + "_updated");
                    log.info("Going to update task " + pickedNumber);
                    familyManager.updateTask(familyId, pickedNumber, task);
                    log.info("UPDATED " + task.getName());
                });
            } catch (IndexOutOfBoundsException e) {
                // the list was modified in the middle
                log.warn("CONFLICT updating task with index " + pickedNumber2);
                throw e;
            } catch (IllegalArgumentException e) {
                //  the list was empty
                // TODO better analyze the reason
                log.warn("List is empty. Try to update it later ");
                throw new EmptyListUpdateException();
            } catch (PessimisticLockingFailureException e) {
                log.error("UPDATE failure updating task with index " + pickedNumber2 + ", possibly: locking timeout");
                throw e;
            } catch (OptimisticLockException e) {
                log.warn("Retry due to optimistic lock in update");
                throw e;
            } catch (Exception e) {
                log.error("unknown error");
                e.printStackTrace();
                throw e;
            }
        });
    }

    public void deleteRandomTask(UUID familyId) throws NoTasksToChooseFrom {
        //TODO use lock (SELECT FOR UPDATE ?)
        int pickedNumber = -1;
        try {
            pickedNumber = getRandomTaskIndex(familyId);
            Task task = familyManager.removeTask(familyId, pickedNumber);
            log.info("DELETED " + task.getName());
        } catch (PessimisticLockingFailureException e) {
            log.error("UPDATE failure deleting  task index " + pickedNumber + ", possibly: locking timeout");
        }
    }
}
