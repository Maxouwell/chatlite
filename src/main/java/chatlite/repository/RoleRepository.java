package chatlite.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import chatlite.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
}
