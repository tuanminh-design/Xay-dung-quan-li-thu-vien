package dto;
import java.sql.Date;

public class PhieuMuonDTO {
    private int maPhieu;
    private String tenSach;
    private int soLuongMuon;
    private int soLuongDaTra;
    private Date ngayHenTra;
    private String trangThai;

    public PhieuMuonDTO(int maPhieu, String tenSach, int soLuongMuon, int soLuongDaTra, Date ngayHenTra, String trangThai) {
        this.maPhieu = maPhieu;
        this.tenSach = tenSach;
        this.soLuongMuon = soLuongMuon;
        this.soLuongDaTra = soLuongDaTra;
        this.ngayHenTra = ngayHenTra;
        this.trangThai = trangThai;
    }
    @Override
    public String toString() {
        return String.format("Mã Phiếu: %-4d | Sách: %-25s | Đã mượn: %-2d | Đã trả: %-2d | Hạn: %s | TT: %s",
                maPhieu, tenSach, soLuongMuon, soLuongDaTra, ngayHenTra.toString(), trangThai);
    }
}