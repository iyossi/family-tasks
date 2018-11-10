package application;

import model.Family;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

//@Log4j2
@SpringBootApplication
public class FamilyTasksApplication implements CommandLineRunner {

    //	 private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(LogExample.class.getName());
    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(FamilyTasksApplication.class);

    public static void main(String[] args) {

        SpringApplication.run(FamilyTasksApplication.class, args);

    }

    private static void load(SessionFactory sessionFactory) {
        System.out.println("-- loading persons --");
        Session session = sessionFactory.openSession();
        @SuppressWarnings("unchecked")
        List<Family> families = session.createQuery("FROM family").list();
        families.forEach((x) -> System.out.printf("- %s%n", x));
        session.close();
    }

    private static void persist(SessionFactory sessionFactory) {
        Family p1 = new Family("John", 35);
        Family p2 = new Family("Tina", 30);
        System.out.println("-- persisting persons --");
        System.out.printf("- %s%n- %s%n", p1, p2);

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(p1);
        session.save(p2);
        session.getTransaction().commit();
    }


    @Override
    public void run(String... args) throws Exception {


        log.debug("Starting");
        SessionFactory sessionFactory = new Configuration().configure()
                .buildSessionFactory();
        try {
            persist(sessionFactory);
            load(sessionFactory);
            Thread.sleep(1000000);
        } finally {
            sessionFactory.close();
        }

    }
}
