package com.scm.services;

import com.scm.entities.Contact;
import com.scm.entities.User;

import java.util.List;

import org.springframework.data.domain.Page;

public interface ContactService {
    // save contact
    Contact save(Contact contact);

    // update contact
    Contact update(Contact contact);

    // get contacts
    List<Contact> getAll();

    // get contact by id
    Contact getById(String id);

    // delete contact
    void delete(String id);

    // search contact
    Page<Contact> searchByName(String nameKeyword, int page, int size, String sortBy, String order, User user);

    Page<Contact> searchByEmail(String emailKeyword, int page, int size, String sortBy, String order, User user);

    Page<Contact> searchByPhoneNumber(String phoneNumberKeyword, int page, int size, String sortBy, String order, User user);

    // get contact by userId
    List<Contact> getByUserId(String userId);

    // get contact by user
    Page<Contact> getByUser(User user, int page, int size, String sortBy, String direction);
}
