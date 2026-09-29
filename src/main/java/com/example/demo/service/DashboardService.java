package com.example.demo.service;
import java.math.BigDecimal; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.DashboardStatsResponse; import com.example.demo.repository.*;
@Service public class DashboardService {
 private final ActorService actors; private final UserRepository users; private final ProductRepository products; private final OrderRepository orders; private final ReviewRepository reviews; private final ReportRepository reports;
 public DashboardService(ActorService a,UserRepository u,ProductRepository p,OrderRepository o,ReviewRepository r,ReportRepository x){actors=a;users=u;products=p;orders=o;reviews=r;reports=x;}
 @Transactional(readOnly=true) public DashboardStatsResponse stats(java.util.UUID id){actors.requireRole(id,"ADMIN"); BigDecimal sales=orders.findAll().stream().map(o->o.getTotalAmount()).reduce(BigDecimal.ZERO,BigDecimal::add); return new DashboardStatsResponse(users.count(),users.countByActiveTrue(),users.countByRole("FARMER"),products.count(),orders.count(),reviews.count(),reports.countByStatus("OPEN"),sales);}
}
