package chatlite.service;

import java.util.List;

import org.springframework.data.domain.Page;

import chatlite.model.Message;

public interface MessageService {

	public void save(Message message);

	public List<Message> findAll();

	public Page<Message> findAll(int page);

}
