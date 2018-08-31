package io.github.artsok.tasks.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDTO implements ClientDTO {
    private Long id;
    private String company;
    private Long orgId;
    private String country;
}
