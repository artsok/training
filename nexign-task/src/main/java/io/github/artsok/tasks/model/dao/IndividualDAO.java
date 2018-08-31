package io.github.artsok.tasks.model.dao;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

/**
 * DAO is an abbreviation for Data Access Object, so it should encapsulate the logic for retrieving,
 * saving and updating data in your data storage (a database, a file-system, whatever).
 * Here is an example how the DAO and DTO interfaces would look like:
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualDAO {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "BIRTHDAY", nullable = false)
    private LocalDate birthday;

    @Column(name = "PERSONALID", unique = true, nullable = false)
    private Long personalId;
}
