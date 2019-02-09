package com.lovnx.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.apache.log4j.Logger;
import org.springframework.cloud.client.ServiceInstance;

@RestController
public class ConsumerController {

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired  
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private DiscoveryClient client;

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(@RequestParam Integer a,@RequestParam Integer b) {

        ServiceInstance instance = client.getLocalServiceInstance();

        logger.info("robbin /add, host:" + instance.getHost() + ", service_id:" + instance.getServiceId() + ", result:" );

    	this.loadBalancerClient.choose("service-B");//随机访问策略
        return restTemplate.getForEntity("http://service-B/add?a="+a+"&b="+b, String.class).getBody();
    	
    }
    
}


