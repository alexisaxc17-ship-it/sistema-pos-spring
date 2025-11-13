package com.example.posapp;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("Admin: " + encoder.encode("admin123"));
        System.out.println("Empleado: " + encoder.encode("empleado123"));
    }
}

