package com.payten.FoodRest.repository;

import com.payten.FoodRest.model.Users;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsersRepository extends MongoRepository<Users, String> {
    @Aggregation(pipeline = { "{ '$group': { '_id' : '$location' } }" })
    List<String> findDistinctLocations();

    @Query(fields = "{'location': 0}")
    List<Users> findByLocation(String location);
}
