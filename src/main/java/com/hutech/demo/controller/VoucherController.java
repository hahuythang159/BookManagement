package com.hutech.demo.controller;

import com.hutech.demo.model.Voucher;
import com.hutech.demo.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/voucher")
public class VoucherController {
    @Autowired
    private VoucherService voucherService;

    @GetMapping("/list")
    public String showVoucherList(Model model) {
        List<Voucher> vouchers = voucherService.getAllVouchers();
        model.addAttribute("vouchers", vouchers);
        return "voucher/voucher-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("voucher", new Voucher());
        return "voucher/voucher-create";
    }

    @PostMapping("/create")
    public String createVoucher(@ModelAttribute("voucher") Voucher voucher) {
        voucherService.createVoucher(voucher);
        return "redirect:/voucher/list";
    }

    @PostMapping("/delete/{voucherId}")
    public String deleteVoucher(@PathVariable Long voucherId) {
        voucherService.deleteVoucher(voucherId);
        return "redirect:/voucher/list";
    }
    @GetMapping("/edit/{id}")
    public String editVoucher(@PathVariable Long id, Model model) {
        Optional<Voucher> voucherOptional = voucherService.getVoucherById(id);
        if (voucherOptional.isPresent()) {
            model.addAttribute("voucher", voucherOptional.get());
            return "voucher/edit-voucher";
        } else {
            // Handle case where voucher is not found
            return "redirect:/voucher"; // Or redirect to another page
        }
    }
    @PostMapping("/update")
    public String updateVoucher(@ModelAttribute Voucher voucher) {
        voucherService.updateVoucher(voucher);
        return "redirect:/voucher/list";
    }

}
