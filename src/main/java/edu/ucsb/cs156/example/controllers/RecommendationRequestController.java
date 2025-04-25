package edu.ucsb.cs156.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import edu.ucsb.cs156.example.entities.RecommendationRequest;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.RecommendationRequestRepository;

import java.time.LocalDate;

@Tag(name = "RecommendationRequest")
@RequestMapping("/api/recommendationrequests")
@RestController
@Slf4j
public class RecommendationRequestController extends ApiController {

    @Autowired
    RecommendationRequestRepository recommendationRequestRepository;

    @Operation(summary = "List all recommendation requests")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<RecommendationRequest> allRecommendationRequests() {
        return recommendationRequestRepository.findAll();
    }

    @Operation(summary = "Create a new recommendation request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public RecommendationRequest postRecommendationRequest(
            @Parameter(name="requesterEmail")   @RequestParam String requesterEmail,
            @Parameter(name="professorEmail")   @RequestParam String professorEmail,
            @Parameter(name="explanation")      @RequestParam String explanation,
            @Parameter(name="dateRequested", description="YYYY-MM-DD")
            @RequestParam("dateRequested")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateRequested,
            @Parameter(name="dateNeeded", description="YYYY-MM-DD")
            @RequestParam("dateNeeded")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateNeeded,
            @Parameter(name="done")             @RequestParam boolean done
    ) {
        RecommendationRequest req = new RecommendationRequest();
        req.setRequesterEmail(requesterEmail);
        req.setProfessorEmail(professorEmail);
        req.setExplanation(explanation);
        req.setDateRequested(dateRequested.atStartOfDay());
        req.setDateNeeded(dateNeeded.atStartOfDay());
        req.setDone(done);

        return recommendationRequestRepository.save(req);
    }


    /**
     * Get a single date by id
     * 
     * @param id the id of the date
     * @return a UCSBDate
     */
    @Operation(summary= "Get a single date")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBDate getById(
            @Parameter(name="id") @RequestParam Long id) {
        UCSBDate ucsbDate = ucsbDateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDate.class, id));

        return ucsbDate;
    }




}
