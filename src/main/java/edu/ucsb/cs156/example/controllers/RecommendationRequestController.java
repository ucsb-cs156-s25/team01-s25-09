package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.RecommendationRequest;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.RecommendationRequestRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;

/**
 * REST controller for RecommendationRequest
 */

@Tag(name = "RecommendationRequest")
@RequestMapping("/api/RecommendationRequest")
@RestController
@Slf4j
public class RecommendationRequestController extends ApiController {

    @Autowired
    RecommendationRequestRepository recommendationRequestRepository;

    /**
     * List all RecommendationRequests in the database.
     *
     * @return iterable of RecommendationRequest
     */
    @Operation(summary = "List all recommendation requests")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<RecommendationRequest> index() {
        return recommendationRequestRepository.findAll();
    }

    /**
     * Get a single RecommendationRequest by id.
     *
     * @param id primary-key id of the request
     * @return the RecommendationRequest
     */
    @Operation(summary = "Get a single recommendation request by id")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public RecommendationRequest getById(
            @Parameter(name = "id") @RequestParam Long id) {

        return recommendationRequestRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(RecommendationRequest.class, id));
    }

    /**
     * Create a new RecommendationRequest.
     *
     * @param requesterEmail email of the student requesting the letter
     * @param professorEmail email of the professor
     * @param explanation    reason for the request
     * @param dateRequested  when the request was made (ISO-8601)
     * @param dateNeeded     deadline for the letter (ISO-8601)
     * @param done           completion flag
     * @return saved RecommendationRequest
     */
    @Operation(summary = "Create a new recommendation request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public RecommendationRequest create(
            @Parameter(name = "requesterEmail")  @RequestParam String requesterEmail,
            @Parameter(name = "professorEmail")  @RequestParam String professorEmail,
            @Parameter(name = "explanation")     @RequestParam String explanation,
            @Parameter(name = "dateRequested",
                       description = "ISO-8601 date/time")
                @RequestParam("dateRequested")
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateRequested,
            @Parameter(name = "dateNeeded",
                       description = "ISO-8601 date/time")
                @RequestParam("dateNeeded")
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateNeeded,
            @Parameter(name = "done")            @RequestParam boolean done) {

        RecommendationRequest rr = RecommendationRequest.builder()
                .requesterEmail(requesterEmail)
                .professorEmail(professorEmail)
                .explanation(explanation)
                .dateRequested(dateRequested)
                .dateNeeded(dateNeeded)
                .done(done)
                .build();

        return recommendationRequestRepository.save(rr);
    }
    /**
     * Delete a RecommendationRequest.
     *
     * @param id primary-key id of the request
     * @return generic message confirming deletion
     */
    @Operation(summary = "Delete a recommendation request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteRecommendationRequest(
            @Parameter(name = "id") @RequestParam Long id) {

        RecommendationRequest rr = recommendationRequestRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(RecommendationRequest.class, id));

        recommendationRequestRepository.delete(rr);
        return genericMessage("RecommendationRequest with id %s deleted".formatted(id));
    }

    // ──────────────────────────────────────────────────────────────
    // UPDATE (PUT /api/RecommendationRequest?id=…)
    // ──────────────────────────────────────────────────────────────
    /**
     * Update an existing RecommendationRequest.
     *
     * @param id       id of the request to update
     * @param incoming updated data
     * @return updated RecommendationRequest
     */
    @Operation(summary = "Update a recommendation request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public RecommendationRequest updateRecommendationRequest(
            @Parameter(name = "id") @RequestParam Long id,
            @RequestBody @Valid RecommendationRequest incoming) {

        RecommendationRequest rr = recommendationRequestRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(RecommendationRequest.class, id));

        rr.setRequesterEmail(incoming.getRequesterEmail());
        rr.setProfessorEmail(incoming.getProfessorEmail());
        rr.setExplanation(incoming.getExplanation());
        rr.setDateRequested(incoming.getDateRequested());
        rr.setDateNeeded(incoming.getDateNeeded());
        rr.setDone(incoming.isDone());

        return recommendationRequestRepository.save(rr);
    }
}
