package chatlite.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "message")
public class Message {

	private Long id;
    private LocalDateTime date;
	private String username;
	private String channel;
	private String content;
	
	public Message() {
		this.date = LocalDateTime.now();
	}

	public Message(String content) {
		this.content = content;
		this.date = LocalDateTime.now();
	}

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Lob
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	public String getChannel() {
		return channel;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public LocalDateTime getDate() {
		return date;
	}
	
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
}
