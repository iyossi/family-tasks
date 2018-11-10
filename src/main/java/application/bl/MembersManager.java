package application.bl;


import application.model.Family;
import application.repository.FamilyMemberRepository;
import application.repository.FamilyRepository;
import lombok.extern.java.Log;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class MembersManager {

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    //@PostConstruct
    public void doIt() {
        System.out.println("-- YOSSI doIt --");

        SessionFactory sessionFactory = new Configuration().configure()
                .buildSessionFactory();
        try {
            persist(sessionFactory);
            load(sessionFactory);

        } finally {
            sessionFactory.close();
        }
    }

    private void load(SessionFactory sessionFactory) {
        System.out.println("-- loading persons --");
        Session session = sessionFactory.openSession();
        @SuppressWarnings("unchecked")
        List<Family> families = session.createQuery("FROM family").list();
        families.forEach((x) -> System.out.printf("- %s%n", x));
        session.close();
    }

    private void persist(SessionFactory sessionFactory) {
        Family p1 = new Family("John");
        Family p2 = new Family("Tina");
        System.out.println("-- persisting persons --");
        System.out.printf("- %s%n- %s%n", p1, p2);

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        familyRepository.save(p1);
        familyRepository.save(p2);
//        session.save(p1);
//        session.save(p2);
        session.getTransaction().commit();
    }
}
