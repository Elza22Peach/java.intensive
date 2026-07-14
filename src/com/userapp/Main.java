package com.userapp;

import com.userapp.console.ConsoleInterface;
import com.userapp.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting User Service Application");

        // Add shutdown hook to close SessionFactory
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down...");
            HibernateUtil.shutdown();
        }));

        ConsoleInterface console = new ConsoleInterface();
        console.start();
    }
}