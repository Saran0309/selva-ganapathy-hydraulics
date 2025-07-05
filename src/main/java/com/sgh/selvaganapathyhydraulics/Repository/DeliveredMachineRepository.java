package com.sgh.selvaganapathyhydraulics.Repository;

import com.sgh.selvaganapathyhydraulics.entity.DeliveredMachines;
import com.sgh.selvaganapathyhydraulics.entity.MachineDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface DeliveredMachineRepository extends MongoRepository<DeliveredMachines, String> {

}
