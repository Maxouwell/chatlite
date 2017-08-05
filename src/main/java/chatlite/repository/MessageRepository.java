package chatlite.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import chatlite.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
    
}
