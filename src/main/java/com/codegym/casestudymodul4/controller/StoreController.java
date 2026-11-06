package com.codegym.casestudymodul4.controller;

import com.codegym.casestudymodul4.entity.*;
import com.codegym.casestudymodul4.service.SalesReportService;
import com.codegym.casestudymodul4.service.StoreService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StoreController {
  private final StoreService store;
  private final SalesReportService salesReportService;

  public StoreController(StoreService store, SalesReportService salesReportService) {
    this.store = store;
    this.salesReportService = salesReportService;
  }

  @ModelAttribute("cartCount")
  public int cartCount(HttpSession s) {
    Map<Long, Integer> c = cart(s);
    return c.values().stream().mapToInt(Integer::intValue).sum();
  }

  @SuppressWarnings("unchecked")
  private Map<Long, Integer> cart(HttpSession s) {
    Object data = s.getAttribute("cart");
    if (data instanceof Map<?, ?>) return (Map<Long, Integer>) data;
    Map<Long, Integer> result = new LinkedHashMap<>();
    s.setAttribute("cart", result);
    return result;
  }

  @GetMapping({"/", "/products"})
  public String home(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Long category,
      @RequestParam(required = false) BigDecimal min,
      @RequestParam(required = false) BigDecimal max,
      Model model) {
    model.addAttribute("products", store.search(keyword, category, min, max));
    model.addAttribute("categories", store.allCategories());
    model.addAttribute("keyword", keyword);
    model.addAttribute("selectedCategory", category);
    model.addAttribute("min", min);
    model.addAttribute("max", max);
    return "home";
  }

  @GetMapping("/products/{id}")
  public String product(@PathVariable Long id, Model model) {
    Product p = store.product(id).orElseThrow();
    model.addAttribute("product", p);
    return "product-detail";
  }

  @GetMapping("/login")
  public String login(@RequestParam(required = false) String error, Model model) {
    if (error != null)
      model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu chưa chính xác.");
    return "login";
  }

  @GetMapping("/register")
  public String registerForm(Model m) {
    m.addAttribute("message", null);
    return "register";
  }

  @PostMapping("/register")
  public String register(
      @RequestParam String username,
      @RequestParam String fullName,
      @RequestParam String email,
      @RequestParam String password,
      @RequestParam String confirmPassword,
      Model m) {
    if (!username.matches("[a-zA-Z0-9_]{3,30}")) {
      m.addAttribute("error", "Tên đăng nhập gồm 3–30 chữ cái, số hoặc dấu gạch dưới.");
      return "register";
    }
    if (password.length() < 6) {
      m.addAttribute("error", "Mật khẩu cần có ít nhất 6 ký tự.");
      return "register";
    }
    if (!password.equals(confirmPassword)) {
      m.addAttribute("error", "Mật khẩu xác nhận chưa khớp.");
      return "register";
    }
    try {
      store.register(username, password, email, fullName);
      m.addAttribute("message", "Tạo tài khoản thành công. Bạn có thể đăng nhập ngay.");
      return "login";
    } catch (IllegalArgumentException ex) {
      m.addAttribute("error", ex.getMessage());
      return "register";
    }
  }

  @PostMapping("/cart/items")
  public String addToCart(
      @RequestParam Long productId,
      @RequestParam(defaultValue = "1") int quantity,
      HttpSession session,
      RedirectAttributes ra) {
    Product p = store.product(productId).orElseThrow();
    Map<Long, Integer> c = cart(session);
    int next = c.getOrDefault(productId, 0) + Math.max(quantity, 1);
    if (next > p.getStock()) {
      ra.addFlashAttribute("error", "Số lượng vượt quá tồn kho hiện có.");
    } else {
      c.put(productId, next);
      ra.addFlashAttribute("success", "Đã thêm sản phẩm vào giỏ hàng.");
    }
    return "redirect:/";
  }

  @GetMapping("/cart")
  public String cartPage(HttpSession s, Model m) {
    List<Map<String, Object>> rows = new ArrayList<>();
    BigDecimal total = BigDecimal.ZERO;
    for (var row : cart(s).entrySet()) {
      Product p = store.product(row.getKey()).orElse(null);
      if (p != null) {
        BigDecimal line = p.getPrice().multiply(BigDecimal.valueOf(row.getValue()));
        rows.add(Map.of("product", p, "quantity", row.getValue(), "line", line));
        total = total.add(line);
      }
    }
    m.addAttribute("rows", rows);
    m.addAttribute("total", total);
    return "cart";
  }

  @PostMapping("/cart/items/{id}/quantity")
  public String quantity(
      @PathVariable Long id, @RequestParam int quantity, HttpSession s, RedirectAttributes ra) {
    Map<Long, Integer> c = cart(s);
    if (quantity < 1) c.remove(id);
    else {
      Product p = store.product(id).orElseThrow();
      if (quantity > p.getStock()) ra.addFlashAttribute("error", "Số lượng vượt quá tồn kho.");
      else c.put(id, quantity);
    }
    return "redirect:/cart";
  }

  @PostMapping("/cart/items/{id}/delete")
  public String remove(@PathVariable Long id, HttpSession s) {
    cart(s).remove(id);
    return "redirect:/cart";
  }

  @GetMapping("/checkout")
  public String checkoutPage(HttpSession s, Model m, Principal principal) {
    cartPage(s, m);
    m.addAttribute("account", store.account(principal.getName()).orElseThrow());
    return "checkout";
  }

  @PostMapping("/checkout")
  public String checkout(
      @RequestParam String fullName,
      @RequestParam String phone,
      @RequestParam String address,
      HttpSession s,
      Principal principal,
      Model m) {
    try {
      SalesOrder o =
          store.checkout(
              principal.getName(), fullName, phone, address, new LinkedHashMap<>(cart(s)));
      cart(s).clear();
      return "redirect:/orders/" + o.getId();
    } catch (IllegalArgumentException e) {
      cartPage(s, m);
      m.addAttribute("error", e.getMessage());
      m.addAttribute("account", store.account(principal.getName()).orElseThrow());
      return "checkout";
    }
  }

  @GetMapping("/orders")
  public String orders(Principal p, Model m) {
    m.addAttribute("orders", store.ordersFor(p.getName()));
    return "orders";
  }

  @GetMapping("/orders/{id}")
  public String order(@PathVariable Long id, Principal p, Model m) {
    SalesOrder o =
        store.allOrders().stream()
            .filter(x -> x.getId().equals(id) && x.getUsername().equals(p.getName()))
            .findFirst()
            .orElseThrow();
    m.addAttribute("order", o);
    return "order-detail";
  }

  @GetMapping("/profile")
  public String profile(Principal p, Model m) {
    m.addAttribute("account", store.account(p.getName()).orElseThrow());
    return "profile";
  }

  @PostMapping("/profile")
  public String profileSave(
      Principal p, @RequestParam String fullName, @RequestParam String email, Model m) {
    try {
      store.updateProfile(p.getName(), fullName, email);
      m.addAttribute("success", "Thông tin đã được cập nhật.");
    } catch (IllegalArgumentException e) {
      m.addAttribute("error", e.getMessage());
    }
    m.addAttribute("account", store.account(p.getName()).orElseThrow());
    return "profile";
  }

  @GetMapping("/admin")
  public String admin() {
    return "redirect:/admin/products";
  }

  @GetMapping("/admin/products")
  public String adminProducts(Model m) {
    m.addAttribute("products", store.allProducts());
    m.addAttribute("categories", store.allCategories());
    return "admin/products";
  }

  @GetMapping("/admin/products/new")
  public String newProduct(Model m) {
    m.addAttribute("product", new Product());
    m.addAttribute("categories", store.allCategories());
    return "admin/product-form";
  }

  @GetMapping("/admin/products/{id}/edit")
  public String editProduct(@PathVariable Long id, Model m) {
    m.addAttribute("product", store.product(id).orElseThrow());
    m.addAttribute("categories", store.allCategories());
    return "admin/product-form";
  }

  @PostMapping("/admin/products/save")
  public String saveProduct(
      @Valid @ModelAttribute Product product,
      BindingResult result,
      @RequestParam Long categoryId,
      Model m,
      RedirectAttributes ra) {
    if (result.hasErrors()) {
      m.addAttribute("categories", store.allCategories());
      return "admin/product-form";
    }
    Category c =
        store.allCategories().stream()
            .filter(x -> x.getId().equals(categoryId))
            .findFirst()
            .orElse(null);
    product.setCategory(c);
    store.saveProduct(product);
    ra.addFlashAttribute("success", "Đã lưu sản phẩm.");
    return "redirect:/admin/products";
  }

  @PostMapping("/admin/products/{id}/delete")
  public String deleteProduct(@PathVariable Long id) {
    store.deleteProduct(id);
    return "redirect:/admin/products";
  }

  @GetMapping("/admin/categories")
  public String adminCategories(Model m) {
    m.addAttribute("categories", store.allCategories());
    return "admin/categories";
  }

  @PostMapping("/admin/categories")
  public String saveCategory(
      @RequestParam String name,
      @RequestParam(defaultValue = "✦") String icon,
      RedirectAttributes ra) {
    store.saveCategory(new Category(name, icon));
    ra.addFlashAttribute("success", "Đã thêm danh mục.");
    return "redirect:/admin/categories";
  }

  @GetMapping("/admin/users")
  public String adminUsers(Model m) {
    m.addAttribute("users", store.allUsers());
    return "admin/users";
  }

  @PostMapping("/admin/users/{id}/toggle")
  public String toggleUser(@PathVariable Long id, @RequestParam boolean enabled) {
    store.updateUserStatus(id, enabled);
    return "redirect:/admin/users";
  }

  @GetMapping("/admin/orders")
  public String adminOrders(Model m) {
    m.addAttribute("orders", store.allOrders());
    return "admin/orders";
  }

  @GetMapping("/admin/reports")
  public String salesReport(
      @RequestParam(defaultValue = "month") String period,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day,
      @RequestParam(required = false) String month,
      @RequestParam(required = false) Integer quarter,
      @RequestParam(required = false) Integer year,
      Model model) {
    model.addAttribute(
        "report", salesReportService.generate(period, day, month, quarter, year));
    return "admin/reports";
  }

  @PostMapping("/admin/orders/{id}/status")
  public String orderStatus(@PathVariable Long id, @RequestParam String status) {
    store.updateOrderStatus(id, status);
    return "redirect:/admin/orders";
  }
}
