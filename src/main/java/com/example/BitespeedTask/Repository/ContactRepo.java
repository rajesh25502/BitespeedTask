package com.example.BitespeedTask.Repository;

import com.example.BitespeedTask.Model.Contact;
import com.example.BitespeedTask.Model.LinkPrecedence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepo extends JpaRepository<Contact,Integer> {

    public List<Contact> findByLinkedId(Integer id);

    @Query("SELECT c FROM Contact c WHERE c.email=?1 AND c.phoneNumber=?2")
    public Contact isExist(@Param("email") String email,@Param("phone") String phone);

    @Query("SELECT c FROM Contact c WHERE c.phoneNumber=?1 ORDER BY c.createdAt LIMIT 1")
    public Contact findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    @Query("SELECT c FROM Contact c WHERE c.email=?1 ORDER BY c.createdAt LIMIT 1")
    public Contact findByEmail(@Param("email") String email);
}
