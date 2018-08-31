package io.github.artsok.tasks.repository;

import io.github.artsok.tasks.dao.OrganizationDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Использую JpaRepository, т.к нужны методы сортиврокив отличии от интерфеса
 * CrudRepository {@link org.springframework.data.repository.CrudRepository}
 */
@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationDAO, Long> {
}
