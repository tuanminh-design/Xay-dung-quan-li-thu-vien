package dto;
import java.math.BigDecimal;

public class BaoCaoDTO {
    private int tongDocGia;
    private int tongSachTrongKho;
    private int tongLuotMuon;
    private BigDecimal tongTienPhat;

    public BaoCaoDTO(int tongDocGia, int tongSachTrongKho, int tongLuotMuon, BigDecimal tongTienPhat) {
        this.tongDocGia = tongDocGia;
        this.tongSachTrongKho = tongSachTrongKho;
        this.tongLuotMuon = tongLuotMuon;
        this.tongTienPhat = tongTienPhat;
    }
    @Override
    public String toString() {
        return String.format("Thống kê: Độc giả HĐ: %d | Sách trong kho: %d | Lượt mượn: %d | Doanh thu phạt: %,.0f VNĐ",
                tongDocGia, tongSachTrongKho, tongLuotMuon, tongTienPhat);
    }
}