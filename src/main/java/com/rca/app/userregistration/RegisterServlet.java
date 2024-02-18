package com.rca.app.userregistration;

import jakarta.servlet.RequestDispatcher;
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
        String role = req.getParameter("role");

        // Validate email and password using RegexValidator
        boolean isValidEmail = RegexValidator.isValidEmail(email);
        boolean isValidPassword = RegexValidator.isValidPassword(password);

        if (!isValidEmail || !isValidPassword) {
            // Redirect to an error page or display an error message
            resp.getWriter().println("Invalid email or password format.");
            return;
        }

        // Hashing the password
        String hashedPassword = PasswordHasher.hashPassword(password);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/javaUserDb";
            String userN = "root";
            String pass = "";

            try (Connection con = DriverManager.getConnection(url, userN, pass);
                 PreparedStatement ps = con.prepareStatement("INSERT INTO users (userName, email, password, role) VALUES (?,?,?,?)")) {

                ps.setString(1, userName);
                ps.setString(2, email);
                ps.setString(3, hashedPassword);
                ps.setString(4, role);

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Data inserted successfully!");

                    // Redirect based on user role
                    if ("admin".equalsIgnoreCase(role)) {
                        // Forward to admin.jsp
                        RequestDispatcher dispatcher = req.getRequestDispatcher("Admin.jsp");
                        dispatcher.forward(req, resp);
                    } else if ("guest".equalsIgnoreCase(role)) {
                        // Forward to guest.jsp
                        RequestDispatcher dispatcher = req.getRequestDispatcher("Guest.jsp");
                        dispatcher.forward(req, resp);
                    }

                } else {
                    System.out.println("Failed to insert data.");
                }

            } catch (SQLException e) {
                // Log or handle the SQL exception appropriately
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
            }

        } catch (ClassNotFoundException e) {
            // Log or handle the ClassNotFoundException appropriately
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }
}
