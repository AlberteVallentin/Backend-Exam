package dat.security.daos;

import dat.security.entities.User;
import dat.security.enums.RoleType;
import dat.security.exceptions.SecurityValidationException;
import dat.security.token.UserDTO;

public interface ISecurityDAO {
    UserDTO getVerifiedUser(String email, String password) throws SecurityValidationException;
    User createUser(String email, String name, String password, RoleType roleType) throws SecurityValidationException; // Ensure this method is present
    User addRole(UserDTO userDTO, String newRole) throws SecurityValidationException; // Ensure this method is present
}
