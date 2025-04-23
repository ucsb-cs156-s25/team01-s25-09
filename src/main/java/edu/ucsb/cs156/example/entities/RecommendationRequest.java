package edu.ucsb.cs156.example.entities;
// ^ idk why this is highlighted in red, but it is the correct path

import jakarta.persistence.*;
import lombok.*;

import java.lang.annotation.Inherited;
import java.time.LocalDateTime;




@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

// table name in the database
@Entity(name = "RecommendationRequest") 



public class RecommendationRequest {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private long id;

    private String requestorEmail;
    private String professorEmail;
    private String explanation;    // Would I need a limitation of length here?
    
    private LocalDateTime dateRequested;
    private LocalDateTime dateNeeded;

    private boolean done;

}
