package com.example.herokupipeexample;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CustomerController {

    @Autowired
    private MetricRegistry registry;

    private CustomerRepository customerRepository;
    private Logger logger;

    public CustomerController(CustomerRepository customerRepository, MetricRegistry registry) {
        this.customerRepository = customerRepository;
        this.registry = registry;
        logger = LoggerFactory.getLogger(CustomerController.class);
    }

    @RequestMapping("/")
    public String welcome() {
        logger.info("Home page");
        registry.meter("welcome").mark();
        return "Welcome to this small REST service. It will accept a GET on /list with a request parameter lastName, and a POST to / with a JSON payload with firstName and lastName as values.";
    }

    @RequestMapping("/list")
    public List<Customer> find(@RequestParam(value = "lastName") String lastName) {
        final Timer.Context context = registry.timer("responses").time();

        logger.warn("Getting customer");
        List<Customer> customers = customerRepository.findByLastName(lastName);
        context.stop();
        return customers;
    }

    @PostMapping("/")
    public Customer newCustomer(@RequestBody Customer customer) {
        logger.warn("Creating customer");
        registry.counter("customers").inc();
        System.out.println(customer);
        return customerRepository.save(customer);
    }

}
