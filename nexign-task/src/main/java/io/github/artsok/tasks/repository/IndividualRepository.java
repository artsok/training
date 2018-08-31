package io.github.artsok.tasks.repository;

import io.github.artsok.tasks.model.dao.IndividualDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndividualRepository extends JpaRepository<IndividualDAO, Integer> {

}
