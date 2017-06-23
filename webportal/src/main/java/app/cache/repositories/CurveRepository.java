package app.cache.repositories;

import app.cache.entity.Curve;
import app.cache.entity.Params;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by matthias on 19.06.17.
 */
public interface CurveRepository extends MongoRepository<Curve, String> {

}
