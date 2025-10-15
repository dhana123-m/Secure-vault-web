package com.example.vault;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class VaultController {

    private final VaultService svc;

    public VaultController(VaultService svc) {
        this.svc = svc;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam("master") String master, RedirectAttributes ra) {
        char[] m = master.toCharArray();
        try {
            // ✅ If vault file does not exist, create it automatically with default password
            if (!svc.vaultExists()) {
                char[] defaultMaster = "admin123".toCharArray();
                svc.createEmptyVault(defaultMaster);
                svc.unlock(defaultMaster);
                return "redirect:/dashboard";
            }

            boolean ok = svc.unlock(m);
            if (ok) return "redirect:/dashboard";
        } catch (Exception e) {
            e.printStackTrace();
        }
        ra.addFlashAttribute("error", "Wrong master password or vault error.");
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("list", svc.list());
        return "dashboard";
    }

    @GetMapping("/add")
    public String addForm() {
        return "add";
    }

    @PostMapping("/add")
    public String doAdd(@RequestParam("site") String site,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        RedirectAttributes ra) {
        svc.add(site, username, password);
        try { svc.save(); } catch (Exception e) { ra.addFlashAttribute("error", "Save failed: " + e.getMessage()); }
        return "redirect:/dashboard";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("idx") int idx, RedirectAttributes ra) {
        svc.delete(idx);
        try { svc.save(); } catch (Exception e) { ra.addFlashAttribute("error", "Save failed: " + e.getMessage()); }
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout() {
        svc.lock();
        return "redirect:/login";
    }
}
