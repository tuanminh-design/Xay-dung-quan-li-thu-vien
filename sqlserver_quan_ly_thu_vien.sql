IF DB_ID('QLThuvien') IS NULL
    CREATE DATABASE QLThuvien;
GO
USE QLThuvien;
GO

SET NOCOUNT ON;
GO

-- 1. BANG VAI TRO
CREATE TABLE VaiTro (
    MaVaiTro INT PRIMARY KEY,
    TenVaiTro NVARCHAR(50) UNIQUE NOT NULL
);

-- 2. BANG TAI KHOAN (CHI ADMIN)
CREATE TABLE TaiKhoan (
    MaTK INT IDENTITY(1,1) PRIMARY KEY,
    TenDangNhap VARCHAR(50) UNIQUE NOT NULL,
    MatKhauHash VARBINARY(32) NOT NULL,
    MuoiSalt VARBINARY(16) NOT NULL,
    HoTen NVARCHAR(100),
    Email VARCHAR(100) UNIQUE NOT NULL,
    MaVaiTro INT FOREIGN KEY REFERENCES VaiTro(MaVaiTro),
    TrangThai BIT DEFAULT 1 -- 1: Hoat dong, 0: Khoa
);

-- Nguoi muon la doc gia, admin chi quan ly
CREATE TABLE DocGia (
    MaDocGia INT IDENTITY(1,1) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    Email VARCHAR(100) NULL UNIQUE,
    SoDienThoai VARCHAR(20) NULL,
    DiaChi NVARCHAR(200) NULL,
    TrangThai BIT DEFAULT 1
);

