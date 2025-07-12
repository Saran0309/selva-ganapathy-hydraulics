package com.sgh.selvaganapathyhydraulics.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import com.sgh.selvaganapathyhydraulics.entity.MachineDetails;

// Repository for accessing machine data in MongoDB
@Repository
@EnableMongoRepositories
public interface MachineDetailsRepository extends MongoRepository<MachineDetails, String> {

        List<MachineDetails> findByNameIgnoreCaseAndParentTypeIgnoreCase(String name, String parentType);
        List<MachineDetails> findByParentType(String type);



}
