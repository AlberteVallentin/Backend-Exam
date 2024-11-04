package dat.security.daos;

import dat.security.entities.Role;
import dat.security.entities.User;
import dat.security.enums.RoleType;
import dat.security.exceptions.SecurityValidationException;
import dat.security.token.UserDTO;
import io.javalin.http.UnauthorizedResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

public class SecurityDAO implements ISecurityDAO {

    private static EntityManagerFactory emf;

    public SecurityDAO(EntityManagerFactory _emf) {
        emf = _emf;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // Method to get a verified user by email and password
    @Override
    public UserDTO getVerifiedUser(String email, String password) throws SecurityValidationException {
        try (EntityManager em = getEntityManager()) {
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();

            // Verify the password
            if (!user.verifyPassword(password)) {
                throw new UnauthorizedResponse("Invalid email or password.");
            }

            return new UserDTO(user.getEmail(), user.getRole().getRoleType());
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("No user found with the provided email.");
        } catch (Exception e) {
            throw new SecurityValidationException("An error occurred while verifying the user: " + e.getMessage());
        }
    }



    @Override
    public User createUser(String name, String email, String password, RoleType roleType) throws SecurityValidationException {
        if (roleType == null) {
            throw new SecurityValidationException("Role must be provided when creating a user");
        }

        if (roleType == RoleType.ADMIN) {
            throw new SecurityValidationException("Cannot assign ADMIN role during registration");
        }

        try (EntityManager em = getEntityManager()) {
            User existingUser = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst()
                .orElse(null);

            if (existingUser != null) {
                throw new EntityExistsException("User with email: " + email + " already exists");
            }

            Role role = em.createQuery("SELECT r FROM Role r WHERE r.roleType = :roleType", Role.class)
                .setParameter("roleType", roleType)
                .getSingleResult();

            if (role == null) {
                throw new EntityNotFoundException("Role not found: " + roleType);
            }

            User userEntity = new User(name, email, password, role);
            em.getTransaction().begin();
            em.persist(userEntity);
            em.getTransaction().commit();
            return userEntity;
        } catch (EntityExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new SecurityValidationException("An error occurred while creating the user: " + e.getMessage());
        }
    }


    @Override
    public User addRole(UserDTO userDTO, String newRole) throws SecurityValidationException {
        if (userDTO == null || userDTO.getEmail() == null) {
            throw new SecurityValidationException("UserDTO or email must not be null.");
        }

        RoleType roleType;
        try {
            roleType = RoleType.valueOf(newRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new SecurityValidationException("Invalid role: " + newRole);
        }

        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();

            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", userDTO.getEmail())
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userDTO.getEmail()));

            Role role = em.createQuery("SELECT r FROM Role r WHERE r.roleType = :roleType", Role.class)
                .setParameter("roleType", roleType)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + newRole));

            user.setRole(role);
            em.merge(user);

            em.getTransaction().commit();

            return user;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new SecurityValidationException("An error occurred while adding role: " + e.getMessage());
        }
    }

}