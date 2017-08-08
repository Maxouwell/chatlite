package chatlite.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import chatlite.bean.PagingInfo;
import chatlite.model.Message;
import chatlite.service.MessageService;

@Controller
public class ChatController {
	
	private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
	
	//TODO i18n
	private static final String ERROR_WHILE_INSERT = "Error while inserting value";
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private SimpMessagingTemplate messagingService;
	
	private Set<String> userList = new HashSet<>(); //TODO remplacer par User
	
    @RequestMapping(value = {"/", "/chat"}, method = RequestMethod.GET)
    public String chat(Model model) {
    	return "chat";
    }
    
    @MessageMapping("/message/send")
    @SendTo("/topic/messages/flow")
    public Message message(SimpMessageHeaderAccessor headerAccessor, Message message) throws Exception {
    	
    	message.setUsername(StringEscapeUtils.escapeHtml4(headerAccessor.getUser().getName()));
    	message.setContent(StringEscapeUtils.escapeHtml4(message.getContent()));
    	
    	messageService.save(message);
    	
    	return message;
    }
    
    @MessageMapping("/messages/askList")
    @SendToUser("/topic/messages/list")
    public Page<Message> messagesList(PagingInfo page) {
    	
    	return messageService.findAll(page.getPage());
    }
    
    @MessageMapping("/users/askList")
    @SendToUser("/topic/users/list")
    public List<String> usersList() {
    	return userList.stream().collect(Collectors.toList());
    }
    
    @MessageExceptionHandler
    @SendToUser("/topic/message/error")
    public Message error(Exception e) {
    	return new Message(ERROR_WHILE_INSERT);
    }
    
    @EventListener
    public void onSocketConnected(SessionConnectedEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String user = sha.getUser().getName(); 
        logger.info("[Connected] " + user);
        userList.add(user);
        
        messagingService.convertAndSend("/topic/users/list", userList.stream().collect(Collectors.toList()));
    }

    @EventListener
    public void onSocketDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String user = sha.getUser().getName(); 
        logger.info("[Disconnected] " + user);
        userList.remove(user);
        
        messagingService.convertAndSend("/topic/users/list", userList.stream().collect(Collectors.toList()));
    }
}