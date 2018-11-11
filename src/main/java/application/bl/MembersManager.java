package application.bl;


import application.model.Family;
import application.model.FamilyMember;
import application.repository.FamilyMemberRepository;
import application.repository.FamilyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class MembersManager {
    private static final int MAX_MEMBERS_PER_FAMILY = 2;
    private Logger log = LoggerFactory.getLogger(MembersManager.class);

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

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
            for (int index = 0; index <= MAX_MEMBERS_PER_FAMILY; index++) {
                FamilyMember member = new FamilyMember(family.getName() + "_member_" + index, (index + 1) * 3);
                family.getMembers().add(member);
            }
        });
        log.info("-- persisting persons --");
        log.debug(families.toString());
        familyRepository.saveAll(families);
    }
}
