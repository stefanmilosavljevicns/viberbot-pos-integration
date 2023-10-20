package com.payten.restapi.repository;
import java.util.List;
import com.payten.restapi.model.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Query;

public interface UsersRepository extends MongoRepository<Users, String> {
    @Aggregation(pipeline = { "{ '$group': { '_id' : '$location' } }" })
    List<String> findDistinctLocations();

    @Query(fields = "{'location': 0}")
    List<Users> findByLocation(String location);
}
