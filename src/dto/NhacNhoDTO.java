package dto;
import java.sql.Date;

public class NhacNhoDTO {
    private String emailDocGia;
    private String hoTen;
    private String tenSach;
    private Date ngayHenTra;
    private int soNgayQuaHan; // Số âm là còn hạn, số dương là quá hạn

    public NhacNhoDTO(String emailDocGia, String hoTen, String tenSach, Date ngayHenTra, int soNgayQuaHan) {
        this.emailDocGia = emailDocGia;
        this.hoTen = hoTen;
        this.tenSach = tenSach;
        this.ngayHenTra = ngayHenTra;
        this.soNgayQuaHan = soNgayQuaHan;
    }

    public String getEmailDocGia() {
        return emailDocGia;
    }
    public String getHoTen() {
        return hoTen;
    }
    public String getTenSach() {
        return tenSach;
    }
    public Date getNgayHenTra() {
        return ngayHenTra;
    }
    public int getSoNgayQuaHan() {
        return soNgayQuaHan;
    }
}