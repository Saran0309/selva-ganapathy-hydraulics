package com.sgh.selvaganapathyhydraulics.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sgh.selvaganapathyhydraulics.entity.MachineDetails;

// Repository for accessing machine data in MongoDB
@Repository
public interface MachineDetailsRepository extends MongoRepository<MachineDetails, String> {
    List<MachineDetails> findByNameContainingIgnoreCaseAndParentTypeContainingIgnoreCase(String name, String parentType);
    Optional<MachineDetails> findByNameAndParentType(String name, String parentType);

    
}
