package chatlite.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import chatlite.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
