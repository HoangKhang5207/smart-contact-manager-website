package com.scm.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scm.entities.Contact;
import com.scm.services.ContactService;

@RestController
@RequestMapping("/api")
public class ApiController {

    private ContactService contactService;

    public ApiController(ContactService contactService) {
        this.contactService = contactService;
    }

    // get contact
    @GetMapping("/contacts/{contactId}")
    public Contact getContact(@PathVariable String contactId) {
        return contactService.getById(contactId);
    }   
}
