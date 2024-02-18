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

@WebServlet(name = "RegisterStudent", value = "/registerStudent")
public class RegisterStudent extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        String name = req.getParameter("name");
        String studentClass = req.getParameter("class");
        int age;
        try {
            age = Integer.parseInt(req.getParameter("age"));
        } catch (NumberFormatException e) {
            resp.getWriter().println("Invalid age format");
            return;
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/classDb", "root", "");
             PreparedStatement ps = con.prepareStatement("INSERT INTO student (name, class, age) VALUES (?, ?, ?)")) {

            ps.setString(1, name);
            ps.setString(2, studentClass);
            ps.setInt(3, age);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student inserted successfully!");
                resp.sendRedirect("StudentList.jsp");
            } else {
                System.out.println("Failed to insert data.");
            }

        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
    }
}
