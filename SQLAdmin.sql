CREATE DATABASE QLThuvien;
GO
USE QLThuvien;
GO

-- 1. Bảng Vai trò
CREATE TABLE VaiTro (
    MaVaiTro INT PRIMARY KEY,
    TenVaiTro NVARCHAR(50) UNIQUE NOT NULL
);

-- 2. Bảng Tài khoản (Admin & User)
CREATE TABLE TaiKhoan (
    MaTK INT IDENTITY(1,1) PRIMARY KEY,
    TenDangNhap VARCHAR(50) UNIQUE NOT NULL,
    MatKhauHash VARCHAR(MAX) NOT NULL,
    HoTen NVARCHAR(100),
    Email VARCHAR(100) UNIQUE NOT NULL,
    MaVaiTro INT FOREIGN KEY REFERENCES VaiTro(MaVaiTro),
    TrangThai BIT DEFAULT 1 -- 1: Hoạt động, 0: Khóa
);

-- 3. Danh mục bổ trợ
CREATE TABLE TheLoai (MaLoai INT IDENTITY(1,1) PRIMARY KEY, TenLoai NVARCHAR(100) UNIQUE);
CREATE TABLE TacGia (MaTG INT IDENTITY(1,1) PRIMARY KEY, TenTG NVARCHAR(100));

-- 4. Bảng Sách
CREATE TABLE Sach (
    MaSach INT IDENTITY(1,1) PRIMARY KEY,
    TieuDe NVARCHAR(250) NOT NULL,
    MaLoai INT FOREIGN KEY REFERENCES TheLoai(MaLoai),
    MaTG INT FOREIGN KEY REFERENCES TacGia(MaTG),
    SoLuong INT CHECK (SoLuong >= 0),
    GiaTien DECIMAL(18,2),
    ViTri NVARCHAR(100)
);

-- 5. Bảng Phiếu mượn
CREATE TABLE PhieuMuon (
    MaPhieu INT IDENTITY(1,1) PRIMARY KEY,
    MaND INT FOREIGN KEY REFERENCES TaiKhoan(MaTK), -- Người mượn
    MaAdmin INT FOREIGN KEY REFERENCES TaiKhoan(MaTK), -- Admin duyệt
    NgayMuon DATETIME DEFAULT GETDATE()
);

-- 6. Chi tiết mượn (Quan hệ Nhiều-Nhiều)
CREATE TABLE ChiTietPhieuMuon (
    MaPhieu INT FOREIGN KEY REFERENCES PhieuMuon(MaPhieu),
    MaSach INT FOREIGN KEY REFERENCES Sach(MaSach),
    NgayHenTra DATETIME NOT NULL,
    NgayTraThuc DATETIME NULL,
    TienPhat DECIMAL(18,2) DEFAULT 0,
    PRIMARY KEY (MaPhieu, MaSach)
);

-- 7. Bảng Góp ý (Feedback)
CREATE TABLE PhanHoi (
    MaPH INT IDENTITY(1,1) PRIMARY KEY,
    MaND INT FOREIGN KEY REFERENCES TaiKhoan(MaTK),
    NoiDung NVARCHAR(MAX) NOT NULL,
    NgayGui DATETIME DEFAULT GETDATE(),
    TrangThai NVARCHAR(50) DEFAULT N'Chưa xem'
);

-- 8. Bảng Cấu hình (Settings)
CREATE TABLE CauHinh (
    MaKhoa VARCHAR(50) PRIMARY KEY,
    GiaTri DECIMAL(18,2),
    GhiChu NVARCHAR(250)
);