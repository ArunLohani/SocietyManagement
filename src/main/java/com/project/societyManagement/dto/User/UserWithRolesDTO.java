
package com.project.societyManagement.dto.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWithRolesDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private List<Long> assignedRoleIds;
    private List<String> assignedRoleNames;
}