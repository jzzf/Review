package com.example.reviews.controller;

import com.example.reviews.domain.Comment;
import com.example.reviews.domain.Review;
import com.example.reviews.model.CommentDto;
import com.example.reviews.repository.CommentRepository;
import com.example.reviews.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/comments")
public class CommentsController {

    private final CommentRepository commentRepository;

    private final ReviewRepository reviewRepository;

    @Autowired
    public CommentsController(CommentRepository commentRepository, ReviewRepository reviewRepository) {
        this.commentRepository = commentRepository;
        this.reviewRepository = reviewRepository;
    }

    /**
     * Creates a comment for a review.
     *
     * 1. Add argument for comment entity. Use {@link RequestBody} annotation.
     * 2. Check for existence of review.
     * 3. If review not found, return NOT_FOUND.
     * 4. If found, save comment.
     *
     * @param reviewId The id of the review.
     */
    @RequestMapping(value = "/reviews/{reviewId}", method = RequestMethod.POST)
    public ResponseEntity<Comment> createCommentForReview(
            @PathVariable("reviewId") Integer reviewId, @RequestBody CommentDto commentDto) {
        Optional<Review> review = reviewRepository.findById(reviewId);

        if (review.isPresent()) {
            Review value = review.get();

            Comment comment = new Comment();
            comment.setAuthorName(commentDto.getAuthorName());
            comment.setContent(commentDto.getContent());
            comment.setReview(value);
            comment = commentRepository.saveAndFlush(comment);

            review.get().getComments().add(comment);

            reviewRepository.saveAndFlush(value);

            return new ResponseEntity<>(comment, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    /**
     * List comments for a review.
     *
     * 1. Check for existence of review.
     * 2. If review not found, return NOT_FOUND.
     * 3. If found, return list of comments.
     *
     * @param reviewId The id of the review.
     */
    @RequestMapping(value = "/reviews/{reviewId}", method = RequestMethod.GET)
    public ResponseEntity<List<Comment>> listCommentsForReview(@PathVariable("reviewId") Integer reviewId) {
        Optional<Review> review = reviewRepository.findById(reviewId);

        if (!review.isPresent()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);
        }

        List<Comment> comments = commentRepository.findAllByReviewId(reviewId);

        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
}
