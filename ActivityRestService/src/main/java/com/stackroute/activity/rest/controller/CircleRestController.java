package com.stackroute.activity.rest.controller;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.stackroute.activity.dao.CircleDAO;
import com.stackroute.activity.model.Circle;
import com.stackroute.activity.model.UserCircle;

@RestController
public class CircleRestController {
	
	private static final Logger logger =
			LoggerFactory.getLogger(CircleRestController.class);

	
	@Autowired
	CircleDAO circleDAO;
	
	@Autowired
	UserCircle userCircle;
	
	@Autowired
	ResourceBundleMessageSource messageSource;
	
	
	
	@PostMapping("/circle/create")
	public ResponseEntity<Circle> createCircle(@RequestBody Circle circle){
		
		Circle c=circleDAO.get(circle.getId());
		if (c!=null) {
			
            logger.debug("A circle with name " + circle.getName() + " already exist");
            Circle errorCircle=new Circle();
            errorCircle.setErrorCode("409");
            errorCircle.setErrorMessage(messageSource.getMessage("circle.create.failure", new String[]{circle.getName()}, Locale.US));
            return new ResponseEntity<Circle>(errorCircle,HttpStatus.CONFLICT);
        }
		
		circleDAO.save(circle);
		circle.setErrorCode("200");
		circle.setErrorMessage(messageSource.getMessage("circle.create.success", null, Locale.US));
		return new ResponseEntity<Circle>(circle,HttpStatus.CREATED);
	}
	
	
	@PutMapping("/circle/add/{userId}/{circleId}")
	public ResponseEntity<UserCircle> addUser(@PathVariable("userId") String userId, @PathVariable("circleId") String circleId){
		
		boolean status=circleDAO.addUser(userId, circleId);
		if(status==false){
			userCircle.setErrorCode("409");
			userCircle.setErrorMessage(messageSource.getMessage("circle.add.user.failure", new String[]{userId,circleId}, Locale.US));
			return new ResponseEntity<UserCircle>(HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		else
		{
			userCircle.setErrorCode("200");
			userCircle.setErrorMessage(messageSource.getMessage("circle.add.user.success", new String[]{userId,circleId}, Locale.US));
			return new ResponseEntity<UserCircle>(HttpStatus.OK);
		}
		
	}
	
	@PutMapping("/circle/remove/{userId}/{circleId}")
	public ResponseEntity<UserCircle> removeUser(@PathVariable("userId") String userId, @PathVariable("circleId") String circleId){
		
		boolean status=circleDAO.removeUser(userId, circleId);
		if(status==false){
			userCircle.setErrorCode("404");
			userCircle.setErrorMessage(messageSource.getMessage("circle.remove.user.failure", new String[]{userId,circleId}, Locale.US));
			return new ResponseEntity<UserCircle>(HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		else
		{
			userCircle.setErrorCode("200");
			userCircle.setErrorMessage(messageSource.getMessage("circle.remove.user.succss", new String[]{userId,circleId}, Locale.US));
			return new ResponseEntity<UserCircle>(HttpStatus.OK);
			
		}
		
	}
	
	//-------------------------------Retrieve all circles-------------------------------------
	@GetMapping("/circle")
	public ResponseEntity<List<Circle>> getAllCircles(){
	
		return new ResponseEntity<List<Circle>>(circleDAO.getAllCircles(),HttpStatus.OK);
		
	}
	
//-----------------------Retrieve circles for a specific user--------------------------------	
	@GetMapping("/circle/search/user/{userId}")
	public ResponseEntity<List<String>> getMyCircles(@PathVariable("userId") String userId){
		
		return new ResponseEntity<List<String>>(circleDAO.getMyCircles(userId),HttpStatus.OK);
		
	}
	
//----------------------Retrieve circles by Search String-----------------------------------	
	@GetMapping("/circle/search/{searchString}")
	public ResponseEntity<List<Circle>> getAllCircles(@PathVariable("searchString") String searchString){
		
		return new ResponseEntity<List<Circle>>(circleDAO.getAllCircles(searchString),HttpStatus.OK);
		
		
	}

}
