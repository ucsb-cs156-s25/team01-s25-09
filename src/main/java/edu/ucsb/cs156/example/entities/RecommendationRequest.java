package edu.ucsb.cs156.example.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * This is a JPA entity that represents a recommendation request.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "recommendationrequests")
public class RecommendationRequest {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String requesterEmail;
    private String professorEmail;
    private String explanation;
    
    private LocalDateTime dateRequested;
    private LocalDateTime dateNeeded;

    private boolean done;

}
