package com.audiobank.demo.services;

import com.audiobank.demo.models.User;
import com.audiobank.demo.repositories.UserRepository;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String login(String email, String password) {
        // 1 - check if user in database
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            if (!checkPass(password, optionalUser.get().getPassword())) {
                return null;
            }
            // 2 - Create generate and save api key
            String apiKey = generateApiKey();
            User user = optionalUser.get();
            user.setApiKey(apiKey);
            userRepository.save(user);
            return apiKey;
        }
        return null;

    }

    @Override
    public Boolean createAccount(String email, String firstName, String lastName) {
        // 1 - check if user already exists
        if ( userRepository.emailExists(email) || userRepository.isRegistered(firstName, lastName) ) {
            return false;
        }
        // 2 - Create new user
        String plainPassword = java.util.UUID.randomUUID().toString().split("-")[0];
        String hashedPassword = hashPassword(plainPassword);
        userRepository.save(new User(email, hashedPassword, firstName, lastName));
        // 3 - Send email to reset password
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(email);
            helper.setSubject("Bienvenue chez Audiograph!");
            helper.setText("Voici votre mot de passe : " + plainPassword + "\nNous vous recommendez de le changer dès que possible.");
        };
        javaMailSender.send(messagePreparator);
        return true;
    }

    @Override
    public void logout(String apiKey) {
        // TODO : make boolean and say if logout was successful or not
        Optional<User> optionalUser = userRepository.findByApiKey(apiKey);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setApiKey(null);
            userRepository.save(user);
        }
    }

    @Override
    public Long getUserId(String apiKey) {
        Optional<User> optionalUser = userRepository.findByApiKey(apiKey);
        return optionalUser.map(User::getId).orElse(null);
    }

    @Override
    public User getUser(String apiKey) {
        return userRepository.findByApiKey(apiKey).get();
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public List<String> getAllFullName() {
        List<String> names = userRepository.findAll().stream().map(user ->user.getFullName())
                                            .collect(Collectors.toList());
        return names;
    }

    @Override
    public List<String> getUnclaimedUsers() {
        List<User> users = userRepository.findUnclaimedNames();
        List<String> fullNames = users.stream().map(user ->user.getFullName())
                                        .collect(Collectors.toList());
        return fullNames;
    }

    public String generateApiKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] values = new byte[24];
        secureRandom.nextBytes(values);
        return values.toString();
    }

    public String hashPassword(String plainTextPassword){
		return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
	}

    public boolean checkPass(String plainPassword, String hashedPassword) {
		return BCrypt.checkpw(plainPassword, hashedPassword);
	}
}
