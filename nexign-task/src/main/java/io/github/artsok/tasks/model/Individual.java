package io.github.artsok.tasks.model;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Individual {
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
