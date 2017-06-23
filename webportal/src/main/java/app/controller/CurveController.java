package app.controller;

import app.cache.entity.Curve;
import app.cache.repositories.CurveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by matthias on 19.06.17.
 */
@RestController
public class CurveController {

    @Autowired
    private CurveRepository repository;

    @RequestMapping(value = "/getCurves", method = RequestMethod.GET)
    public @ResponseBody List<Curve> getCurve() {
        return repository.findAll();
    }
}
