package service;

import entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.logging.Logger;

@Stateless
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    public User createUser(String username, String password) {
        // Проверяем, существует ли пользователь
        if (findByUsername(username) != null) {
            throw new IllegalArgumentException("User with username " + username + " already exists");
        }

        // Хешируем пароль
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        logger.info("Creating user: " + username);

        User user = new User(username, passwordHash);
        em.persist(user);
        em.flush(); // Гарантируем, что ID будет присвоен

        return user;
    }

    public User findByUsername(String username) {
        try {
            TypedQuery<User> query = em.createNamedQuery("User.findByUsername", User.class);
            query.setParameter("username", username);
            List<User> users = query.getResultList();
            return users.isEmpty() ? null : users.get(0);
        } catch (Exception e) {
            logger.warning("Error finding user by username: " + username + " - " + e.getMessage());
            return null;
        }
    }

    public User findById(Long id) {
        return em.find(User.class, id);
    }

    public boolean validateUser(String username, String password) {
        try {
            User user = findByUsername(username);
            if (user == null) {
                logger.warning("User not found: " + username);
                return false;
            }

            boolean isValid = BCrypt.checkpw(password, user.getPasswordHash());
            if (!isValid) {
                logger.warning("Invalid password for user: " + username);
            }

            return isValid;
        } catch (Exception e) {
            logger.severe("Error validating user: " + username + " - " + e.getMessage());
            return false;
        }
    }

    public List<User> getAllUsers() {
        return em.createNamedQuery("User.findAll", User.class).getResultList();
    }

    public void deleteUser(Long id) {
        User user = findById(id);
        if (user != null) {
            em.remove(user);
        }
    }
}