-- 3. DANH MUC BO TRO
CREATE TABLE TheLoai (
    MaLoai INT IDENTITY(1,1) PRIMARY KEY,
    TenLoai NVARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE TacGia (
    MaTG INT IDENTITY(1,1) PRIMARY KEY,
    TenTG NVARCHAR(100) NOT NULL
);

-- Bang lien ket nhieu tac gia cho 1 sach
CREATE TABLE SachTacGia (
    MaSach INT NOT NULL,
    MaTG INT NOT NULL,
    PRIMARY KEY (MaSach, MaTG)
);

-- 4. BANG SACH
CREATE TABLE Sach (
    MaSach INT IDENTITY(1,1) PRIMARY KEY,
    TieuDe NVARCHAR(250) NOT NULL,
    MaLoai INT FOREIGN KEY REFERENCES TheLoai(MaLoai),
    SoLuong INT CHECK (SoLuong >= 0),
    GiaTien DECIMAL(18,2),
    ViTri NVARCHAR(100)
);

ALTER TABLE SachTacGia
ADD CONSTRAINT FK_SachTacGia_Sach FOREIGN KEY (MaSach) REFERENCES Sach(MaSach),
    CONSTRAINT FK_SachTacGia_TacGia FOREIGN KEY (MaTG) REFERENCES TacGia(MaTG);

-- 5. BANG PHIEU MUON
CREATE TABLE PhieuMuon (
    MaPhieu INT IDENTITY(1,1) PRIMARY KEY,
    MaDocGia INT FOREIGN KEY REFERENCES DocGia(MaDocGia), -- Nguoi muon
    MaAdmin INT FOREIGN KEY REFERENCES TaiKhoan(MaTK), -- Admin duyet
    NgayMuon DATETIME DEFAULT GETDATE(),
    TrangThai NVARCHAR(30) DEFAULT N'DangMuon'
);

-- 6. CHI TIET MUON (QUAN HE NHIEU-NHIEU)
CREATE TABLE ChiTietPhieuMuon (
    MaPhieu INT FOREIGN KEY REFERENCES PhieuMuon(MaPhieu),
    MaSach INT FOREIGN KEY REFERENCES Sach(MaSach),
    SoLuongMuon INT NOT NULL CHECK (SoLuongMuon > 0),
    SoLuongDaTra INT NOT NULL DEFAULT 0 CHECK (SoLuongDaTra >= 0),
    NgayHenTra DATETIME NOT NULL,
    NgayTraThuc DATETIME NULL,
    TienPhat DECIMAL(18,2) DEFAULT 0,
    PRIMARY KEY (MaPhieu, MaSach),
    CONSTRAINT CK_ChiTiet_SoLuong CHECK (SoLuongDaTra <= SoLuongMuon)
);

-- 7. BANG GOP Y (FEEDBACK)
CREATE TABLE PhanHoi (
    MaPH INT IDENTITY(1,1) PRIMARY KEY,
    MaDocGia INT FOREIGN KEY REFERENCES DocGia(MaDocGia),
    NoiDung NVARCHAR(MAX) NOT NULL,
    NgayGui DATETIME DEFAULT GETDATE(),
    TrangThai NVARCHAR(50) DEFAULT N'Chua xem'
);

-- 8. BANG CAU HINH (SETTINGS)
CREATE TABLE CauHinh (
    MaKhoa VARCHAR(50) PRIMARY KEY,
    GiaTri DECIMAL(18,2),
    GhiChu NVARCHAR(250)
);

-- 9. KIEM KE KHO DINH KY
CREATE TABLE DotKiemKe (
    MaDot INT IDENTITY(1,1) PRIMARY KEY,
    NgayKiemKe DATETIME DEFAULT GETDATE(),
    MaAdmin INT FOREIGN KEY REFERENCES TaiKhoan(MaTK),
    GhiChu NVARCHAR(250)
);

CREATE TABLE ChiTietKiemKe (
    MaDot INT FOREIGN KEY REFERENCES DotKiemKe(MaDot),
    MaSach INT FOREIGN KEY REFERENCES Sach(MaSach),
    SoLuongHeThong INT NOT NULL CHECK (SoLuongHeThong >= 0),
    SoLuongThucTe INT NOT NULL CHECK (SoLuongThucTe >= 0),
    PRIMARY KEY (MaDot, MaSach)
);
GO

/* =========================================================
   STORED PROCEDURE
   ========================================================= */

-- Dang ky / Dang nhap
CREATE PROCEDURE sp_DangKy
    @TenDangNhap VARCHAR(50),
    @MatKhau NVARCHAR(200),
    @HoTen NVARCHAR(100),
    @Email VARCHAR(100)
AS
BEGIN
    IF EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = @TenDangNhap)
        THROW 51001, N'Ten dang nhap da ton tai', 1;

    DECLARE @Salt VARBINARY(16) = CRYPT_GEN_RANDOM(16);
    DECLARE @Hash VARBINARY(32) = HASHBYTES('SHA2_256', CONVERT(VARBINARY(4000), @MatKhau) + @Salt);

    INSERT INTO TaiKhoan(TenDangNhap, MatKhauHash, MuoiSalt, HoTen, Email, MaVaiTro, TrangThai)
    VALUES (@TenDangNhap, @Hash, @Salt, @HoTen, @Email, 1, 1);
END
GO

CREATE PROCEDURE sp_DangNhap
    @TenDangNhap VARCHAR(50),
    @MatKhau NVARCHAR(200)
AS
BEGIN
    DECLARE @Hash VARBINARY(32), @Salt VARBINARY(16), @TrangThai BIT, @MaVaiTro INT;
    SELECT @Hash = MatKhauHash, @Salt = MuoiSalt, @TrangThai = TrangThai, @MaVaiTro = MaVaiTro
    FROM TaiKhoan WHERE TenDangNhap = @TenDangNhap;

    IF @Hash IS NULL THROW 51002, N'Khong tim thay tai khoan', 1;
    IF @TrangThai = 0 THROW 51003, N'Tai khoan da bi khoa', 1;
    IF @MaVaiTro <> 1 THROW 51005, N'Tai khoan khong thuoc nhom admin', 1;

    IF HASHBYTES('SHA2_256', CONVERT(VARBINARY(4000), @MatKhau) + @Salt) <> @Hash
        THROW 51004, N'Mat khau khong dung', 1;

    SELECT tk.MaTK, tk.TenDangNhap, tk.HoTen, tk.Email, vt.TenVaiTro
    FROM TaiKhoan tk JOIN VaiTro vt ON tk.MaVaiTro = vt.MaVaiTro
    WHERE tk.TenDangNhap = @TenDangNhap;
