package dat.daos.impl;

import dat.config.HibernateConfig;
import dat.daos.IDAO;
import dat.dtos.GuideDTO;
import dat.entities.Guide;
import dat.exceptions.ApiException;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class GuideDAO implements IDAO<GuideDTO, Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuideDAO.class);
    private static GuideDAO instance;
    private static EntityManagerFactory emf;

    public static GuideDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new GuideDAO();
        }
        return instance;
    }

    @Override
    public GuideDTO getById(Integer id) throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            Guide guide = em.find(Guide.class, id);
            if (guide == null) {
                throw new ApiException(404, "Guide not found with id: " + id);
            }
            LOGGER.info("Retrieved guide with id: {}", id);
            return new GuideDTO(guide);
        } catch (PersistenceException e) {
            LOGGER.error("Database error during read operation: {}", e.getMessage());
            throw new ApiException(500, "Database error occurred during read operation");
        }
    }

    @Override
    public List<GuideDTO> getAll() throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Guide> query = em.createQuery("SELECT g FROM Guide g", Guide.class);
            List<GuideDTO> guides = query.getResultList().stream()
                .map(GuideDTO::new)
                .collect(Collectors.toList());

            if (guides.isEmpty()) {
                throw new ApiException(404, "No guides found in the database");
            }

            LOGGER.info("Retrieved {} guides", guides.size());
            return guides;
        } catch (PersistenceException e) {
            LOGGER.error("Database error during readAll operation: {}", e.getMessage());
            throw new ApiException(500, "Database error occurred during readAll operation");
        }
    }


    public GuideDTO create(GuideDTO guideDTO) throws ApiException {
        throw new ApiException(501, "Create operation is not supported for Guide");
    }

    @Override
    public GuideDTO update(Integer id, GuideDTO guideDTO) throws ApiException {
        throw new ApiException(501, "Update operation is not supported for Guide");
    }

    @Override
    public void delete(Integer id) throws ApiException {
        throw new ApiException(501, "Delete operation is not supported for Guide");
    }





//    @Override
//    public GuideDTO create(GuideDTO guideDTO) throws ApiException {
//        try (EntityManager em = emf.createEntityManager()) {
//            em.getTransaction().begin();
//            Guide guide = new Guide();
//            updateGuideFields(guide, guideDTO);
//            em.persist(guide);
//            em.getTransaction().commit();
//
//            LOGGER.info("Created guide with id: {}", guide.getId());
//            return new GuideDTO(guide);
//        } catch (PersistenceException e) {
//            LOGGER.error("Database error during create operation: {}", e.getMessage());
//            throw new ApiException(500, "Database error occurred during create operation");
//        }
//    }
//
//    @Override
//    public GuideDTO update(Integer id, GuideDTO guideDTO) throws ApiException {
//        try (EntityManager em = emf.createEntityManager()) {
//            em.getTransaction().begin();
//            Guide guide = em.find(Guide.class, id);
//            if (guide == null) {
//                throw new ApiException(404, "Guide not found with id: " + id);
//            }
//
//            updateGuideFields(guide, guideDTO);
//            Guide updatedGuide = em.merge(guide);
//            em.getTransaction().commit();
//
//            LOGGER.info("Updated guide with id: {}", id);
//            return new GuideDTO(updatedGuide);
//        } catch (PersistenceException e) {
//            LOGGER.error("Database error during update operation: {}", e.getMessage());
//            throw new ApiException(500, "Database error occurred during update operation");
//        }
//    }
//
//    @Override
//    public void delete(Integer id) throws ApiException {
//        try (EntityManager em = emf.createEntityManager()) {
//            em.getTransaction().begin();
//            Guide guide = em.find(Guide.class, id);
//            if (guide == null) {
//                throw new ApiException(404, "Guide not found with id: " + id);
//            }
//            em.remove(guide);
//            em.getTransaction().commit();
//            LOGGER.info("Deleted guide with id: {}", id);
//        } catch (PersistenceException e) {
//            LOGGER.error("Database error during delete operation: {}", e.getMessage());
//            throw new ApiException(500, "Database error occurred during delete operation");
//        }
//    }


//    private void updateGuideFields(Guide guide, GuideDTO guideDTO) {
//        guide.setFirstName(guideDTO.getFirstName());
//        guide.setLastName(guideDTO.getLastName());
//        guide.setEmail(guideDTO.getEmail());
//        guide.setPhone(guideDTO.getPhone());
//        guide.setYearsOfExperience(guideDTO.getYearsOfExperience());
//    }
}