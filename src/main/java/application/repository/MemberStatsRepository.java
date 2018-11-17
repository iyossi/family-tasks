package application.repository;


import application.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MemberStatsRepository extends JpaRepository<Task, UUID> {

//    @Query ("UPDATE created t set t.count = t.count + 1 WHERE t.id = :id;")
//    public void incrementCreated();
//
//    @Query ("UPDATE updated t set t.count = t.count + 1 WHERE t.id = :id;")
//    public void incrementUpdated();
//
//    @Query ("UPDATE deleted t set t.count = t.count + 1 WHERE t.id = :id;")
//    public void incrementdeleted();
}
