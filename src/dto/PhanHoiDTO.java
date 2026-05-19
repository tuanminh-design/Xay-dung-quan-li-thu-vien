package dto;
import java.sql.Date;

public class PhanHoiDTO {
    private String nguoiGui;
    private String email;
    private String noiDung;
    private Date ngayGui;

    public PhanHoiDTO(String nguoiGui, String email, String noiDung, Date ngayGui) {
        this.nguoiGui = nguoiGui;
        this.email = email;
        this.noiDung = noiDung;
        this.ngayGui = ngayGui;
    }
    @Override
    public String toString() {
        return String.format("[%s] %s (%s): %s", ngayGui, nguoiGui, email, noiDung);
    }
}