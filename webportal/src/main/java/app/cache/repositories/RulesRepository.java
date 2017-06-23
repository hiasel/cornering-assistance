package app.cache.repositories;

import app.cache.entity.Rules;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by matthias on 19.06.17.
 */
public interface RulesRepository extends MongoRepository<Rules, String> {

}
