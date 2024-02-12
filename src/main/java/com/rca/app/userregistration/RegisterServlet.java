package com.rca.app.userregistration;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet(name = "RegisterServlet", value = "/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // No implementation for now
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        String userName = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        // Hashing the password
        String hashedPassword = PasswordHasher.hashPassword(password);

        String role = req.getParameter("role");

        try {
            // Use try-with-resources to automatically close resources
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/javaUserDb";
            String userN = "root";
            String pass = "";

            try (Connection con = DriverManager.getConnection(url, userN, pass)) {
                String sql = "INSERT INTO users (userName, email, password, role) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, userName);
                    ps.setString(2, email);
                    ps.setString(3, hashedPassword);
                    ps.setString(4, role);

                    int rowsAffected = ps.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Data inserted successfully!");

                        // Redirect based on user role
                        if ("admin".equalsIgnoreCase(role)) {
                            // Redirect to admin.jsp
                            resp.sendRedirect("Admin.jsp");
                        } else if ("guest".equalsIgnoreCase(role)) {
                            // Redirect to guest.jsp
                            resp.sendRedirect("Guest.jsp");
                        }
                    } else {
                        System.out.println("Failed to insert data.");
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            // Log the exception or handle it appropriately
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }
}
