package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.MenuItemReview;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.MenuItemReviewRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.time.LocalDateTime;

/**
* This is a REST controller for MenuItemReviewController
*/

@Tag(name = "MenuItemReview")
@RequestMapping("/api/menuitemreview")
@RestController
@Slf4j

public class MenuItemReviewController extends ApiController{

@Autowired
MenuItemReviewRepository menuItemReviewRepository;

/**
* List all Menu Item reviews
*
* @return an iterable of menu item reviews
*/
@Operation(summary= "List all Menu Item Reviews")
@PreAuthorize("hasRole('ROLE_USER')")
@GetMapping("/all")
public Iterable<MenuItemReview> allMenuItemReview() {
Iterable<MenuItemReview> menuItemReview = menuItemReviewRepository.findAll();
return menuItemReview;
}

/**
* Create a new Menu item review
*
* @param itemId ID of the item being reviewed
* @param reviewerEmail Email of the reviewer
* @param dateReviewed the date the review was made
* @param comments comments left by the reviewer
* @return the saved menu Item review
*/
@Operation(summary= "Create a new menu item review")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@PostMapping("/post")
public MenuItemReview postMenuItemReview(
@Parameter(name="itemId") @RequestParam long itemId,
@Parameter(name="reviewerEmail") @RequestParam String reviewerEmail,
@Parameter(name="stars") @RequestParam int stars,
@Parameter(name="comments") @RequestParam String comments,
@Parameter(name="dateReviewed", description="date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("dateReviewed") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateReviewed)
throws JsonProcessingException {

// For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
// See: https://www.baeldung.com/spring-date-parameters

log.info("dateReviewed={}", dateReviewed);

MenuItemReview menuItemReview = new MenuItemReview();
menuItemReview.setItemId(itemId);
menuItemReview.setReviewerEmail(reviewerEmail);
menuItemReview.setStars(stars);
menuItemReview.setDateReviewed(dateReviewed);
menuItemReview.setComments(comments);

MenuItemReview savedMenuItemReview = menuItemReviewRepository.save(menuItemReview);

return savedMenuItemReview;
}

 /**
     * Get a single date by id
     * 
     * @param id the id of the menu item review
     * @return a menu item review
     */
    @Operation(summary= "Get a single menu item review")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public MenuItemReview getById(
            @Parameter(name="id") @RequestParam Long id) {
        MenuItemReview menuItemReview = menuItemReviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MenuItemReview.class, id));

        return menuItemReview;
    }


}