package com.hethong.baotri.dieu_khien.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Class để test kết nối SQL Server trước khi chạy ứng dụng
 * Chạy class này để kiểm tra kết nối database
 */
public class TestSQLServerConnection {

    // Thông tin kết nối từ ảnh
    private static final String URL = "jdbc:sqlserver://lov:1433;databaseName=BaoTriThietBi;trustServerCertificate=true;encrypt=false";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "123456";

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("🔍 KIỂM TRA KẾT NỐI SQL SERVER");
        System.out.println("=================================================================");

        try {
            // Load SQL Server JDBC driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("✅ Đã load driver SQL Server thành công");

            // Tạo kết nối
            System.out.println("🔄 Đang kết nối đến: " + URL);
            System.out.println("👤 Username: " + USERNAME);

            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("✅ Kết nối SQL Server thành công!");

            // Test query cơ bản
            Statement statement = connection.createStatement();

            // Kiểm tra database
            System.out.println("\n📊 THÔNG TIN DATABASE:");
            ResultSet rs = statement.executeQuery("SELECT @@VERSION as Version");
            if (rs.next()) {
                System.out.println("   SQL Server Version: " + rs.getString("Version"));
            }

            // Kiểm tra các bảng
            System.out.println("\n📋 KIỂM TRA CÁC BẢNG:");
            rs = statement.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' ORDER BY TABLE_NAME");
            while (rs.next()) {
                System.out.println("   ✓ " + rs.getString("TABLE_NAME"));
            }

            // Kiểm tra người dùng
            System.out.println("\n👥 KIỂM TRA NGƯỜI DÙNG:");
            rs = statement.executeQuery("SELECT COUNT(*) as UserCount FROM nguoi_dung");
            if (rs.next()) {
                int userCount = rs.getInt("UserCount");
                System.out.println("   Tổng số người dùng: " + userCount);

                if (userCount > 0) {
                    // Hiển thị danh sách user
                    ResultSet userRs = statement.executeQuery("SELECT ten_dang_nhap, ho_va_ten FROM nguoi_dung ORDER BY ten_dang_nhap");
                    while (userRs.next()) {
                        System.out.println("   👤 " + userRs.getString("ten_dang_nhap") + " - " + userRs.getString("ho_va_ten"));
                    }
                } else {
                    System.out.println("   ⚠️ Chưa có người dùng nào trong database!");
                    System.out.println("   💡 Hãy chạy script tạo dữ liệu mẫu trước");
                }
            }

            // Kiểm tra vai trò
            System.out.println("\n🔐 KIỂM TRA VAI TRÒ:");
            rs = statement.executeQuery("SELECT COUNT(*) as RoleCount FROM vai_tro");
            if (rs.next()) {
                int roleCount = rs.getInt("RoleCount");
                System.out.println("   Tổng số vai trò: " + roleCount);

                if (roleCount > 0) {
                    ResultSet roleRs = statement.executeQuery("SELECT ten_vai_tro, mo_ta FROM vai_tro ORDER BY ten_vai_tro");
                    while (roleRs.next()) {
                        System.out.println("   🔒 " + roleRs.getString("ten_vai_tro") + " - " + roleRs.getString("mo_ta"));
                    }
                }
            }

            // Test authentication với user admin
            System.out.println("\n🔑 TEST AUTHENTICATION:");
            rs = statement.executeQuery("SELECT ten_dang_nhap, ho_va_ten, trang_thai_hoat_dong, tai_khoan_khong_bi_khoa FROM nguoi_dung WHERE ten_dang_nhap = 'admin'");
            if (rs.next()) {
                System.out.println("   ✅ Tìm thấy user admin:");
                System.out.println("      - Username: " + rs.getString("ten_dang_nhap"));
                System.out.println("      - Full Name: " + rs.getString("ho_va_ten"));
                System.out.println("      - Active: " + (rs.getBoolean("trang_thai_hoat_dong") ? "YES" : "NO"));
                System.out.println("      - Unlocked: " + (rs.getBoolean("tai_khoan_khong_bi_khoa") ? "YES" : "NO"));
                System.out.println("      - Password: 123456 (default)");
            } else {
                System.out.println("   ❌ Không tìm thấy user admin!");
            }

            connection.close();

            System.out.println("\n=================================================================");
            System.out.println("✅ KIỂM TRA HOÀN THÀNH - DATABASE SẴN SÀNG SỬ DỤNG!");
            System.out.println("🚀 Có thể khởi động ứng dụng Spring Boot");
            System.out.println("=================================================================");

        } catch (Exception e) {
            System.err.println("❌ LỖI KẾT NỐI DATABASE:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();

            System.out.println("\n💡 CÁCH KHẮC PHỤC:");
            System.out.println("1. Kiểm tra SQL Server đã khởi động chưa");
            System.out.println("2. Kiểm tra thông tin kết nối (server, port, database)");
            System.out.println("3. Kiểm tra username/password");
            System.out.println("4. Kiểm tra firewall và network");
            System.out.println("5. Đảm bảo database 'BaoTriThietBi' đã được tạo");
        }
    }
}