END
GO

-- Quan ly the loai
CREATE PROCEDURE sp_ThemTheLoai @TenLoai NVARCHAR(100)
AS
BEGIN
    INSERT INTO TheLoai(TenLoai) VALUES (@TenLoai);
END
GO

CREATE PROCEDURE sp_SuaTheLoai @MaLoai INT, @TenLoai NVARCHAR(100)
AS
BEGIN
    UPDATE TheLoai SET TenLoai = @TenLoai WHERE MaLoai = @MaLoai;
END
GO

CREATE PROCEDURE sp_XoaTheLoai @MaLoai INT
AS
BEGIN
    IF EXISTS (SELECT 1 FROM Sach WHERE MaLoai = @MaLoai)
        THROW 51010, N'The loai dang duoc sach su dung, khong the xoa', 1;
    DELETE FROM TheLoai WHERE MaLoai = @MaLoai;
END
GO

-- Quan ly tac gia
CREATE PROCEDURE sp_ThemTacGia @TenTG NVARCHAR(100)
AS
BEGIN
    INSERT INTO TacGia(TenTG) VALUES (@TenTG);
END
GO

CREATE PROCEDURE sp_SuaTacGia @MaTG INT, @TenTG NVARCHAR(100)
AS
BEGIN
    UPDATE TacGia SET TenTG = @TenTG WHERE MaTG = @MaTG;
END
GO

CREATE PROCEDURE sp_XoaTacGia @MaTG INT
AS
BEGIN
    IF EXISTS (SELECT 1 FROM SachTacGia WHERE MaTG = @MaTG)
        THROW 51011, N'Tac gia dang duoc sach su dung, khong the xoa', 1;
    DELETE FROM TacGia WHERE MaTG = @MaTG;
END
GO

CREATE PROCEDURE sp_GanTacGiaSach @MaSach INT, @MaTG INT
AS
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Sach WHERE MaSach = @MaSach)
        THROW 51012, N'Khong tim thay sach', 1;
    IF NOT EXISTS (SELECT 1 FROM TacGia WHERE MaTG = @MaTG)
        THROW 51013, N'Khong tim thay tac gia', 1;
    IF EXISTS (SELECT 1 FROM SachTacGia WHERE MaSach = @MaSach AND MaTG = @MaTG)
        THROW 51014, N'Sach da co tac gia nay', 1;

    INSERT INTO SachTacGia(MaSach, MaTG) VALUES (@MaSach, @MaTG);
END
GO

-- Muon tra sach
CREATE PROCEDURE sp_TaoPhieuMuon
    @MaDocGia INT,
    @MaAdmin INT,
    @MaSach INT,
    @SoLuongMuon INT,
    @NgayHenTra DATETIME
AS
BEGIN
    SET XACT_ABORT ON;
    BEGIN TRAN;
    BEGIN TRY
        DECLARE @MaPhieu INT;
        DECLARE @SoLuongCon INT;

        SELECT @SoLuongCon = SoLuong FROM Sach WITH (UPDLOCK, ROWLOCK) WHERE MaSach = @MaSach;
        IF @SoLuongCon IS NULL THROW 51020, N'Khong tim thay sach', 1;
        IF @SoLuongCon < @SoLuongMuon THROW 51021, N'Khong du sach trong kho', 1;

        IF NOT EXISTS (SELECT 1 FROM DocGia WHERE MaDocGia = @MaDocGia AND TrangThai = 1)
            THROW 51024, N'Khong tim thay doc gia hoac doc gia bi khoa', 1;

        INSERT INTO PhieuMuon(MaDocGia, MaAdmin, TrangThai) VALUES (@MaDocGia, @MaAdmin, N'DangMuon');
        SET @MaPhieu = SCOPE_IDENTITY();

        INSERT INTO ChiTietPhieuMuon(MaPhieu, MaSach, SoLuongMuon, NgayHenTra)
        VALUES (@MaPhieu, @MaSach, @SoLuongMuon, @NgayHenTra);

        UPDATE Sach SET SoLuong = SoLuong - @SoLuongMuon WHERE MaSach = @MaSach;
        COMMIT TRAN;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRAN;
        THROW;
    END CATCH
