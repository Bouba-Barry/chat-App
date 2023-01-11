package chatUdp.gui;

import chatUdp.app.ChatClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class ChatUI extends JFrame {
    private JLabel label, usernameLabel, passwordLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton, chatButton, seeUsersButton, backButton;
    private ChatClient chatClient;
    private boolean isLoggedIn;

    public ChatUI(ChatClient chatClient) {
        this.chatClient = chatClient;

        label = new JLabel("Welcome to the Chat App!");
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        chatButton = new JButton("Enter Chat");
        seeUsersButton = new JButton("See Users");
        backButton = new JButton("Back");
        // set the chat and see user buttons not visible
        chatButton.setVisible(false);
        seeUsersButton.setVisible(false);
        backButton.setVisible(false);

        // Add buttons and text fields to a panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 2));
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(chatButton);
        buttonPanel.add(seeUsersButton);
        buttonPanel.add(backButton);
        // Add an ActionListener to the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();
                String passwordString = new String(password);

            }
        });

        // Add an ActionListener to the register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();
                String passwordString = new String(password);

            }
        });

        // Add an ActionListener to the chat button
        chatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        // Add an ActionListener to the see users button
        seeUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               // chatClient.seeUsers();
            }
        });

        // Add an ActionListener to the back button
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isLoggedIn = false;
                showMenu();
            }
        });

        // Add the components to the frame
        add(label, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        setTitle("Chat Application - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void showMenu(){
        // show or hide the buttons based on the isLoggedIn value
        loginButton.setVisible(!isLoggedIn);
        registerButton.setVisible(!isLoggedIn);
        chatButton.setVisible(isLoggedIn);
        seeUsersButton.setVisible(isLoggedIn);
        backButton.setVisible(isLoggedIn);
    }

}
