package application;

import application.bl.MembersManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class FamilyTasksApplication implements CommandLineRunner {

    //	 private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(LogExample.class.getName());
//    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(FamilyTasksApplication.class);
//    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(FamilyTasksApplication.class.getName());
    private Logger log = LoggerFactory.getLogger(FamilyTasksApplication.class);
    @Autowired
    private MembersManager membersManager;

    public static void main(String[] args) {

        SpringApplication.run(FamilyTasksApplication.class, args);

    }




    @Override
    public void run(String... args) throws Exception {
        log.debug("----------------------Starting 2 ");
        membersManager.doIt();

        log.debug("----------------------Starting 3");
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
