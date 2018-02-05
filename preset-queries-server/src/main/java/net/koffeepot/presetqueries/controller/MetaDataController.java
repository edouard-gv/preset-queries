package net.koffeepot.presetqueries.controller;

import net.koffeepot.presetqueries.entity.Configuration;
import net.koffeepot.presetqueries.repository.ConfigurationRepository;
import net.koffeepot.presetqueries.service.ParameterType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class MetaDataController {

    @Autowired
    private ConfigurationRepository configurationRepository;

    @RequestMapping(path = "/api/configurations", method= RequestMethod.GET)
    public @ResponseBody
    List<Configuration> getConfigurations() {
        //we iterate through the list at the last moment
        List<Configuration> configurationList = new ArrayList<>();
        configurationRepository.findAll().forEach(configurationList::add);
        return configurationList;
    }

    @RequestMapping(path = "/api/parameter-types", method= RequestMethod.GET)
    public @ResponseBody
    List<ParameterType> getParameterTypes() {
        return Arrays.asList(ParameterType.values());
    }


}
