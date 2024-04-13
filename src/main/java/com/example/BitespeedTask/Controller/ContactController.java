package com.example.BitespeedTask.Controller;

import com.example.BitespeedTask.DTO.ContactRequest;
import com.example.BitespeedTask.DTO.ContactResponse;
import com.example.BitespeedTask.Service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fluxKart/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping("/identify")
    public ResponseEntity<ContactResponse> identifyCustomer(@RequestBody ContactRequest req) throws Exception {
        ContactResponse response=contactService.identifyContact(req);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