END
GO

CREATE PROCEDURE sp_TraSach
    @MaPhieu INT,
    @MaSach INT,
    @SoLuongTra INT,
    @TienPhat DECIMAL(18,2) = 0
AS
BEGIN
    SET XACT_ABORT ON;
    BEGIN TRAN;
    BEGIN TRY
        DECLARE @ConNo INT;
        SELECT @ConNo = SoLuongMuon - SoLuongDaTra
        FROM ChiTietPhieuMuon WITH (UPDLOCK, ROWLOCK)
        WHERE MaPhieu = @MaPhieu AND MaSach = @MaSach;

        IF @ConNo IS NULL THROW 51022, N'Khong tim thay chi tiet muon', 1;
        IF @SoLuongTra <= 0 OR @SoLuongTra > @ConNo THROW 51023, N'So luong tra khong hop le', 1;

        UPDATE ChiTietPhieuMuon
        SET SoLuongDaTra = SoLuongDaTra + @SoLuongTra,
            NgayTraThuc = GETDATE(),
            TienPhat = TienPhat + @TienPhat
        WHERE MaPhieu = @MaPhieu AND MaSach = @MaSach;

        UPDATE Sach SET SoLuong = SoLuong + @SoLuongTra WHERE MaSach = @MaSach;

        UPDATE PhieuMuon
        SET TrangThai = CASE
            WHEN EXISTS (
                SELECT 1 FROM ChiTietPhieuMuon
                WHERE MaPhieu = @MaPhieu AND SoLuongDaTra < SoLuongMuon
            ) THEN N'TraMotPhan' ELSE N'DaTra'
        END
        WHERE MaPhieu = @MaPhieu;

        COMMIT TRAN;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRAN;
        THROW;
    END CATCH
END
GO

-- Bao cao
CREATE PROCEDURE sp_BaoCaoTheoThang
    @Nam INT,
    @Thang INT
AS
BEGIN
    SELECT
        COUNT(DISTINCT pm.MaPhieu) AS TongPhieu,
        SUM(ct.SoLuongMuon) AS TongSachMuon,
        SUM(ct.SoLuongDaTra) AS TongSachDaTra,
        SUM(ct.SoLuongMuon - ct.SoLuongDaTra) AS TongSachChuaTra,
        SUM(ct.TienPhat) AS TongTienPhat
    FROM PhieuMuon pm
    JOIN ChiTietPhieuMuon ct ON pm.MaPhieu = ct.MaPhieu
    WHERE YEAR(pm.NgayMuon) = @Nam
      AND MONTH(pm.NgayMuon) = @Thang;
END
GO

-- Kiem ke
CREATE PROCEDURE sp_TaoDotKiemKe
    @MaAdmin INT,
    @GhiChu NVARCHAR(250) = NULL
AS
BEGIN
    INSERT INTO DotKiemKe(MaAdmin, GhiChu) VALUES (@MaAdmin, @GhiChu);
END
GO

CREATE PROCEDURE sp_KiemKeSach
    @MaDot INT,
    @MaSach INT,
    @SoLuongThucTe INT
