package com.thuvien.dao;

import dto.*;
import com.thuvien.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThuVienDAO {

    // ================= TÍNH NĂNG CHUNG =================
    public boolean guiPhanHoi(Integer maDocGia, String hoTenKhach, String emailKhach, String noiDung) throws Exception {
        String sql = "INSERT INTO PhanHoi (MaDocGia, HoTenKhach, EmailKhach, NoiDung) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (maDocGia != null) ps.setInt(1, maDocGia); else ps.setNull(1, Types.INTEGER);
            ps.setString(2, hoTenKhach); ps.setString(3, emailKhach); ps.setString(4, noiDung);
            return ps.executeUpdate() > 0;
        }
    }

    // ================= NGHIỆP VỤ ĐỘC GIẢ =================
    public boolean dangKyDocGia(String hoTen, String email, String tenDangNhap, String matKhau) throws Exception {
        String sql = "{call sp_DangKyDocGia(?, ?, ?, ?, NULL, NULL)}";
        try (Connection conn = DBConnection.getConnection(); CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, hoTen); cs.setString(2, email); cs.setString(3, tenDangNhap); cs.setString(4, matKhau);
            cs.execute();
            return true;
        }
    }

    public DocGiaDTO dangNhapDocGia(String tenDangNhap, String matKhau) throws Exception {
        String sql = "{call sp_DangNhapDocGia(?, ?)}";
        try (Connection conn = DBConnection.getConnection(); CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, tenDangNhap); cs.setString(2, matKhau);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) return new DocGiaDTO(rs.getInt("MaDocGia"), rs.getString("HoTen"), rs.getString("Email"), true);
            }
        }
        return null;
    }

    public List<SachDTO> timKiemSach(String tuKhoa) throws Exception {
        List<SachDTO> list = new ArrayList<>();
        String sql = "SELECT MaSach, TieuDe, SoLuong, GiaTien FROM Sach WHERE TieuDe LIKE ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + tuKhoa + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new SachDTO(rs.getInt("MaSach"), rs.getString("TieuDe"), rs.getInt("SoLuong"), rs.getDouble("GiaTien")));
            }
        }
        return list;
    }

    public void muonSach(int maDocGia, int maSach, int soLuong, Date ngayHenTra) throws Exception {
        String sql = "{call sp_TaoPhieuMuon(?, 1, ?, ?, ?)}";
        try (Connection conn = DBConnection.getConnection(); CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, maDocGia); cs.setInt(2, maSach); cs.setInt(3, soLuong); cs.setDate(4, ngayHenTra);
            cs.execute();
        }
    }

    public List<PhieuMuonDTO> xemLichSuMuon(int maDocGia) throws Exception {
        List<PhieuMuonDTO> list = new ArrayList<>();
        String sql = "SELECT pm.MaPhieu, s.TieuDe, ct.SoLuongMuon, ct.SoLuongDaTra, ct.NgayHenTra, pm.TrangThai " +
                "FROM PhieuMuon pm JOIN ChiTietPhieuMuon ct ON pm.MaPhieu = ct.MaPhieu " +
                "JOIN Sach s ON ct.MaSach = s.MaSach WHERE pm.MaDocGia = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maDocGia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new PhieuMuonDTO(rs.getInt("MaPhieu"), rs.getString("TieuDe"), rs.getInt("SoLuongMuon"), rs.getInt("SoLuongDaTra"), rs.getDate("NgayHenTra"), rs.getString("TrangThai")));
            }
        }
        return list;
    }

    public boolean luuSachYeuThich(int maDocGia, int maSach) throws Exception {
        String sql = "INSERT INTO SachYeuThich(MaDocGia, MaSach) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maDocGia); ps.setInt(2, maSach);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean guiDeXuatSach(int maDocGia, String tenSach, String tacGia, String lyDo) throws Exception {
        String sql = "INSERT INTO DeXuatSach(MaDocGia, TenSachDeXuat, TacGiaDeXuat, LyDoDeXuat) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maDocGia); ps.setString(2, tenSach); ps.setString(3, tacGia); ps.setString(4, lyDo);
            return ps.executeUpdate() > 0;
        }
    }

    // ================= NGHIỆP VỤ ADMIN =================
    public TaiKhoanDTO dangNhapAdmin(String tenDangNhap, String matKhau) throws Exception {
        String sql = "SELECT MaTK, HoTen FROM TaiKhoan WHERE TenDangNhap = ? AND MatKhauHash = HASHBYTES('SHA2_256', CONVERT(VARBINARY(4000), ?) + MuoiSalt) AND TrangThai = 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap); ps.setString(2, matKhau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new TaiKhoanDTO(rs.getInt("MaTK"), rs.getString("HoTen"));
            }
        }
        return null;
    }

    public boolean themSach(String tieuDe, int soLuong, double giaTien) throws Exception {
        String sql = "INSERT INTO Sach(TieuDe, SoLuong, GiaTien) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tieuDe); ps.setInt(2, soLuong); ps.setDouble(3, giaTien);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatSach(int maSach, String tieuDe, int soLuong, double giaTien) throws Exception {
        String sql = "UPDATE Sach SET TieuDe=?, SoLuong=?, GiaTien=? WHERE MaSach=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tieuDe); ps.setInt(2, soLuong); ps.setDouble(3, giaTien); ps.setInt(4, maSach);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean xoaSach(int maSach) throws Exception {
        String sql = "DELETE FROM Sach WHERE MaSach = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maSach);
            return ps.executeUpdate() > 0;
        }
    }

    public void traSach(int maPhieu, int maSach, int soLuongTra) throws Exception {
        String sql = "{call sp_TraSach(?, ?, ?, 0)}";
        try (Connection conn = DBConnection.getConnection(); CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, maPhieu); cs.setInt(2, maSach); cs.setInt(3, soLuongTra);
            cs.execute();
        }
    }

    public boolean kiemKeKho(int maAdmin, int maSach, int soLuongThucTe) throws Exception {
        String sql = "INSERT INTO DotKiemKe(MaAdmin, GhiChu) VALUES (?, N'Kiểm kê định kỳ'); SELECT SCOPE_IDENTITY() AS MaDot;";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maAdmin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int maDot = rs.getInt("MaDot");
                    String sqlChiTiet = "INSERT INTO ChiTietKiemKe(MaDot, MaSach, SoLuongHeThong, SoLuongThucTe) SELECT ?, ?, SoLuong, ? FROM Sach WHERE MaSach = ?";
                    try(PreparedStatement psCT = conn.prepareStatement(sqlChiTiet)){
                        psCT.setInt(1, maDot); psCT.setInt(2, maSach); psCT.setInt(3, soLuongThucTe); psCT.setInt(4, maSach);
                        return psCT.executeUpdate() > 0;
                    }
                }
            }
        }
        return false;
    }

    public List<DocGiaDTO> layDanhSachDocGia() throws Exception {
        List<DocGiaDTO> list = new ArrayList<>();
        String sql = "SELECT MaDocGia, HoTen, Email, TrangThai FROM DocGia";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new DocGiaDTO(rs.getInt("MaDocGia"), rs.getString("HoTen"), rs.getString("Email"), rs.getBoolean("TrangThai")));
        }
        return list;
    }

    public boolean khoaTaiKhoanDocGia(int maDocGia, boolean moKhoa) throws Exception {
        String sql = "UPDATE DocGia SET TrangThai = ? WHERE MaDocGia = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, moKhoa); ps.setInt(2, maDocGia);
            return ps.executeUpdate() > 0;
        }
    }

    public BaoCaoDTO layBaoCaoTongQuan() throws Exception {
        String sql = "SELECT * FROM vw_BaoCaoTongQuan";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new BaoCaoDTO(rs.getInt("TongDocGiaHoatDong"), rs.getInt("TongSachTrongKho"), rs.getInt("TongLuotSachDaMuon"), rs.getBigDecimal("TongDoanhThuTienPhat"));
            }
        }
        return null;
    }

    public List<PhanHoiDTO> layDanhSachPhanHoi() throws Exception {
        List<PhanHoiDTO> list = new ArrayList<>();
        String sql = "SELECT ISNULL(dg.HoTen, p.HoTenKhach) AS NguoiGui, ISNULL(dg.Email, p.EmailKhach) AS Email, p.NoiDung, p.NgayGui FROM PhanHoi p LEFT JOIN DocGia dg ON p.MaDocGia = dg.MaDocGia";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while(rs.next()) list.add(new PhanHoiDTO(rs.getString("NguoiGui"), rs.getString("Email"), rs.getString("NoiDung"), rs.getDate("NgayGui")));
        }
        return list;
    }

    public List<DeXuatDTO> layDanhSachDeXuat() throws Exception {
        List<DeXuatDTO> list = new ArrayList<>();
        String sql = "SELECT dx.TenSachDeXuat, dx.TacGiaDeXuat, dx.LyDoDeXuat, dg.HoTen FROM DeXuatSach dx JOIN DocGia dg ON dx.MaDocGia = dg.MaDocGia";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while(rs.next()) list.add(new DeXuatDTO(rs.getString("HoTen"), rs.getString("TenSachDeXuat"), rs.getString("TacGiaDeXuat"), rs.getString("LyDoDeXuat")));
        }
        return list;
    }
    // ================= TÍNH NĂNG TỰ ĐỘNG (AUTOMATION) =================
    public List<NhacNhoDTO> layDanhSachCanNhacNho() {
        List<NhacNhoDTO> list = new ArrayList<>();
        // Query những sách chưa trả xong và có Ngày hẹn trả <= Ngày mai
        String sql = "SELECT dg.Email, dg.HoTen, s.TieuDe, ct.NgayHenTra, " +
                "DATEDIFF(day, ct.NgayHenTra, GETDATE()) AS SoNgayQuaHan " +
                "FROM PhieuMuon pm " +
                "JOIN ChiTietPhieuMuon ct ON pm.MaPhieu = ct.MaPhieu " +
                "JOIN DocGia dg ON pm.MaDocGia = dg.MaDocGia " +
                "JOIN Sach s ON ct.MaSach = s.MaSach " +
                "WHERE ct.SoLuongDaTra < ct.SoLuongMuon " +
                "AND DATEDIFF(day, GETDATE(), ct.NgayHenTra) <= 1"; // Sắp đến hạn 1 ngày hoặc đã quá hạn

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new NhacNhoDTO(
                        rs.getString("Email"), rs.getString("HoTen"),
                        rs.getString("TieuDe"), rs.getDate("NgayHenTra"),
                        rs.getInt("SoNgayQuaHan")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}