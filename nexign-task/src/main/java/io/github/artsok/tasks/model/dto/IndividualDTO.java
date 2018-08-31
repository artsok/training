package io.github.artsok.tasks.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualDTO implements ClientDTO {
    private Integer id;
    private String name;
    private String email;
    private LocalDate birthday;
    private Long personalId;
}
