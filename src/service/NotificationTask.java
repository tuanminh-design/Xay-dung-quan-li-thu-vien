package service;

import com.thuvien.dao.ThuVienDAO;
import dto.NhacNhoDTO;
import com.thuvien.util.EmailUtil;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationTask {
    private final ThuVienDAO dao = new ThuVienDAO();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void startDeadlineNotifier() {
        // Chạy ngầm lập lịch: Quét tự động
        // Thay số 24, TimeUnit.HOURS thành 1, TimeUnit.MINUTES nếu bạn muốn test nhanh mỗi phút gửi 1 lần
        Runnable task = () -> {
            try {
                System.out.println("🔍 [Hệ thống chạy ngầm] Đang quét danh sách đến hạn trả sách...");
                List<NhacNhoDTO> danhSach = dao.layDanhSachCanNhacNho();

                for (NhacNhoDTO nhacNho : danhSach) {
                    String subject = "";
                    String body = "";

                    if (nhacNho.getSoNgayQuaHan() > 0) {
                        // Kịch bản: Đã QUÁ HẠN
                        subject = "⚠️ QUÁ HẠN TRẢ SÁCH - Thư viện Enterprise";
                        body = "Xin chào " + nhacNho.getHoTen() + ",\n\n" +
                                "Cuốn sách '" + nhacNho.getTenSach() + "' của bạn đã QUÁ HẠN " + nhacNho.getSoNgayQuaHan() + " ngày.\n" +
                                "Hạn trả ban đầu là: " + nhacNho.getNgayHenTra() + ".\n" +
                                "Vui lòng mang sách đến thư viện trả ngay để tránh phát sinh thêm tiền phạt!\n\n" +
                                "Trân trọng,\nBan Quản Trị Thư Viện.";
                    } else {
                        // Kịch bản: SẮP ĐẾN HẠN (Còn 1 ngày hoặc đúng hôm nay)
                        subject = "⏳ Sắp đến hạn trả sách - Thư viện Enterprise";
                        body = "Xin chào " + nhacNho.getHoTen() + ",\n\n" +
                                "Đây là email tự động nhắc nhở bạn. Cuốn sách '" + nhacNho.getTenSach() + "' sẽ đến hạn trả vào ngày: " + nhacNho.getNgayHenTra() + ".\n" +
                                "Vui lòng sắp xếp thời gian đến thư viện để hoàn trả sách.\n\n" +
                                "Trân trọng,\nBan Quản Trị Thư Viện.";
                    }

                    // Gọi hàm gửi Email
                    EmailUtil.sendEmail(nhacNho.getEmailDocGia(), subject, body);
                }
            } catch (Exception e) {
                System.err.println("Lỗi luồng Notification: " + e.getMessage());
            }
        };

        // Kích hoạt chạy nhiệm vụ (Chạy lần đầu sau 5 giây, sau đó lặp lại mỗi 24 tiếng)
        scheduler.scheduleAtFixedRate(task, 5, 24 * 60 * 60, TimeUnit.SECONDS);
    }
}