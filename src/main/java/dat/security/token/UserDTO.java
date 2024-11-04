package dat.security.token;

import dat.security.enums.RoleType;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Purpose: To hold information about a user
 * Author: Thomas Hartmann
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String name;
    private String email;
    private RoleType roleType;
    private String password;

    public UserDTO(String email, String password, RoleType roleType) {
        this.email = email;
        this.password = password;
        this.roleType = roleType;
    }

    public UserDTO(String email, RoleType roleType) {
        this.email = email;
        this.roleType = roleType;
    }

    public UserDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDTO userDTO)) return false;
        return Objects.equals(name, userDTO.name) && Objects.equals(email, userDTO.email) && roleType == userDTO.roleType && Objects.equals(password, userDTO.password);
    }



    @Override
    public int hashCode() {
        return Objects.hash(name, email, roleType, password);
    }
}
