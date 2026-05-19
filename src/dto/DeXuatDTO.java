package dto;

public class DeXuatDTO {
    private String nguoiDeXuat;
    private String tenSach;
    private String tacGia;
    private String lyDo;

    public DeXuatDTO(String nguoiDeXuat, String tenSach, String tacGia, String lyDo) {
        this.nguoiDeXuat = nguoiDeXuat;
        this.tenSach = tenSach;
        this.tacGia = tacGia;
        this.lyDo = lyDo;
    }
    @Override
    public String toString() {
        return String.format("Sách: %-25s | Tác giả: %-15s | Bởi: %-15s | Lý do: %s",
                tenSach, tacGia, nguoiDeXuat, lyDo);
    }
}