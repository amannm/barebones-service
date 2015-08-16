package com.amannmalik.service.barebones.endpoint;

import javax.enterprise.context.Dependent;

/**
 * Created by Amann on 8/16/2015.
 */


@Dependent
public class ExampleData {

    public String getGreeting() {
        return "Hello World";
    }

}
