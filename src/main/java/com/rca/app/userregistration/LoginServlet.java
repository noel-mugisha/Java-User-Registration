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
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // No implementation for now
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        // Hashed Password
        String hashedPassword = PasswordHasher.hashPassword(password);

        try {
            // Use try-with-resources to automatically close resources
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/javaUserDb";
            String userN = "root";
            String pass = "";

            try (Connection con = DriverManager.getConnection(url, userN, pass)) {
                String sql = "SELECT * FROM users WHERE email=? AND password=?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, email);
                    ps.setString(2, hashedPassword);

                    try (ResultSet resultSet = ps.executeQuery()) {
                        if (resultSet.next()) {
                            String userRole = resultSet.getString("role");
                            if ("admin".equalsIgnoreCase(userRole)) {
                                resp.sendRedirect("Admin.jsp");
                                System.out.println("User logged in");
                            } else {
                                resp.sendRedirect("Guest.jsp");
                                System.out.println("User logged in");
                            }
                        } else {
                            System.out.println("User not registered or incorrect credentials");
                        }
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
