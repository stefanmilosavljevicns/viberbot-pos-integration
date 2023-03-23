package com.payten.FoodRest.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Query;
import com.payten.FoodRest.model.Menu;
import java.util.List;

public interface MenuRepository extends MongoRepository<Menu, String> {
    @Aggregation(pipeline = { "{ '$group': { '_id' : '$category' } }" })
    List<String> findDistinctCategories();

    @Query(value = "{ 'name' : ?0 }")
    Menu findByName(String name);

    @Query(fields = "{'category': 0}")
    List<Menu> findByCategory(String location);
}
