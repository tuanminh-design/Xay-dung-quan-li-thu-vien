package dto;

public class TaiKhoanDTO {
    private int maTK;
    private String hoTen;
    public TaiKhoanDTO(int maTK, String hoTen) {
        this.maTK = maTK;
        this.hoTen = hoTen;
    }
    public int getMaTK() {
        return maTK;
    }
    public String getHoTen() {
        return hoTen;
    }
}