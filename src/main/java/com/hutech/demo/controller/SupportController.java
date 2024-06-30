package com.hutech.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.hutech.demo.repository.CustomerSupportRepository;
import com.hutech.demo.model.CustomerSupport;
import java.util.List;
import java.time.LocalDateTime;
import com.hutech.demo.controller.ReplyForm;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/support")
public class SupportController {

    @Autowired
    private CustomerSupportRepository supportRepository;

    // Phương thức để hiển thị form nhập yêu cầu hỗ trợ
    @GetMapping("/form")
    public String showSupportForm(Model model) {
        model.addAttribute("support", new CustomerSupport()); // Truyền đối tượng mới vào model
        return "support/support-form"; // Trả về view để hiển thị form
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "support/success"; // Trả về tên của view để hiển thị trang "Success"
    }

    // Xử lý yêu cầu từ khách hàng gửi hỗ trợ
    @PostMapping("/submit")
    public String submitSupport(@ModelAttribute CustomerSupport support) {
        support.setSubmitDate(LocalDateTime.now());
        supportRepository.save(support);
        return "redirect:/support/success"; // Điều hướng về trang thành công
    }

    // Xem danh sách yêu cầu hỗ trợ (dành cho admin)
    @GetMapping("/list")
    public String showSupportList(Model model) {
        List<CustomerSupport> supportRequests = supportRepository.findAll();
        model.addAttribute("supportRequests", supportRequests);
        return "support/support-list";
    }

    // Xem chi tiết một yêu cầu hỗ trợ cụ thể (dành cho admin)
    @GetMapping("/details/{id}")
    public String showSupportDetails(@PathVariable Long id, Model model) {
        CustomerSupport support = supportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid support id: " + id));
        model.addAttribute("support", support);
        return "support/support-details"; // Trả về view hiển thị chi tiết hỗ trợ
    }

    // Phản hồi cho một yêu cầu hỗ trợ (dành cho admin)
    @PostMapping("/respond/{id}")
    public String respondToSupport(@PathVariable("id") Long id, @RequestParam("response") String response) {
        CustomerSupport support = supportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid support ID: " + id));
        // Xử lý phản hồi vào đây, ví dụ lưu vào field của entity hoặc gửi email
        // Ví dụ gửi email đến địa chỉ của khách hàng
        String customerEmail = support.getCustomerEmail();
        // Thực hiện gửi email (ví dụ: đoạn mã gửi email đã tham khảo trước đó)

        // Đánh dấu là đã giải quyết
        support.setResolved(true);
        supportRepository.save(support);
        return "redirect:/support/details/" + id; // Điều hướng về trang chi tiết yêu cầu đã phản hồi
    }

    // Xử lý gửi phản hồi (dành cho admin)
    @PostMapping("/send-reply")
    public String sendReply(@ModelAttribute("replyForm") ReplyForm replyForm, RedirectAttributes redirectAttributes) {
        Long supportId = replyForm.getSupportId();
        String replyMessage = replyForm.getReplyMessage();

        // Xử lý gửi phản hồi và cập nhật trạng thái
        // Ví dụ: gửi email và cập nhật trạng thái yêu cầu hỗ trợ
        CustomerSupport support = supportRepository.findById(supportId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid support ID: " + supportId));

        // Thực hiện gửi email (ví dụ: đoạn mã gửi email đã tham khảo trước đó)
        String customerEmail = support.getCustomerEmail();
        // Gửi email
        // emailService.sendEmail(customerEmail, replyMessage);

        // Cập nhật trạng thái yêu cầu hỗ trợ đã giải quyết
        support.setResolved(true);
        supportRepository.save(support);

        // Redirect về trang chi tiết yêu cầu hỗ trợ
        redirectAttributes.addAttribute("id", supportId);
        return "redirect:/support/details/{id}";
    }

    // Xử lý yêu cầu từ khách hàng gửi hỗ trợ với việc đặt ngày gửi
    @PostMapping("/submitSupportRequest")
    public String submitSupportRequest(CustomerSupport support) {
        support.setSubmitDate(LocalDateTime.now());
        supportRepository.save(support); // Sử dụng instance supportRepository thay vì class CustomerSupportRepository
        return "redirect:/support";
    }
}
