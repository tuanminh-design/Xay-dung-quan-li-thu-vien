import com.thuvien.dao.ThuVienDAO;
import dto.*;
import service.NotificationTask; // Import thêm NotificationTask

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ThuVienDAO dao = new ThuVienDAO();

    public static void main(String[] args) {
        // 1. Khởi chạy hệ thống thông báo ngầm ngay khi bật chương trình
        NotificationTask autoNotifier = new NotificationTask();
        autoNotifier.startDeadlineNotifier();

        // 2. Chạy Menu hệ thống
        while (true) {
            System.out.println("\n====== HỆ THỐNG QUẢN LÝ THƯ VIỆN ENTERPRISE ======");
            System.out.println("1. Đăng nhập ĐỘC GIẢ");
            System.out.println("2. Đăng ký ĐỘC GIẢ MỚI");
            System.out.println("3. Đăng nhập NHÂN VIÊN / ADMIN");
            System.out.println("4. Gửi Góp ý / Phản hồi (Khách vãng lai)");
            System.out.println("0. Thoát");
            System.out.print("Chọn chức năng: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1: menuDocGia(); break;
                    case 2: dangKyDocGia(); break;
                    case 3: menuNhanVien(); break;
                    case 4: guiGopYKhach(); break;
                    case 0: System.out.println("Tạm biệt!"); System.exit(0);
                    default: System.out.println("❌ Lựa chọn không hợp lệ!");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Vui lòng nhập số hợp lệ!");
            }
        }
    }

    private static void guiGopYKhach() {
        System.out.println("\n--- GỬI Ý KIẾN ĐÓNG GÓP ---");
        System.out.print("Họ tên của bạn: "); String ten = scanner.nextLine();
        System.out.print("Địa chỉ Email liên hệ: "); String email = scanner.nextLine();
        System.out.print("Nội dung góp ý: "); String noiDung = scanner.nextLine();
        try {
            if (dao.guiPhanHoi(null, ten, email, noiDung)) System.out.println(" Cảm ơn bạn đã đóng góp ý kiến!");
        } catch (Exception e) { System.out.println(" Lỗi hệ thống: " + e.getMessage()); }
    }

    private static void dangKyDocGia() {
        System.out.println("\n--- ĐĂNG KÝ ĐỘC GIẢ ---");
        System.out.print("Họ tên: "); String ten = scanner.nextLine();
        System.out.print("Email: "); String email = scanner.nextLine();
        System.out.print("Tên đăng nhập: "); String username = scanner.nextLine();
        System.out.print("Mật khẩu: "); String pass = scanner.nextLine();
        try {
            if(dao.dangKyDocGia(ten, email, username, pass))
                System.out.println(" Đăng ký thành công! Vui lòng đăng nhập.");
        } catch (Exception e) { System.out.println(" Lỗi đăng ký: Tài khoản hoặc Email đã tồn tại."); }
    }

    private static void menuDocGia() {
        System.out.println("\n--- ĐĂNG NHẬP ĐỘC GIẢ ---");
        System.out.print("Tên đăng nhập: "); String user = scanner.nextLine();
        System.out.print("Mật khẩu: "); String pass = scanner.nextLine();

        try {
            DocGiaDTO docGia = dao.dangNhapDocGia(user, pass);
            if (docGia == null) { System.out.println(" Sai thông tin hoặc tài khoản bị khóa!"); return; }

            System.out.println("\nXin chào Độc giả: " + docGia.getHoTen());
            while (true) {
                System.out.println("\n--- TÍNH NĂNG ĐỘC GIẢ ---");
                System.out.println("1. Tìm kiếm sách");
                System.out.println("2. Mượn sách");
                System.out.println("3. Xem lịch sử mượn");
                System.out.println("4. Thêm sách yêu thích");
                System.out.println("5. Đề xuất mua sách mới");
                System.out.println("6. Gửi phản hồi / Khiếu nại dịch vụ");
                System.out.println("0. Đăng xuất");
                System.out.print("Chọn chức năng: ");

                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == 0) break;

                switch (choice) {
                    case 1:
                        System.out.print("Nhập từ khóa tên sách: ");
                        List<SachDTO> kq = dao.timKiemSach(scanner.nextLine());
                        if (kq.isEmpty()) System.out.println("Không tìm thấy sách!");
                        else kq.forEach(System.out::println);
                        break;
                    case 2:
                        System.out.print("Mã Sách cần mượn: "); int maSach = Integer.parseInt(scanner.nextLine());
                        System.out.print("Số lượng mượn: "); int soLuong = Integer.parseInt(scanner.nextLine());
                        System.out.print("Số ngày mượn: "); int soNgay = Integer.parseInt(scanner.nextLine());
                        dao.muonSach(docGia.getMaDocGia(), maSach, soLuong, Date.valueOf(LocalDate.now().plusDays(soNgay)));
                        System.out.println(" Tạo phiếu mượn thành công.");
                        break;
                    case 3:
                        List<PhieuMuonDTO> ls = dao.xemLichSuMuon(docGia.getMaDocGia());
                        if(ls.isEmpty()) System.out.println("Chưa có giao dịch."); else ls.forEach(System.out::println);
                        break;
                    case 4:
                        System.out.print("Mã Sách thêm Yêu thích: ");
                        if(dao.luuSachYeuThich(docGia.getMaDocGia(), Integer.parseInt(scanner.nextLine())))
                            System.out.println(" Đã lưu vào yêu thích.");
                        break;
                    case 5:
                        System.out.print("Tên sách đề xuất: "); String ts = scanner.nextLine();
                        System.out.print("Tác giả: "); String tg = scanner.nextLine();
                        System.out.print("Lý do đề xuất: "); String ld = scanner.nextLine();
                        if(dao.guiDeXuatSach(docGia.getMaDocGia(), ts, tg, ld)) System.out.println(" Đã gửi đề xuất!");
                        break;
                    case 6:
                        System.out.print("Nội dung khiếu nại: ");
                        if(dao.guiPhanHoi(docGia.getMaDocGia(), null, null, scanner.nextLine())) System.out.println("✅ Đã gửi phản hồi.");
                        break;
                }
            }
        } catch (Exception e) { System.out.println(" Đã xảy ra lỗi: " + e.getMessage()); }
    }

    private static void menuNhanVien() {
        System.out.println("\n--- ĐĂNG NHẬP ADMIN ---");
        System.out.print("Tên đăng nhập: "); String user = scanner.nextLine();
        System.out.print("Mật khẩu: "); String pass = scanner.nextLine();

        try {
            TaiKhoanDTO admin = dao.dangNhapAdmin(user, pass);
            if (admin == null) { System.out.println(" Sai tài khoản hoặc bạn không có quyền!"); return; }

            System.out.println("\n Xin chào Quản trị viên: " + admin.getHoTen());
            while (true) {
                System.out.println("\n--- TÍNH NĂNG NHÂN VIÊN ---");
                System.out.println("1. Thêm Sách mới");
                System.out.println("2. Sửa thông tin sách");
                System.out.println("3. Xóa sách");
                System.out.println("4. Xem / Khóa thẻ Độc giả");
                System.out.println("5. Xử lý Trả sách");
                System.out.println("6. Kiểm kê kho");
                System.out.println("7. Báo cáo thống kê");
                System.out.println("8. Xem hòm thư Góp ý");
                System.out.println("9. Xem danh sách Đề xuất sách");
                System.out.println("0. Đăng xuất");
                System.out.print("Chọn chức năng: ");

                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == 0) break;

                switch (choice) {
                    case 1:
                        System.out.print("Tựa sách: "); String t = scanner.nextLine();
                        System.out.print("Số lượng: "); int sl = Integer.parseInt(scanner.nextLine());
                        System.out.print("Giá: "); double g = Double.parseDouble(scanner.nextLine());
                        if(dao.themSach(t, sl, g)) System.out.println(" Đã thêm sách.");
                        break;
                    case 2:
                        System.out.print("Mã sách cần sửa: "); int idUpdate = Integer.parseInt(scanner.nextLine());
                        System.out.print("Tựa sách mới: "); String tm = scanner.nextLine();
                        System.out.print("Số lượng mới: "); int slm = Integer.parseInt(scanner.nextLine());
                        System.out.print("Giá mới: "); double gm = Double.parseDouble(scanner.nextLine());
                        if(dao.capNhatSach(idUpdate, tm, slm, gm)) System.out.println(" Đã cập nhật.");
                        break;
                    case 3:
                        System.out.print("Mã sách xóa: ");
                        if(dao.xoaSach(Integer.parseInt(scanner.nextLine()))) System.out.println(" Đã xóa.");
                        break;
                    case 4:
                        dao.layDanhSachDocGia().forEach(System.out::println);
                        System.out.print("\nThao tác (1: Mở khóa thẻ, 0: Khóa thẻ, -1: Bỏ qua): ");
                        int action = Integer.parseInt(scanner.nextLine());
                        if(action != -1) {
                            System.out.print("Nhập Mã Độc giả: ");
                            if(dao.khoaTaiKhoanDocGia(Integer.parseInt(scanner.nextLine()), action == 1))
                                System.out.println(" Cập nhật thẻ thành công.");
                        }
                        break;
                    case 5:
                        System.out.print("Mã Phiếu mượn: "); int maPhieu = Integer.parseInt(scanner.nextLine());
                        System.out.print("Mã Sách trả: "); int maSachTra = Integer.parseInt(scanner.nextLine());
                        System.out.print("Số lượng trả: "); int slTra = Integer.parseInt(scanner.nextLine());
                        dao.traSach(maPhieu, maSachTra, slTra);
                        System.out.println(" Đã ghi nhận trả sách.");
                        break;
                    case 6:
                        System.out.print("Mã Sách kiểm kê: "); int maSachKK = Integer.parseInt(scanner.nextLine());
                        System.out.print("SL thực tế: "); int slThucTe = Integer.parseInt(scanner.nextLine());
                        if(dao.kiemKeKho(admin.getMaTK(), maSachKK, slThucTe)) System.out.println(" Đã kiểm kê.");
                        break;
                    case 7:
                        BaoCaoDTO bc = dao.layBaoCaoTongQuan();
                        if(bc != null) System.out.println(bc.toString());
                        break;
                    case 8:
                        List<PhanHoiDTO> ph = dao.layDanhSachPhanHoi();
                        if(ph.isEmpty()) System.out.println("Thùng thư trống."); else ph.forEach(System.out::println);
                        break;
                    case 9:
                        List<DeXuatDTO> dx = dao.layDanhSachDeXuat();
                        if(dx.isEmpty()) System.out.println("Chưa có đề xuất nào."); else dx.forEach(System.out::println);
                        break;
                }
            }
        } catch (Exception e) { System.out.println(" Lỗi thao tác: " + e.getMessage()); }
    }
}