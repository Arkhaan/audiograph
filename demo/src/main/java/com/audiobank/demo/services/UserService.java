package com.audiobank.demo.services;

import java.util.List;

import com.audiobank.demo.models.User;

public interface UserService {

    String login(String login, String password);

    Boolean createAccount(String email, String firstName, String lastName);

    void logout(String apiKey);

    Long getUserId(String apiKey);

    User getUser(String apiKey);

    List<User> getAll();

    List<String> getConnectedUsers(String apiKey);

    List<String> getAllFullName();

    List<String> getUnclaimedUsers();

    String hashPassword(String plainTextPassword);
    boolean checkPass(String plainPassword, String hashedPassword);
}
