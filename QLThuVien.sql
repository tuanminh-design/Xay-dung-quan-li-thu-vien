CREATE DATABASE QLThuVien;
GO

USE QLThuVien;
GO

CREATE TABLE DocGia (
    id INT IDENTITY(1,1) PRIMARY KEY,
    hoTen NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) UNIQUE NOT NULL,
    matKhau NVARCHAR(100) NOT NULL,
    soDienThoai NVARCHAR(15),
    diaChi NVARCHAR(255),
    ngayDangKy DATE DEFAULT GETDATE(),
    trangThai NVARCHAR(20) DEFAULT N'Hoạt động'
);


CREATE TABLE Sach (
    id INT IDENTITY(1,1) PRIMARY KEY,
    tenSach NVARCHAR(255) NOT NULL,
    tacGia NVARCHAR(100),
    theLoai NVARCHAR(100),
    soLuong INT DEFAULT 0,
    moTa NVARCHAR(MAX)
);


CREATE TABLE MuonSach (
    id INT IDENTITY(1,1) PRIMARY KEY,
    docGiaId INT NOT NULL,
    sachId INT NOT NULL,
    ngayMuon DATE DEFAULT GETDATE(),
    hanTra DATE NOT NULL,
    ngayTra DATE NULL,
    trangThai NVARCHAR(50) DEFAULT N'Đang mượn',

    FOREIGN KEY (docGiaId) REFERENCES DocGia(id),
    FOREIGN KEY (sachId) REFERENCES Sach(id)
);


CREATE TABLE SachYeuThich (
    id INT IDENTITY(1,1) PRIMARY KEY,
    docGiaId INT,
    sachId INT,

    FOREIGN KEY (docGiaId) REFERENCES DocGia(id),
    FOREIGN KEY (sachId) REFERENCES Sach(id)
);


CREATE TABLE ThongBao (
    id INT IDENTITY(1,1) PRIMARY KEY,
    docGiaId INT,
    noiDung NVARCHAR(255),
    ngayThongBao DATETIME DEFAULT GETDATE(),
    daDoc BIT DEFAULT 0,

    FOREIGN KEY (docGiaId) REFERENCES DocGia(id)
);