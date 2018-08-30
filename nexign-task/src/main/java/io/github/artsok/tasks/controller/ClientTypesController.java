package io.github.artsok.tasks.controller;

import io.github.artsok.tasks.repository.IndividualRepository;
import io.github.artsok.tasks.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/getClientTypes")
public class ClientTypesController {

    private static final int INDIVIDUAL_OWNERSHIP = 1;
    private static final int ORGANIZATION_OWNERSHIP = 2;

    @Autowired
    private IndividualRepository individualRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getClientTypes(@RequestParam(name = "typeOfOwnership") Integer type,
                                 @RequestParam(name = "orderBy", required = false) String orderBy) {


        System.out.println("Размер " + individualRepository.findAll().size());

        if (INDIVIDUAL_OWNERSHIP == type) {
            return ResponseEntity.status(HttpStatus.OK).body(individualRepository.findAll());
        } else if (ORGANIZATION_OWNERSHIP == type) {
            return ResponseEntity.status(HttpStatus.OK).body(organizationRepository.findAll());
        } else {
            throw new IllegalArgumentException("Не заданы требуемые параметры");
        }
    }
}
