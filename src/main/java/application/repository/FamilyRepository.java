package application.repository;

import application.model.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FamilyRepository extends JpaRepository<Family, UUID> {

    @Query("select size(u.tasks) from family u where u.id=:familyId")
    int findAllChildrenCount(@Param("familyId") UUID familyId);


    // @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select family from family family left join fetch family.tasks where family.id =:id")
    Family findOneWithTasksById(@Param("id") UUID id);
}

