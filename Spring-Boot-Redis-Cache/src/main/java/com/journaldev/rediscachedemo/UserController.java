package com.journaldev.rediscachedemo;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @PostConstruct
	public void run() {

		//Populating embedded database here
		LOG.info("Saving users. Current user count is {}.");
		Userrr shubham = new Userrr("Shubham", 2000);
		Userrr pankaj = new Userrr("Pankaj", 29000);
		Userrr lewis = new Userrr("Lewis", 550);

		userRepository.save(shubham);
		userRepository.save(pankaj);
		userRepository.save(lewis);
		
		LOG.info("Done saving users. Data: {}.", userRepository.findAll());
	}

    @Cacheable(value = "users", key = "#userId", unless = "#result.followers < 12000")
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public Userrr getUser(@PathVariable String userId) {
        LOG.info("Getting user with ID {}.", userId);
        return userRepository.findById(Long.valueOf(userId)).get();
    }

    @CachePut(value = "users", key = "#user.id")
    @PutMapping("/update")
    public Userrr updatePersonByID(@RequestBody Userrr user) {
        userRepository.save(user);
        return user;
    }

    @CacheEvict(value = "users", allEntries=true)
    @DeleteMapping("/{userId}")
    public void deleteUserByID(@PathVariable Long userId) {
        LOG.info("deleting person with id {}", userId);
        userRepository.deleteById(userId);
    }
}
