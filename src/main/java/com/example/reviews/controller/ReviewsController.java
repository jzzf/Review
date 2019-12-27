package com.example.reviews.controller;

import com.example.reviews.domain.Product;
import com.example.reviews.domain.Review;
import com.example.reviews.model.ReviewDto;
import com.example.reviews.repository.ProductRepository;
import com.example.reviews.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ReviewsController {

    public final ProductRepository productRepository;

    public final ReviewRepository reviewRepository;

    @Autowired
    public ReviewsController(ProductRepository productRepository, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }

    /**
     * Creates a review for a product
     *
     * 1. Add argument for review entity. Use {@link RequestBody} annotation.
     * 2. Check for existence of product.
     * 3. If product no found, return NOT_FOUND,
     * 4. If found, save review.
     *
     * @param productId The id of the product.
     * @return The created review or 404 if product is not found.
     */
    @RequestMapping(value = "/reviews/products/{productId}", method = RequestMethod.POST)
    public ResponseEntity<Review> createReviewForProduct(
            @PathVariable("productId") Integer productId, @RequestBody ReviewDto reviewDto) {
        Optional<Product> product = productRepository.findById(productId);

        if (product.isPresent()) {
            Product value = product.get();

            Review review = new Review();
            review.setAuthorName(reviewDto.getAuthorName());
            review.setContent(reviewDto.getContent());
            review.setProduct(value);

            value.getReviews().add(review);

            productRepository.saveAndFlush(value);

            return new ResponseEntity<>(null, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    /**
     * Lists reviews by product.
     *
     * @param productId the id of the product.
     * @return The list of reviews.
     */
    @RequestMapping(value = "/reviews/products/{productId}", method = RequestMethod.GET)
    public ResponseEntity<List<Review>> listReviewsForProduct(@PathVariable("productId") Integer productId) {
        List<Review> reviews = reviewRepository.findAllByProductId(productId);

        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }
}
