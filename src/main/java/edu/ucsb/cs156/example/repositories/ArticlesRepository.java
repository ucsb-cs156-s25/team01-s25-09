package edu.ucsb.cs156.example.repositories;

import edu.ucsb.cs156.example.entities.Articles;

import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Articles entity
 */
public interface ArticlesRepository extends CrudRepository<Articles, Long> {
}