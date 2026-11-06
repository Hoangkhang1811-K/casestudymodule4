package com.codegym.casestudymodul4.service;

import com.codegym.casestudymodul4.entity.*;
import com.codegym.casestudymodul4.repository.*;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreService {
  private final ProductRepository products;
  private final CategoryRepository categories;
  private final CustomerAccountRepository accounts;
  private final SalesOrderRepository orders;
  private final PasswordEncoder encoder;

  public StoreService(
      ProductRepository products,
      CategoryRepository categories,
      CustomerAccountRepository accounts,
      SalesOrderRepository orders,
      PasswordEncoder encoder) {
    this.products = products;
    this.categories = categories;
    this.accounts = accounts;
    this.orders = orders;
    this.encoder = encoder;
  }

  @PostConstruct
  @Transactional
  public void seed() {
    if (categories.count() == 0) {
      Category decor = categories.save(new Category("Trang trí", "✦"));
      Category lighting = categories.save(new Category("Đèn & ánh sáng", "☼"));
      Category textile = categories.save(new Category("Vải & thảm", "▧"));
      Category kitchen = categories.save(new Category("Bếp & bàn ăn", "⌂"));
      products.saveAll(
          List.of(
              new Product(
                  "Bình gốm Mộc An",
                  "Bình gốm thủ công men mờ, dáng tối giản lấy cảm hứng từ những đường nét tự"
                      + " nhiên.",
                  new BigDecimal("680000"),
                  18,
                  "https://images.unsplash.com/photo-1578500494198-246f612d3b3d?auto=format&fit=crop&w=900&q=85",
                  "BÁN CHẠY",
                  "#efe6dc",
                  decor),
              new Product(
                  "Đèn bàn Ánh Trăng",
                  "Ánh sáng vàng dịu, chụp đèn vải linen và thân gỗ sồi tự nhiên.",
                  new BigDecimal("1250000"),
                  12,
                  "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=900&q=85",
                  "MỚI VỀ",
                  "#e8e9df",
                  lighting),
              new Product(
                  "Bình hoa Nắng Sớm",
                  "Gốm stoneware nung nhiệt cao, mỗi chiếc có sắc men độc bản.",
                  new BigDecimal("520000"),
                  21,
                  "https://images.unsplash.com/photo-1612196808214-b7e1d6145a8c?auto=format&fit=crop&w=900&q=85",
                  null,
                  "#eae3da",
                  decor),
              new Product(
                  "Thảm dệt Bờ Cát",
                  "Dệt tay từ sợi cotton tự nhiên, bền đẹp và dễ phối trong mọi không gian.",
                  new BigDecimal("1890000"),
                  8,
                  "https://images.unsplash.com/photo-1600166898405-da9535204843?auto=format&fit=crop&w=900&q=85",
                  "THỦ CÔNG",
                  "#e6e1d5",
                  textile),
              new Product(
                  "Khay gỗ Bình Yên",
                  "Gỗ tần bì bền chắc, hoàn thiện bằng dầu thực vật an toàn.",
                  new BigDecimal("420000"),
                  25,
                  "https://images.unsplash.com/photo-1603199506016-b9a594b593c0?auto=format&fit=crop&w=900&q=85",
                  null,
                  "#eee5d6",
                  kitchen),
              new Product(
                  "Gối tựa Nếp Nhà",
                  "Vỏ linen mềm mại dệt từ sợi tự nhiên, khóa kéo ẩn tinh tế.",
                  new BigDecimal("390000"),
                  16,
                  "https://images.unsplash.com/photo-1584100936595-c0654b55a2e2?auto=format&fit=crop&w=900&q=85",
                  null,
                  "#e9e3dc",
                  textile),
              new Product(
                  "Tách trà Thảnh Thơi",
                  "Bộ tách gốm hai chiếc với lớp men xanh rêu thanh nhã.",
                  new BigDecimal("350000"),
                  30,
                  "https://images.unsplash.com/photo-1514228742587-6b1558fcca3d?auto=format&fit=crop&w=900&q=85",
                  null,
                  "#e6ebe4",
                  kitchen),
              new Product(
                  "Đèn thả Mây Trôi",
                  "Đan thủ công từ mây tự nhiên, tạo điểm nhấn ấm áp cho căn phòng.",
                  new BigDecimal("2150000"),
                  6,
                  "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=900&q=85",
                  null,
                  "#f0e8dc",
                  lighting)));
    }
    if (accounts.count() == 0)
      accounts.save(
          new CustomerAccount(
              "admin", encoder.encode("admin123"), "admin@marketly.vn", "Quản trị viên", "ADMIN"));
  }

  public List<Product> search(String keyword, Long categoryId, BigDecimal min, BigDecimal max) {
    List<Product> found;
    if (keyword != null && !keyword.isBlank())
      found = products.findByNameContainingIgnoreCaseAndActiveTrue(keyword.trim());
    else if (categoryId != null) found = products.findByCategoryIdAndActiveTrue(categoryId);
    else found = products.findByActiveTrue();
    if (min != null)
      found =
          found.stream().filter(p -> p.getPrice().compareTo(min) >= 0).collect(Collectors.toList());
    if (max != null)
      found =
          found.stream().filter(p -> p.getPrice().compareTo(max) <= 0).collect(Collectors.toList());
    return found;
  }

  public List<Product> allProducts() {
    return products.findAll();
  }

  public List<Category> allCategories() {
    return categories.findAll();
  }

  public List<CustomerAccount> allUsers() {
    return accounts.findAll();
  }

  public List<SalesOrder> allOrders() {
    return orders.findAllByOrderByCreatedAtDesc();
  }

  public Optional<Product> product(Long id) {
    return products.findById(id);
  }

  public Optional<CustomerAccount> account(String username) {
    return accounts.findByUsername(username);
  }

  public List<SalesOrder> ordersFor(String username) {
    return orders.findByUsernameOrderByCreatedAtDesc(username);
  }

  @Transactional
  public Product saveProduct(Product p) {
    if (p.getCategory() != null && p.getCategory().getId() != null)
      p.setCategory(categories.findById(p.getCategory().getId()).orElse(null));
    return products.save(p);
  }

  @Transactional
  public void deleteProduct(Long id) {
    products
        .findById(id)
        .ifPresent(
            p -> {
              p.setActive(false);
              products.save(p);
            });
  }

  @Transactional
  public Category saveCategory(Category c) {
    return categories.save(c);
  }

  @Transactional
  public CustomerAccount register(String username, String password, String email, String fullName) {
    if (accounts.existsByUsername(username))
      throw new IllegalArgumentException("Tên đăng nhập đã được sử dụng.");
    if (accounts.existsByEmail(email)) throw new IllegalArgumentException("Email đã được sử dụng.");
    return accounts.save(
        new CustomerAccount(username, encoder.encode(password), email, fullName, "USER"));
  }

  @Transactional
  public void updateProfile(String username, String name, String email) {
    CustomerAccount a = accounts.findByUsername(username).orElseThrow();
    if (accounts.findAll().stream()
        .anyMatch(x -> x.getEmail().equalsIgnoreCase(email) && !x.getUsername().equals(username)))
      throw new IllegalArgumentException("Email đã được sử dụng.");
    a.setFullName(name);
    a.setEmail(email);
    accounts.save(a);
  }

  @Transactional
  public void updateUserStatus(Long id, boolean enabled) {
    CustomerAccount a = accounts.findById(id).orElseThrow();
    if (!a.getRole().equals("ADMIN")) a.setEnabled(enabled);
    accounts.save(a);
  }

  @Transactional
  public SalesOrder checkout(
      String username, String name, String phone, String address, Map<Long, Integer> cart) {
    if (cart == null || cart.isEmpty())
      throw new IllegalArgumentException("Giỏ hàng của bạn đang trống.");
    SalesOrder order =
        new SalesOrder("MLY-" + System.currentTimeMillis(), username, name, phone, address);
    BigDecimal total = BigDecimal.ZERO;
    for (var row : cart.entrySet()) {
      Product p =
          products
              .findById(row.getKey())
              .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không còn tồn tại."));
      int qty = row.getValue();
      if (qty < 1 || qty > p.getStock())
        throw new IllegalArgumentException("Số lượng " + p.getName() + " vượt quá tồn kho.");
      p.setStock(p.getStock() - qty);
      products.save(p);
      SalesOrderItem item = new SalesOrderItem(p.getId(), p.getName(), p.getPrice(), qty);
      order.addItem(item);
      total = total.add(item.getLineTotal());
    }
    if (total.compareTo(BigDecimal.valueOf(1_000_000)) < 0)
      total = total.add(BigDecimal.valueOf(35_000));
    order.setTotalAmount(total);
    return orders.save(order);
  }

  @Transactional
  public void updateOrderStatus(Long id, String status) {
    if (!List.of("Đang xử lý", "Đang giao", "Hoàn tất", "Đã hủy").contains(status))
      throw new IllegalArgumentException("Trạng thái đơn hàng không hợp lệ.");
    SalesOrder order = orders.findById(id).orElseThrow();
    order.setStatus(status);
    orders.save(order);
  }
}
