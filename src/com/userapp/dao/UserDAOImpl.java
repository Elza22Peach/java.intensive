package com.userapp.dao;

import com.userapp.entity.User;
import com.userapp.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {
    private static final Logger logger = LogManager.getLogger(UserDAOImpl.class);

    @Override
    public User create(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("User created successfully: {}", user.getEmail());
            return user;
        } catch (ConstraintViolationException e) {
            if (transaction != null) transaction.rollback();
            logger.error("Email already exists: {}", user.getEmail());
            throw new RuntimeException("User with email " + user.getEmail() + " already exists", e);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error creating user: {}", e.getMessage());
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Error finding user by id {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<User> query = session.createQuery("FROM User", User.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error finding all users: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public User update(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User merged = session.merge(user);
            transaction.commit();
            logger.info("User updated successfully: {}", user.getEmail());
            return merged;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error updating user: {}", e.getMessage());
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                transaction.commit();
                logger.info("User deleted successfully: id={}", id);
            } else {
                transaction.rollback();
                logger.warn("User not found for deletion: id={}", id);
                throw new RuntimeException("User with id " + id + " not found");
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error deleting user: {}", e.getMessage());
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Long> query = session.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
            query.setParameter("email", email);
            Long count = query.getSingleResult();
            return count > 0;
        } catch (Exception e) {
            logger.error("Error checking email existence: {}", e.getMessage());
            return false;
        }
    }
}