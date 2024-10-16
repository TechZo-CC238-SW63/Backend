package com.techzo.cambiazo.exchanges.interfaces.rest;

import com.techzo.cambiazo.exchanges.domain.model.queries.GetAllReviewsByUserReceptorIdQuery;
import com.techzo.cambiazo.exchanges.domain.model.queries.GetAverageRatingUserQuery;
import com.techzo.cambiazo.exchanges.domain.services.IReviewCommandService;
import com.techzo.cambiazo.exchanges.domain.services.IReviewQueryService;
import com.techzo.cambiazo.exchanges.interfaces.rest.resources.CreateReviewResource;
import com.techzo.cambiazo.exchanges.interfaces.rest.resources.ReviewAverageRatingResource;
import com.techzo.cambiazo.exchanges.interfaces.rest.resources.ReviewResource;
import com.techzo.cambiazo.exchanges.interfaces.rest.transform.CreateReviewCommandFromResourceAssembler;
import com.techzo.cambiazo.exchanges.interfaces.rest.transform.ReviewResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v2/reviews")
@Tag(name = "Reviews", description = "Reviews Management Endpoints")
public class ReviewController {

    private final IReviewCommandService reviewCommandService;

    private final IReviewQueryService reviewQueryService;

    public ReviewController(IReviewCommandService reviewCommandService, IReviewQueryService reviewQueryService) {
        this.reviewCommandService = reviewCommandService;
        this.reviewQueryService = reviewQueryService;
    }

    @Operation(summary = "Create a new Review", description = "Create a new Review with the input data.")
    @PostMapping
    public ResponseEntity<ReviewResource>createReview(@RequestBody CreateReviewResource resource) {
        try {
            var createReviewCommand = CreateReviewCommandFromResourceAssembler.toCommandFromResource(resource);
            var review = reviewCommandService.handle(createReviewCommand);
            var reviewResource = ReviewResourceFromEntityAssembler.toResourceFromEntity(review.get());
            return ResponseEntity.status(CREATED).body(reviewResource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user-receptor/{userId}")
    public ResponseEntity<List<ReviewResource>> getAllReviewsByUserReceptorId(@PathVariable Long userId) {
        try {
            var getAllReviewsByUserReceptorIdQuery = new GetAllReviewsByUserReceptorIdQuery(userId);
            var reviews = reviewQueryService.handle(getAllReviewsByUserReceptorIdQuery);
            var reviewResource = reviews.stream()
                    .map(ReviewResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();
            return ResponseEntity.ok(reviewResource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        try {
            reviewCommandService.handleDeleteReview(reviewId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/average-rating/{userId}")
    public ResponseEntity<ReviewAverageRatingResource> getAverageRatingByUserReceptorId(@PathVariable Long userId) {
        try {
            var getAverageRatingUserQuery = new GetAverageRatingUserQuery(userId);
            var averageRating = reviewQueryService.getAverageRatingByUserReceptorId(getAverageRatingUserQuery);
            var reviewAverageRatingResource = new ReviewAverageRatingResource(averageRating);
            return ResponseEntity.ok(reviewAverageRatingResource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
