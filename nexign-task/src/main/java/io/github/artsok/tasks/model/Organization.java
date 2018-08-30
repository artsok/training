package io.github.artsok.tasks.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Organization {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String company;

    @Column(unique = true)
    private Long orgId;

    @Column(nullable = false)
    private String country;
}
