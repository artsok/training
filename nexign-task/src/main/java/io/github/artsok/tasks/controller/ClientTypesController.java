package io.github.artsok.tasks.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/getClientTypes")
public class ClientTypesController {


    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object getClientTypes(@RequestParam(name = "typeOfOwnership") String type,
                                 @RequestParam(name = "orderBy", required = false) String orderBy) {

        System.out.println("" + orderBy + " " + type);

        return ResponseEntity.status(HttpStatus.OK).body("SUCCESS");
    }
}