AS
BEGIN
    DECLARE @SoLuongHeThong INT;
    SELECT @SoLuongHeThong = SoLuong FROM Sach WHERE MaSach = @MaSach;
    IF @SoLuongHeThong IS NULL THROW 51030, N'Khong tim thay sach', 1;

    MERGE ChiTietKiemKe AS t
    USING (SELECT @MaDot AS MaDot, @MaSach AS MaSach) AS s
    ON (t.MaDot = s.MaDot AND t.MaSach = s.MaSach)
    WHEN MATCHED THEN
        UPDATE SET SoLuongHeThong = @SoLuongHeThong, SoLuongThucTe = @SoLuongThucTe
    WHEN NOT MATCHED THEN
        INSERT (MaDot, MaSach, SoLuongHeThong, SoLuongThucTe)
        VALUES (@MaDot, @MaSach, @SoLuongHeThong, @SoLuongThucTe);
END
GO

/* =========================================================
   VIEW
   ========================================================= */
CREATE VIEW vw_SachDayDu
AS
SELECT
    s.MaSach, s.TieuDe, tl.TenLoai, s.SoLuong, s.GiaTien, s.ViTri,
    STRING_AGG(tg.TenTG, N', ') AS DanhSachTacGia
FROM Sach s
LEFT JOIN TheLoai tl ON s.MaLoai = tl.MaLoai
LEFT JOIN SachTacGia stg ON s.MaSach = stg.MaSach
LEFT JOIN TacGia tg ON stg.MaTG = tg.MaTG
GROUP BY s.MaSach, s.TieuDe, tl.TenLoai, s.SoLuong, s.GiaTien, s.ViTri;
GO

CREATE VIEW vw_SachDangMuon
AS
SELECT
    pm.MaPhieu, pm.NgayMuon, dg.HoTen AS NguoiMuon, s.TieuDe,
    ct.SoLuongMuon, ct.SoLuongDaTra,
    (ct.SoLuongMuon - ct.SoLuongDaTra) AS SoLuongConNo,
    ct.NgayHenTra, ct.NgayTraThuc
FROM PhieuMuon pm
JOIN DocGia dg ON pm.MaDocGia = dg.MaDocGia
JOIN ChiTietPhieuMuon ct ON pm.MaPhieu = ct.MaPhieu
JOIN Sach s ON ct.MaSach = s.MaSach
WHERE ct.SoLuongDaTra < ct.SoLuongMuon;
GO

CREATE VIEW vw_BaoCaoTongQuan
AS
SELECT
    COUNT(DISTINCT pm.MaPhieu) AS TongPhieuMuon,
    SUM(ct.SoLuongMuon) AS TongSachMuon,
    SUM(ct.SoLuongDaTra) AS TongSachDaTra,
    SUM(ct.SoLuongMuon - ct.SoLuongDaTra) AS TongSachDangMuon,
    SUM(ct.TienPhat) AS TongTienPhat
FROM PhieuMuon pm
JOIN ChiTietPhieuMuon ct ON pm.MaPhieu = ct.MaPhieu;
GO

/* =========================================================
   DU LIEU MAU
   ========================================================= */
INSERT INTO VaiTro(MaVaiTro, TenVaiTro) VALUES (1, N'Admin');
INSERT INTO TheLoai(TenLoai) VALUES (N'Cong nghe'), (N'Kinh te'), (N'Van hoc');
INSERT INTO TacGia(TenTG) VALUES (N'Nguyen Van A'), (N'Tran Thi B');
INSERT INTO DocGia(HoTen, Email, SoDienThoai, DiaChi) VALUES (N'Doc gia 01', 'docgia01@mail.com', '0900000001', N'Ha Noi');

EXEC sp_DangKy @TenDangNhap='admin', @MatKhau=N'Admin@123', @HoTen=N'Quan tri vien', @Email='admin@mail.com';

INSERT INTO Sach(TieuDe, MaLoai, SoLuong, GiaTien, ViTri)
VALUES (N'SQL Server can ban', 1, 10, 120000, N'Ke A1');

EXEC sp_GanTacGiaSach @MaSach=1, @MaTG=1;
GO

SELECT name
FROM sys.views;

SELECT * FROM Sach;