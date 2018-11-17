package application.model;

import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import java.util.UUID;

public class MemberStats {
    @Id
    private UUID familyMemberId;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private FamilyMember familyMember;
    private int created;
    private int updated;
    private int deleted;

    public MemberStats(UUID familyMemberId) {
        this.familyMemberId = familyMemberId;
    }
}
