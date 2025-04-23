package edu.ucsb.cs156.example.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.ucsb.cs156.example.entities.RecommendationRequest;

public interface RecommendationRequestRepository 
       extends JpaRepository<RecommendationRequest, Long> {

    // This interface is intentionally empty. It inherits methods from JpaRepository.
    // You can add custom query methods here if needed.

    }
