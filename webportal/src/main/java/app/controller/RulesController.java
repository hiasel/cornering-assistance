package app.controller;

import app.cache.entity.Rules;
import app.cache.repositories.RulesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by matthias on 19.06.17.
 */
@RestController
public class RulesController {

    @Autowired
    private RulesRepository repository;

    @RequestMapping(value = "/getRules", method = RequestMethod.GET)
    public @ResponseBody Rules getRules() {
        return repository.findAll().get(0);
    }

    @RequestMapping(value = "/updateRules", method = RequestMethod.POST)
    public void updateRules(@RequestBody Rules rules) {
        repository.save(rules);
    }
}
