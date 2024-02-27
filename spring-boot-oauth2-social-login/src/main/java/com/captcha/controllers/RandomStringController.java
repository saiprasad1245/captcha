package com.captcha.controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.captcha.config.CurrentUser;
import com.captcha.dto.ApiResponse;
import com.captcha.dto.JwtAuthenticationResponse;
import com.captcha.dto.LocalUser;
import com.captcha.dto.SignUpRequest;
import com.captcha.model.User;
import com.captcha.models.LoginData;
import com.captcha.models.LoginRepository;
import com.captcha.models.RandomString;
import com.captcha.models.RandomStringDao;
import com.captcha.models.Registration;
import com.captcha.security.jwt.TokenProvider;
import com.captcha.service.UserService;
import com.captcha.services.KafkaSender;
import com.captcha.util.GeneralUtils;
import com.captcha.utils.RandomStringOptions;
import com.captcha.utils.ResponseMessage;
import com.captcha.utils.StringGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.samstevens.totp.code.CodeVerifier;

@RestController
@RequestMapping("/api/auth")
public class RandomStringController {

    @Autowired
    private RandomStringDao _randomStringDao;
    
    @Autowired
    private com.captcha.models.RegisterRepository registerRepository;
    
    @Autowired
    private LoginRepository loginrepo;

    @Autowired
    private KafkaSender kafkaSender;
    
    @Autowired
	UserService userService;
    
    @Autowired
	private CodeVerifier verifier;
    
    @Autowired
	AuthenticationManager authenticationManager;
    
    @Autowired
	TokenProvider tokenProvider;

    @GetMapping(value = "/random/all")
    public List<RandomString> getAll() {
        List<RandomString> result = _randomStringDao.findAll();
        return result;
    }

    @RequestMapping(value = "/randomData", method = RequestMethod.POST, produces = "application/json")
    public List<RandomString> create(@RequestBody RandomStringOptions request) {
        StringGenerator stringGenerator = new StringGenerator();
        if (request.length > 100) {
            request.length = 100;
        }
        List<RandomString> result = stringGenerator.generate(request);

        for (RandomString randomString : result) {
            kafkaSender.send(randomString.getRandomString());
        }

        Collections.reverse(result);

        return result;
    }
    
    @RequestMapping(value = "/signup", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<ResponseMessage> register(@RequestBody SignUpRequest request) {
    	//request.setId(registerRepository.getMaxId()+1);
    	//request.setCreatedDate(new Date());
    	
    	User user = userService.registerNewUser(request);
    	//registerRepository.save(request);

    	return  ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Created Successfully"));
    }
    
    
    @RequestMapping(value = "/deleteCaptcha/{value}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<ResponseMessage> deleteCaptcha(@PathVariable("value") String value) {
    	
    	int i = _randomStringDao.deleteCaptach(value);

    	return  ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Created Successfully"));
    }
    
    
    @RequestMapping(value = "/signin", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<JwtAuthenticationResponse> login(@RequestBody LoginData request) {
        
    	//List<User> reg =registerRepository.getByID(request.getEmail(), request.getPassword());
    	//if(reg !=null && request.getEmail().equals(reg.get(0).getEmail()) && request.getPassword().equals(reg.get(0).getPassword())) {
    		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    		SecurityContextHolder.getContext().setAuthentication(authentication);
    		LocalUser localUser = (LocalUser) authentication.getPrincipal();
    		boolean authenticated = !localUser.getUser().isUsing2FA();
    		String jwt = tokenProvider.createToken(localUser, authenticated);
    		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, true, GeneralUtils.buildUserInfo(localUser)));
    		
    	//}else {
    		//return ResponseEntity.ok(new JwtAuthenticationResponse("", true,null));
    	//}
    	
    	
    }
    
	public Registration getSupplierJson(String claimMesg){
		
		Registration suppDto = new Registration();
		
		try {
			ObjectMapper objMapper = new ObjectMapper();
			suppDto = objMapper.readValue(claimMesg, Registration.class);
			
		}catch(IOException e) {
		
			return null;
		}
		return suppDto;
		
	}
    @RequestMapping(value = "/random/check/{value}", method = RequestMethod.GET, produces = "application/json")
    public List<RandomString> check(@PathVariable("value") String value) {
    	List<RandomString> result = _randomStringDao.getRandomDataById(value);
        return result;
    }
    
    @PostMapping("/verify")
	@PreAuthorize("hasRole('PRE_VERIFICATION_USER')")
	public ResponseEntity<?> verifyCode(@NotEmpty @RequestBody String code, @CurrentUser LocalUser user) {
		if (!verifier.isValidCode(user.getUser().getSecret(), code)) {
			return new ResponseEntity<>(new ApiResponse(false, "Invalid Code!"), HttpStatus.BAD_REQUEST);
		}
		String jwt = tokenProvider.createToken(user, true);
		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, true, GeneralUtils.buildUserInfo(user)));
	}
}