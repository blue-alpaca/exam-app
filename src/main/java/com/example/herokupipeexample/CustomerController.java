package com.example.herokupipeexample;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.codahale.metrics.MetricRegistry.name;

@RestController
public class CustomerController {

    @Autowired
    private MetricRegistry registry;

    private CustomerRepository customerRepository;
    private final Counter customersCreated;
    private final Timer responses;
    private Logger logger;

    public CustomerController(CustomerRepository customerRepository, MetricRegistry registry) {
        this.customerRepository = customerRepository;
        this.registry = registry;
        customersCreated = registry.counter(name(CustomerController.class, "customers"));
        responses = registry.timer(name(CustomerController.class, "responses"));
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
        final Timer.Context context = responses.time();

        logger.info("Getting customer");
        List<Customer> customers = customerRepository.findByLastName(lastName);
        context.stop();
        return customers;
    }

    @PostMapping("/")
    public Customer newCustomer(@RequestBody Customer customer) {
        logger.info("Creating customer");
        customersCreated.inc();
        System.out.println(customer);
        return customerRepository.save(customer);
    }

}
