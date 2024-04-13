package com.example.BitespeedTask.DTO;

import lombok.Data;

import java.util.*;

@Data
public class ContactResponse {
    private int primaryContactId;  // Id of primary contact of a user
    private Deque<String> emails=new LinkedList<>();  // Set of emails of user, first element being email of primary contact
    private Deque<String> phoneNumbers=new LinkedList<>();  // Set of contactNumber of user, first element being phoneNumber of primary contact
    private Set<Integer> secondaryContactIds=new HashSet<>();  // set of secondary contact id's
}
