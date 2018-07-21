package com.cassiomolin.example.user.service;

import com.cassiomolin.example.user.domain.UserAccount;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * Service that provides operations for {@link UserAccount}s.
 *
 * @author cassiomolin
 */
@ApplicationScoped
public class UserService {

    @Inject
    private EntityManager em;

    /**
     * Find a user by username or email.
     *
     * @param identifier
     * @return
     */
    public UserAccount findByUsernameOrEmail(String identifier) {
        List<UserAccount> users = em.createQuery("SELECT u FROM UserAccount u WHERE u.username = :identifier OR u.email = :identifier", UserAccount.class)
                .setParameter("identifier", identifier)
                .setMaxResults(1)
                .getResultList();
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    /**
     * Find all users.
     *
     * @return
     */
    public List<UserAccount> findAll() {
        return em.createQuery("SELECT u FROM UserAccount u", UserAccount.class).getResultList();
    }

    /**
     * Find a user by id.
     *
     * @param userId
     * @return
     */
    public Optional<UserAccount> findById(Long userId) {
        return Optional.ofNullable(em.find(UserAccount.class, userId));
    }
}
