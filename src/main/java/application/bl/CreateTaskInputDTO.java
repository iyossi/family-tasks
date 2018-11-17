package application.bl;

import java.util.UUID;

public class CreateTaskInputDTO {
    private UUID familyID;
    private String memberName;

    public CreateTaskInputDTO(UUID familyID, String memberName) {
        this.familyID = familyID;
        this.memberName = memberName;
    }

    public UUID getFamilyID() {
        return familyID;
    }

    public String getMemberName() {
        return memberName;
    }
}
