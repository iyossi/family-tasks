package application.repository;

import application.model.FamilyMember;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FamilyMemberRepository extends CrudRepository<FamilyMember, UUID> {
}

