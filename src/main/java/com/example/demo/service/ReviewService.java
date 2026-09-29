package com.example.demo.service;
import java.util.*; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.*; import com.example.demo.entity.*; import com.example.demo.exception.*; import com.example.demo.repository.*;
@Service public class ReviewService {
    private final ActorService actors; private final ProductRepository products; private final ReviewRepository reviews;
    public ReviewService(ActorService actors, ProductRepository products, ReviewRepository reviews) { this.actors=actors; this.products=products; this.reviews=reviews; }
    @Transactional(readOnly=true) public List<ReviewResponse> get(UUID productId) { if (!products.existsById(productId)) throw new ResourceNotFoundException("Product not found"); return reviews.findAllByProductIdOrderByCreatedAtDesc(productId).stream().map(this::response).toList(); }
    @Transactional public ReviewResponse create(UUID userId, UUID productId, ReviewRequest request) { User user=actors.requireUser(userId); Product p=product(productId); if (reviews.findByProductIdAndUserId(productId,userId).isPresent()) throw new ResourceAlreadyExistsException("You have already reviewed this product"); Review r=new Review(); r.setUser(user); r.setProduct(p); copy(r,request); return response(reviews.save(r)); }
    @Transactional public ReviewResponse update(UUID userId, UUID reviewId, ReviewRequest request) { Review r=review(reviewId); if(!r.getUser().getId().equals(userId)) throw new ForbiddenException("You can only manage your own review"); copy(r,request); return response(reviews.save(r)); }
    @Transactional public void delete(UUID userId, UUID reviewId) { Review r=review(reviewId); if(!r.getUser().getId().equals(userId)) throw new ForbiddenException("You can only manage your own review"); reviews.delete(r); }
    private Product product(UUID id) { return products.findById(id).orElseThrow(()->new ResourceNotFoundException("Product not found")); }
    private Review review(UUID id) { return reviews.findById(id).orElseThrow(()->new ResourceNotFoundException("Review not found")); }
    private void copy(Review r, ReviewRequest q) { r.setRating(q.rating()); r.setComment(q.comment()==null?null:q.comment().trim()); }
    private ReviewResponse response(Review r) { return new ReviewResponse(r.getId(),r.getProduct().getId(),r.getUser().getId(),r.getUser().getName(),r.getRating(),r.getComment(),r.getCreatedAt(),r.getUpdatedAt()); }
}
