package application.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class ActivityLog {
    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private UUID id;

    private ActivityType activityType;

//    private
}
