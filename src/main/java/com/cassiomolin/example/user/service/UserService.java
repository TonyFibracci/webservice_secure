package com.cassiomolin.example.user.service;

import com.cassiomolin.example.security.domain.Authority;
import com.cassiomolin.example.security.service.PasswordEncoder;
import com.cassiomolin.example.user.domain.UserAccount;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.math.BigInteger;
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
    
    @Inject
    private PasswordEncoder encoder;

    /**
     * Find a user by username or email.
     *
     * @param identifier
     * @return
     */
    public UserAccount findByUsername(String identifier) {
        List<UserAccount> users = em.createQuery("SELECT u FROM UserAccount u WHERE u.username = :identifier", UserAccount.class)
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
    
    /**
     * Add new user with expired license
     * @param user
     */
    public void addUser(UserAccount user) {
    	user.setExpiringDate(System.currentTimeMillis());
    	em.getTransaction().begin();
    	long previousId = (long) em.createQuery("SELECT MAX(id) FROM UserAccount").getResultList().get(0);
    	user.setId((long) (previousId + 1));
    	em.createNativeQuery("INSERT INTO UserAccount (id, username, password, expiringDate) VALUES(:id, :username, :password, :expiring)")
    	.setParameter("id", user.getId())
    	.setParameter("expiring", user.getExpiringDate())
    	.setParameter("username", user.getUsername())
    	.setParameter("password", encoder.hashPassword(user.getPassword()))
    	.executeUpdate();
    	for(Authority a : user.getAuthorities()) {
    		em.createNativeQuery("INSERT INTO useraccount_authorities (UserAccount_id, authority) VALUES(:id, :role)")
    		.setParameter("id", user.getId())
    		.setParameter("role", a.toString())
    		.executeUpdate();
    	}
    	em.getTransaction().commit();
    }
    
    /**
     * increase license for one additional month
     * @param identifier
     */
    public void extendLicenseMonth(String identifier, int months) {
       	em.getTransaction().begin();
       	long newExpiringDate = System.currentTimeMillis();
       	BigInteger previousExpiringDateBigInteger = (BigInteger) em.createNativeQuery("SELECT expiringDate FROM UserAccount WHERE username = :identifier").setParameter("identifier", identifier).getResultList().get(0);
       	long previousExpiringDate = previousExpiringDateBigInteger.longValue();
       	if(previousExpiringDate > newExpiringDate)
        	newExpiringDate = previousExpiringDate;
        newExpiringDate += (2629746000L * months);
        em.createNativeQuery("UPDATE UserAccount SET expiringDate = :date WHERE username = :identifier")
        .setParameter("date", newExpiringDate)
        .setParameter("identifier", identifier)
        .executeUpdate();
        em.getTransaction().commit();
    }
    
    /**
     * increase license for one additional week
     * @param identifier
     */
    public void extendLicenseWeek(String identifier, int weeks) {
       	em.getTransaction().begin();
       	long newExpiringDate = System.currentTimeMillis();
       	BigInteger previousExpiringDateBigInteger = (BigInteger) em.createQuery("SELECT expiringDate FROM UserAccount WHERE username = :identifier").setParameter("identifier", identifier).getResultList().get(0);
        long previousExpiringDate = previousExpiringDateBigInteger.longValue();
       	if(previousExpiringDate > newExpiringDate)
        	newExpiringDate = previousExpiringDate;
        newExpiringDate += (604800000L * weeks);
        em.createNativeQuery("UPDATE UserAccount SET expiringDate = :date WHERE username = :identifier")
        .setParameter("date", newExpiringDate)
        .setParameter("identifier", identifier)
        .executeUpdate();
        em.getTransaction().commit();
    }
    
    public void resetLicense(String identifier) {
    	em.getTransaction().begin();
        em.createNativeQuery("UPDATE UserAccount SET expiringDate = 0 WHERE username = :identifier")
        .setParameter("identifier", identifier)
        .executeUpdate();
        em.getTransaction().commit();
    }
}
