package com.userapp.console;

import com.userapp.entity.User;
import com.userapp.service.UserService;
import com.userapp.service.UserServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleInterface {
    private static final Logger logger = LogManager.getLogger(ConsoleInterface.class);
    private final UserService userService;
    private final Scanner scanner;

    public ConsoleInterface() {
        this.userService = new UserServiceImpl();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            printMenu();
            int choice = readIntInput("Enter your choice: ");

            try {
                switch (choice) {
                    case 1 -> createUser();
                    case 2 -> getUserById();
                    case 3 -> getAllUsers();
                    case 4 -> updateUser();
                    case 5 -> deleteUser();
                    case 6 -> {
                        System.out.println("Goodbye!");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice! Please try again.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                logger.error("Operation failed: {}", e.getMessage());
            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private void printMenu() {
        System.out.println("\n========== USER MANAGEMENT SYSTEM ==========");
        System.out.println("1. Create User");
        System.out.println("2. Find User by ID");
        System.out.println("3. List All Users");
        System.out.println("4. Update User");
        System.out.println("5. Delete User");
        System.out.println("6. Exit");
        System.out.println("===========================================");
    }

    private void createUser() {
        System.out.println("\n--- Create New User ---");
        String name = readStringInput("Enter name: ");
        String email = readStringInput("Enter email: ");
        Integer age = readIntInput("Enter age: ");

        User user = userService.createUser(name, email, age);
        System.out.println("User created successfully!");
        System.out.println(user);
    }

    private void getUserById() {
        System.out.println("\n--- Find User by ID ---");
        Long id = (long) readIntInput("Enter user ID: ");

        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            System.out.println("User found:");
            System.out.println(userOpt.get());
        } else {
            System.out.println("User with ID " + id + " not found.");
        }
    }

    private void getAllUsers() {
        System.out.println("\n--- All Users ---");
        List<User> users = userService.getAllUsers();

        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            System.out.printf("%-5s %-20s %-30s %-5s%n", "ID", "Name", "Email", "Age");
            System.out.println("------------------------------------------------------------");
            for (User user : users) {
                System.out.printf("%-5d %-20s %-30s %-5d%n",
                        user.getId(), user.getName(), user.getEmail(), user.getAge());
            }
        }
    }

    private void updateUser() {
        System.out.println("\n--- Update User ---");
        Long id = (long) readIntInput("Enter user ID to update: ");

        Optional<User> existingUser = userService.getUserById(id);
        if (existingUser.isEmpty()) {
            System.out.println("User not found!");
            return;
        }

        System.out.println("Current user details:");
        System.out.println(existingUser.get());

        String name = readStringInput("Enter new name (press Enter to keep current): ");
        String email = readStringInput("Enter new email (press Enter to keep current): ");
        Integer age = readIntInputOptional("Enter new age (or -1 to keep current): ");

        name = name.isEmpty() ? existingUser.get().getName() : name;
        email = email.isEmpty() ? existingUser.get().getEmail() : email;
        age = age == -1 ? existingUser.get().getAge() : age;

        User updated = userService.updateUser(id, name, email, age);
        System.out.println("User updated successfully!");
        System.out.println(updated);
    }

    private void deleteUser() {
        System.out.println("\n--- Delete User ---");
        Long id = (long) readIntInput("Enter user ID to delete: ");

        userService.deleteUser(id);
        System.out.println("User deleted successfully!");
    }

    private String readStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private Integer readIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number! Please enter a valid integer.");
            }
        }
    }

    private Integer readIntInputOptional(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}