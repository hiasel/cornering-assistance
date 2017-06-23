package app.controller;

import app.cache.entity.Params;
import app.cache.repositories.ParamsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by matthias on 19.06.17.
 */
@RestController
public class ParamsController {

    @Autowired
    private ParamsRepository repository;

    @RequestMapping(value = "/getParams", method = RequestMethod.GET)
    public @ResponseBody Params getParams() {
        return repository.findAll().get(0);
    }

    @RequestMapping(value = "/updateParams", method = RequestMethod.POST)
    public void updateParams(@RequestBody Params params) {
        repository.save(params);
    }


    }
