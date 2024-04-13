package com.example.BitespeedTask.Service;

import com.example.BitespeedTask.DTO.ContactRequest;
import com.example.BitespeedTask.DTO.ContactResponse;
import com.example.BitespeedTask.Exception.EmailNotFoundException;
import com.example.BitespeedTask.Exception.PhoneNumberNotFoundException;
import com.example.BitespeedTask.Model.Contact;
import com.example.BitespeedTask.Model.LinkPrecedence;
import com.example.BitespeedTask.Repository.ContactRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    @Autowired
    private ContactRepo contactRepo;

    public ContactResponse identifyContact(ContactRequest req) throws Exception {

        ContactResponse response=new ContactResponse();

        // To check whether the both email and phoneNumber associated with same row
        Optional<Contact> alreadyExist= Optional.ofNullable(contactRepo.isExist(req.getEmail(), req.getPhoneNumber()));

        // To get the Primary contact of phoneNumber
        Optional<Contact> contactsByPhone= Optional.ofNullable(contactRepo.findByPhoneNumber(req.getPhoneNumber()));

        // To get the Primary contact of email
        Optional<Contact> contactsByEmail= Optional.ofNullable(contactRepo.findByEmail(req.getEmail()));

        //email and phoneNumber associated with same row do nothing and return response
        if(alreadyExist.isPresent())
        {
            return generateResponse(alreadyExist.get().getLinkedId()==null?
                    alreadyExist.get().getId():alreadyExist.get().getLinkedId());
        }

        // if both email and phone does not exist create a new primary contact
        if(contactsByPhone.isEmpty() && contactsByEmail.isEmpty())
        {
            Contact contact=new Contact();
            contact.setPhoneNumber(req.getPhoneNumber());
            contact.setEmail(req.getEmail());
            contact.setLinkedId(null);
            contact.setLinkPrecedence(LinkPrecedence.PRIMARY);
            contact.setCreatedAt(new Date());
            contact.setUpdatedAt(new Date());
            contact.setDeletedAt(null);
            contactRepo.save(contact);

            return generateResponse(contact.getId());
        }
        // if both email and password already exist
        else if (contactsByPhone.isPresent() && contactsByEmail.isPresent())
        {
            // if both email and password have different primary contacts, migrate all the linked id
            // of later created primary account to older one
            if(contactsByPhone.get().getLinkPrecedence().equals(LinkPrecedence.PRIMARY)
                    && contactsByEmail.get().getLinkPrecedence().equals(LinkPrecedence.PRIMARY))
            {
                Integer linkedId=0;
                if(contactsByPhone.get().getCreatedAt().before(contactsByEmail.get().getCreatedAt()))
                {
                    for(Contact contact:contactRepo.findByLinkedId(contactsByEmail.get().getId()))
                    {
                        contact.setLinkedId(contactsByPhone.get().getId());
                        contact.setLinkPrecedence(LinkPrecedence.SECONDARY);
                        contact.setUpdatedAt(new Date());
                        contactRepo.save(contact);
                    }
                    contactsByEmail.get().setUpdatedAt(new Date());
                    contactsByEmail.get().setLinkPrecedence(LinkPrecedence.SECONDARY);
                    contactsByEmail.get().setLinkedId(contactsByPhone.get().getId());
                    contactRepo.save(contactsByEmail.get());
                    linkedId=contactsByPhone.get().getId();
                }
                else {
                    for(Contact contact:contactRepo.findByLinkedId(contactsByPhone.get().getId()))
                    {
                        contact.setLinkedId(contactsByEmail.get().getId());
                        contact.setLinkPrecedence(LinkPrecedence.SECONDARY);
                        contact.setUpdatedAt(new Date());
                        contactRepo.save(contact);
                    }
                    contactsByPhone.get().setUpdatedAt(new Date());
                    contactsByPhone.get().setLinkPrecedence(LinkPrecedence.SECONDARY);
                    contactsByPhone.get().setLinkedId(contactsByEmail.get().getId());
                    contactRepo.save(contactsByPhone.get());
                    linkedId=contactsByEmail.get().getId();
                }

                Contact contact=new Contact();
                contact.setPhoneNumber(req.getPhoneNumber());
                contact.setEmail(req.getEmail());
                contact.setLinkedId(linkedId);
                contact.setLinkPrecedence(LinkPrecedence.SECONDARY);
                contact.setCreatedAt(new Date());
                contact.setUpdatedAt(new Date());
                contact.setDeletedAt(null);
                contactRepo.save(contact);
                return generateResponse(linkedId);
            }
            else
            {
                Integer linkedId=0;
                Contact contact=new Contact();
                contact.setPhoneNumber(req.getPhoneNumber());
                contact.setEmail(req.getEmail());

                if(contactsByEmail.get().getLinkedId()==null)
                    linkedId=contactsByEmail.get().getId();
                else
                    linkedId=contactsByPhone.get().getId();

                contact.setLinkedId(linkedId);
                contact.setLinkPrecedence(LinkPrecedence.SECONDARY);
                contact.setCreatedAt(new Date());
                contact.setUpdatedAt(new Date());
                contact.setDeletedAt(null);
                contactRepo.save(contact);
                return generateResponse(contact.getLinkedId());
            }
        }
        // if phone already exist in contact, just set the linked-id to primary account id
        else if (contactsByPhone.isPresent())
        {
            Contact contact=new Contact();
            contact.setPhoneNumber(req.getPhoneNumber());
            contact.setEmail(req.getEmail());
            contact.setLinkedId(contactsByPhone.get().getLinkedId()==null?
                    contactsByPhone.get().getId():contactsByPhone.get().getLinkedId());
            contact.setLinkPrecedence(LinkPrecedence.SECONDARY);
            contact.setCreatedAt(new Date());
            contact.setUpdatedAt(new Date());
            contact.setDeletedAt(null);
            contactRepo.save(contact);
            return generateResponse(contact.getLinkedId());
        }
        // if email already exist in contact, just set the linked-id to primary account id
        else {
            Contact contact=new Contact();
            contact.setPhoneNumber(req.getPhoneNumber());
            contact.setEmail(req.getEmail());
            contact.setLinkedId(contactsByEmail.get().getLinkedId()==null?
                    contactsByEmail.get().getId():contactsByEmail.get().getLinkedId());
            contact.setLinkPrecedence(LinkPrecedence.SECONDARY);
            contact.setCreatedAt(new Date());
            contact.setUpdatedAt(new Date());
            contact.setDeletedAt(null);
            contactRepo.save(contact);
            return generateResponse(contact.getLinkedId());
        }
    }
    // To generate the response
    public ContactResponse generateResponse(Integer linkedID)
    {
        ContactResponse response=new ContactResponse();
        Optional<Contact> contact=contactRepo.findById(linkedID);
        response.getEmails().addFirst(contact.get().getEmail());
        response.getPhoneNumbers().addFirst(contact.get().getPhoneNumber());
        response.setPrimaryContactId(linkedID);
        Optional<List<Contact>> contacts= Optional.ofNullable(contactRepo.findByLinkedId(linkedID));

        for(Contact cont:contacts.get())
        {
            if(!response.getEmails().contains(cont.getEmail()))
                response.getEmails().addLast(cont.getEmail());
            if(!response.getPhoneNumbers().contains(cont.getPhoneNumber()))
                response.getPhoneNumbers().addLast(cont.getPhoneNumber());
            if(!response.getSecondaryContactIds().contains(cont.getId()))
                response.getSecondaryContactIds().add(cont.getId());

        }
        return response;
    }
}