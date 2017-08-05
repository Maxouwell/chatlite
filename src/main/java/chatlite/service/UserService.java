package chatlite.service;

import chatlite.model.User;

public interface UserService {

	public void save(User user);

	public User findByUsername(String username);

}
