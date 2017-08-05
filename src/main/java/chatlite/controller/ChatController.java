package chatlite.controller;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import chatlite.model.Message;
import chatlite.model.PagingInfo;
import chatlite.service.MessageService;

@Controller
public class ChatController {
	@Autowired
	private MessageService messageService;
	
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
    
    @MessageExceptionHandler
    @SendToUser("/topic/message/error")
    public Message error(Exception e) {
    	return new Message("Error while inserting value");
    }
}