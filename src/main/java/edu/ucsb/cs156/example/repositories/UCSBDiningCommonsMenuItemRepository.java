package edu.ucsb.cs156.example.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItem;

/**
 * The UCSBDiningCommonsMenuItemRepository is a repository for UCSBDiningCommonsMenuItem entities.
 */

@Repository
public interface UCSBDiningCommonsMenuItemRepository extends CrudRepository<UCSBDiningCommonsMenuItem, Long> { 

}