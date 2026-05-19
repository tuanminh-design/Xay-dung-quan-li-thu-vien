package dto;

public class SachDTO {
    private int maSach;
    private String tieuDe;
    private int soLuong;
    private double giaTien;

    public SachDTO(int maSach, String tieuDe, int soLuong, double giaTien) {
        this.maSach = maSach;
        this.tieuDe = tieuDe;
        this.soLuong = soLuong;
        this.giaTien = giaTien;
    }
    public int getMaSach() {
        return maSach;
    }
    @Override
    public String toString() {
        return String.format("ID: %-4d | Tựa sách: %-30s | Tồn kho: %-4d | Giá: %,.0f VNĐ",
                maSach, tieuDe, soLuong, giaTien);
    }
}