package sv.project.e_commerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sv.project.e_commerce.dto.response.ApiResponse;
import sv.project.e_commerce.repository.OrderRepository;
import sv.project.e_commerce.repository.ProductRepository;
import sv.project.e_commerce.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Dashboard", description = "Thống kê cho Admin")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    UserRepository userRepository;
    ProductRepository productRepository;
    OrderRepository orderRepository;

    @GetMapping("/stats")
    @Operation(summary = "Lấy thống kê tổng quan")
    public ApiResponse<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalProducts", productRepository.count());
        stats.put("totalOrders", orderRepository.count());
        stats.put("totalRevenue", orderRepository.findAll().stream()
                .filter(o -> "COMPLETED".equals(o.getStatus().name()))
                .mapToDouble(o -> o.getTotalAmount())
                .sum());

        return ApiResponse.<Map<String, Object>>builder()
                .result(stats)
                .build();
    }

    @GetMapping("/revenue-monthly")
    @Operation(summary = "Lấy doanh thu theo tháng")
    public ApiResponse<List<Map<String, Object>>> getMonthlyRevenue() {
        // Simple logic to group revenue by month/year
        // In a real app, this would be a custom query in OrderRepository
        List<sv.project.e_commerce.model.entity.Order> completedOrders = orderRepository.findAll().stream()
                .filter(o -> "COMPLETED".equals(o.getStatus().name()))
                .toList();

        Map<String, Double> monthlyData = new TreeMap<>(); // TreeMap to keep chronological order if keys are
                                                           // YYYY-MM
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM");

        for (sv.project.e_commerce.model.entity.Order order : completedOrders) {
            String month = order.getCreatedAt().format(formatter);
            monthlyData.merge(month, order.getTotalAmount(), Double::sum);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        monthlyData.forEach((month, revenue) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("month", month);
            item.put("revenue", revenue);
            result.add(item);
        });

        return ApiResponse.<List<Map<String, Object>>>builder()
                .result(result)
                .build();
    }
}
