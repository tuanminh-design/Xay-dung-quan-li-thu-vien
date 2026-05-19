package com.thuvien.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:sqlserver://localhost:1433;"
                + "databaseName=QuanLyThuVien;"
                + "encrypt=true;trustServerCertificate=true";

        String user = "sa";
        String password = "tuanminh";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println(" Kết nối SQL Server thành công!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}