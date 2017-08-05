package chatlite.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import chatlite.model.Message;
import chatlite.repository.MessageRepository;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageRepository messageRepository;
    
    @Override
    public void save(Message message) {
    	messageRepository.save(message);
    }

    @Override
    public List<Message> findAll() {
    	return messageRepository.findAll(new Sort(Direction.ASC, "id"));
    }
    
    @Override
    public Page<Message> findAll(int page) {
    	
    	PageRequest pageReq = new PageRequest(page, 30, Direction.DESC, "id");
    	
    	return messageRepository.findAll(pageReq); 
    }
}
