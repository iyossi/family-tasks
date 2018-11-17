package application;

import application.bl.FamilyManager;
import application.bl.MembersManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement
@EnableJpaRepositories
@EnableAsync
@EnableRetry
public class FamilyTasksApplication implements CommandLineRunner {

    public static final int STATS_TIMEOUT_SECONDS = 20; //TODO move to prepreties files

    private Logger log = LoggerFactory.getLogger(FamilyTasksApplication.class);
    @Autowired
    private MembersManager membersManager;

    @Autowired
    private FamilyManager familyManager;

    public static void main(String[] args) {
        SpringApplication.run(FamilyTasksApplication.class, args);
    }

    @Override
    public void run(String... args) {
        membersManager.initialSetup();
        membersManager.getAllMembers().forEach(member -> {
            membersManager.memberActivity(member.getId(), member.getName());
        });

        //print stats after some time
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(STATS_TIMEOUT_SECONDS * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            familyManager.printStats();
        }
    }
}
