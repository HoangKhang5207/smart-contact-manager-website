package com.scm.controllers;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.forms.ContactSearchForm;
import com.scm.helpers.AppConstants;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.ContactService;
import com.scm.services.UserService;
import com.scm.services.ImageService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    private final ContactService contactService;
    private final UserService userService;

    private Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ImageService imageService;

    public ContactController(ContactService contactService, UserService userService) {
        this.contactService = contactService;
        this.userService = userService;
    }

    // add contact page: handler
    @GetMapping("/add")
    public String addContactView(Model model) {
        ContactForm contactForm = new ContactForm();
        contactForm.setFavorite(true);
        model.addAttribute("contactForm", contactForm);

        return "user/add_contact";
    }

    @PostMapping("/add")
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult bindingResult,
            Authentication authentication, HttpSession session) {

        // process form data
        String username = Helper.getEmailOfLoggedInUser(authentication);

        // validate form
        // TODO: add validation logic here
        if (bindingResult.hasErrors()) {
            session.setAttribute("message", Message.builder()
                    .content("Please correct the following error message")
                    .type(MessageType.red)
                    .build());
            return "user/add_contact";
        }

        // form -> contact
        User user = userService.getUserByEmail(username);

        // process the contact picture

        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setDescription(contactForm.getDescription());
        contact.setAddress(contactForm.getAddress());
        contact.setFavorite(contactForm.isFavorite());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        contact.setUser(user);

        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            // image processing
            logger.info("file information: " + contactForm.getContactImage().getOriginalFilename());
            String filename = UUID.randomUUID().toString();
            String fileURL = imageService.uploadImage(contactForm.getContactImage(), filename);
            contact.setPicture(fileURL);
            contact.setCloudinaryImagePublicId(filename);
        }

        // save to database
        contactService.save(contact);
        System.out.println(contactForm);

        // set the contact picture url

        // set message to be displayed on the view

        session.setAttribute("message", Message.builder()
                .content("You have successfully added a new contact")
                .type(MessageType.green)
                .build());

        return "redirect:/user/contacts/add";
    }

    // view contacts
    @GetMapping("/")
    public String viewContacts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model, Authentication authentication) {

        // load all the user contacts
        String username = Helper.getEmailOfLoggedInUser(authentication);

        User user = userService.getUserByEmail(username);

        Page<Contact> pageContact = contactService.getByUser(user, page, size, sortBy, direction);

        model.addAttribute("contacts", pageContact);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
        model.addAttribute("contactSearchForm", new ContactSearchForm());

        return "user/contacts";
    }

    // search handler
    @GetMapping("/search")
    public String searchHandler(
            @ModelAttribute ContactSearchForm contactSearchForm,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model,
            Authentication authentication) {

        logger.info("field {} - keyword {}", contactSearchForm.getField(), contactSearchForm.getValue());

        var user = userService.getUserByEmail(Helper.getEmailOfLoggedInUser(authentication));

        Page<Contact> pageContact = null;
        if (contactSearchForm.getField().equalsIgnoreCase("name")) {
            pageContact = contactService.searchByName(contactSearchForm.getValue(), page, size, sortBy, direction,
                    user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("email")) {
            pageContact = contactService.searchByEmail(contactSearchForm.getValue(), page, size, sortBy, direction,
                    user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("phone")) {
            pageContact = contactService.searchByPhoneNumber(contactSearchForm.getValue(), page, size, sortBy,
                    direction, user);
        }

        logger.info("pageContact: {}", pageContact);

        model.addAttribute("contactSearchForm", contactSearchForm);
        model.addAttribute("pageContact", pageContact);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

        return "user/search";
    }

    // delete contact
    @GetMapping("/delete/{contactId}")
    public String deleteContact(@PathVariable("contactId") String contactId, HttpSession session) {
        contactService.delete(contactId);
        logger.info("contactId {} deleted", contactId);

        session.setAttribute("message",
                Message.builder()
                        .content("You have successfully deleted a contact")
                        .type(MessageType.green)
                        .build());

        return "redirect:/user/contacts/";
    }

    // update the contact form view
    @GetMapping("/view/{contactId}")
    public String updateContactFormView(@PathVariable String contactId, Model model) {

        var contact = contactService.getById(contactId);

        ContactForm contactForm = new ContactForm();
        contactForm.setName(contact.getName());
        contactForm.setEmail(contact.getEmail());
        contactForm.setPhoneNumber(contact.getPhoneNumber());
        contactForm.setAddress(contact.getAddress());
        contactForm.setDescription(contact.getDescription());
        contactForm.setFavorite(contact.isFavorite());
        contactForm.setWebsiteLink(contact.getWebsiteLink());
        contactForm.setLinkedInLink(contact.getLinkedInLink());
        contactForm.setPicture(contact.getPicture());

        model.addAttribute("contactForm", contactForm);
        model.addAttribute("contactId", contactId);

        return "user/update_contact_view";
    }

    @PostMapping("/update/{contactId}")
    public String updateContact(@PathVariable("contactId") String contactId,
            @Valid @ModelAttribute("contactForm") ContactForm contactForm,
            BindingResult bindingResult, Model model) {

        // update the contact
        if (bindingResult.hasErrors()) {
            return "user/update_contact_view";
        }

        var con = contactService.getById(contactId);
        con.setId(contactId);
        con.setName(contactForm.getName());
        con.setEmail(contactForm.getEmail());
        con.setPhoneNumber(contactForm.getPhoneNumber());
        con.setAddress(contactForm.getAddress());
        con.setDescription(contactForm.getDescription());
        con.setFavorite(contactForm.isFavorite());
        con.setWebsiteLink(contactForm.getWebsiteLink());
        con.setLinkedInLink(contactForm.getLinkedInLink());

        // process image
        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            String filename = UUID.randomUUID().toString();
            String imageUrl = imageService.uploadImage(contactForm.getContactImage(), filename);
            con.setCloudinaryImagePublicId(filename);
            con.setPicture(imageUrl);
            contactForm.setPicture(imageUrl);
            logger.info("File is not empty");
        } else {
            logger.info("File is empty");
        }

        var updateCon = contactService.update(con);
        logger.info("updated contact {}", updateCon);
        model.addAttribute("message", Message.builder().content("Contact updated").type(MessageType.green));

        return "redirect:/user/contacts/view/" + contactId;
    }
}
