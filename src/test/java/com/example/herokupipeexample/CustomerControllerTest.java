package com.example.herokupipeexample;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerRepository customerRepository;
    @MockBean
    private MetricRegistry registry;

    @Mock
    private Meter meter;
    @Mock
    private Counter counter;
    @Mock
    private Timer timer;
    @Mock
    private Timer.Context context;

    @Test
    public void welcome() throws Exception {
        when(registry.meter(ArgumentMatchers.any())).thenReturn(meter);
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome")));
    }

    @Test
    public void find() throws Exception {
        when(registry.timer(ArgumentMatchers.any())).thenReturn(timer);
        when(timer.time()).thenReturn(context);

        when(customerRepository.findByLastName("World")).thenReturn(Collections.singletonList(
                new Customer("Hello", "World")
        ));
        this.mockMvc.perform(get("/list").param("lastName", "World")).andExpect(status().isOk())
                .andExpect(content().string(containsString("\"firstName\":\"Hello\"")));
    }

    @Test
    public void newCustomer() throws Exception {
        when(registry.counter(ArgumentMatchers.any())).thenReturn(counter);
        String json = "{\"firstName\":\"Hello\",\"lastName\":\"World\"}";
        this.mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print()).andExpect(status().isOk());
    }

}
