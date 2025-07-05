package com.sgh.selvaganapathyhydraulics.Repository;

import com.sgh.selvaganapathyhydraulics.entity.MachineVideos;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MachineVideosRepository extends MongoRepository<MachineVideos, String> {
}
