CREATE DATABASE LibraryManagement;
GO

USE LibraryManagement;
GO

--USER( dùng để lưu tk của người dùng)
CREATE TABLE Users (
    UserID INT IDENTITY(1,1) PRIMARY KEY,
    Username NVARCHAR(50) UNIQUE NOT NULL,
    PasswordHash NVARCHAR(255) NOT NULL,
    FullName NVARCHAR(100),
    Email NVARCHAR(100) UNIQUE,
    Phone NVARCHAR(15),
    Address NVARCHAR(255),
    Role NVARCHAR(20) DEFAULT 'user', -- user/admin
    CreatedAt DATETIME DEFAULT GETDATE()
);

--UserProfiles(luu chi tiết thông tin người dùng)
CREATE TABLE UserProfiles (
    ProfileID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT UNIQUE,
    DateOfBirth DATE,
    Gender NVARCHAR(10),
    Avatar NVARCHAR(255),
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);


--Sach mà người dùng sẽ xem
CREATE TABLE Books (
    BookID INT IDENTITY(1,1) PRIMARY KEY,
    Title NVARCHAR(255) NOT NULL,
    Author NVARCHAR(100),
    Category NVARCHAR(100),
    Quantity INT DEFAULT 0,
    Available INT DEFAULT 0
);


--Sach nguoi dùng  mượn
CREATE TABLE Borrows (
    BorrowID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT,
    BorrowDate DATE DEFAULT GETDATE(),
    DueDate DATE,
    ReturnDate DATE,
    Status NVARCHAR(20) DEFAULT 'borrowing', 
    -- borrowing / returned / late
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);


--Lưu chi tiết sách mượn
CREATE TABLE BorrowDetails (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    BorrowID INT,
    BookID INT,
    Quantity INT DEFAULT 1,
    FOREIGN KEY (BorrowID) REFERENCES Borrows(BorrowID),
    FOREIGN KEY (BookID) REFERENCES Books(BookID)
);

--Lưu số sách ưu thích chưa mượn
CREATE TABLE Favorites (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT,
    BookID INT,
    CreatedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (BookID) REFERENCES Books(BookID)
);


--bảng nhắc nhở sách quá hạn
CREATE TABLE Notifications (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT,
    Content NVARCHAR(255),
    IsRead BIT DEFAULT 0,
    CreatedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);



