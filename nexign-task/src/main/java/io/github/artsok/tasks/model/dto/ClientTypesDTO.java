package io.github.artsok.tasks.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ClientTypesDTO {
    private Status status;
    private OrderBy orderBy;
    private TypeOfOwnerShip typeOfOwnership;
    private List<ClientDTO> result;

    private enum Status {
        SUCCESS, ERROR
    }

    private enum OrderBy {
        ASC, DESC
    }

    private enum TypeOfOwnerShip {
        INDIVIDUAL(1), ORGANIZATION(2);

        private final int code;

        TypeOfOwnerShip(int code) {
            this.code = code;
        }
    }
}


