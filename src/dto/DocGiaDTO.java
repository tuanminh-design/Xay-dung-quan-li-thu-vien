package dto;

public class DocGiaDTO {
    private int maDocGia;
    private String hoTen;
    private String email;
    private boolean trangThai;

    public DocGiaDTO(int maDocGia, String hoTen, String email, boolean trangThai) {
        this.maDocGia = maDocGia; this.hoTen = hoTen;
        this.email = email; this.trangThai = trangThai;
    }
    public int getMaDocGia() {
        return maDocGia;
    }
    public String getHoTen() {
        return hoTen;
    }
    public String getEmail() {
        return email;
    }
    public boolean isTrangThai() {
        return trangThai;
    }

    @Override
    public String toString() {
        return String.format("ID: %-3d | Tên: %-20s | Email: %-25s | Trạng thái: %s",
                maDocGia, hoTen, email, (trangThai ? "Hoạt động" : "Bị Khóa"));
    }
}