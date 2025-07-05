package com.sgh.selvaganapathyhydraulics.Repository;

import com.sgh.selvaganapathyhydraulics.entity.MachineDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Repository for accessing machine data in MongoDB
@Repository
public interface MachineDetailsRepository extends MongoRepository<MachineDetails, String> {
    List<MachineDetails> findByNameContainingIgnoreCaseAndParentTypeContainingIgnoreCase(String name, String parentType);
}
