package com.captcha.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.captcha.config.CurrentUser;
import com.captcha.dto.ApiResponse;
import com.captcha.dto.AttachmentsDto;
import com.captcha.dto.JwtAuthenticationResponse;
import com.captcha.dto.LocalUser;
import com.captcha.dto.SignUpRequest;
import com.captcha.model.Attachments;
import com.captcha.model.Order;
import com.captcha.model.User;
import com.captcha.models.LoginData;
import com.captcha.models.RandomString;
import com.captcha.models.RandomStringDao;
import com.captcha.repo.OrderRepository;
import com.captcha.repo.UserRepository;
import com.captcha.security.jwt.TokenProvider;
import com.captcha.service.UserService;
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
	UserService userService;
    
    @Autowired
	private CodeVerifier verifier;
    
    @Autowired
	AuthenticationManager authenticationManager;
    
    @Autowired
	TokenProvider tokenProvider;
    
    @Autowired
    com.captcha.repo.PaymentRepository PaymentRepository;
    
    @Autowired
    UserRepository userRepo;
    
    @Autowired
    OrderRepository orderRepo;
    
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
        	RandomString randomString1 = new RandomString(randomString.getRandomString());
            randomString1.setCreatedTime(new Date());
            //randomString1.setTime(60);
            randomString1.setId(_randomStringDao.getMaxId()+1);
            _randomStringDao.save(randomString1);
            
        }

        Collections.reverse(result);

        return result;
    }
    
    @RequestMapping(value = "/demorandomData", method = RequestMethod.POST, produces = "application/json")
    public List<RandomString> createDemo(@RequestBody RandomStringOptions request) {
        StringGenerator stringGenerator = new StringGenerator();
        if (request.length > 100) {
            request.length = 100;
        }
        List<RandomString> result = stringGenerator.generate(request);

        for (RandomString randomString : result) {
        	RandomString randomString1 = new RandomString(randomString.getRandomString());
            randomString1.setCreatedTime(new Date());
            //randomString1.setTime(60);
            randomString1.setId(_randomStringDao.getMaxId()+1);
            _randomStringDao.save(randomString1);
            
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
    
    @PostMapping(value="/createPayment")
	public ResponseEntity<ResponseMessage> createpayment(@RequestParam("claimFile") MultipartFile[] file, @RequestParam("supplr") String claimMesg){
		try {
			AttachmentsDto suprelDto = getSupplierJson(claimMesg);
			if(null == suprelDto) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			Attachments att = new Attachments();
			att.setId(PaymentRepository.getMaxId()+1);
			att.setAddress(suprelDto.getAddress());
			att.setEmail(suprelDto.getEmail());
			att.setFile(file[0].getBytes());
			att.setFileName(file[0].getName());
			att.setName(suprelDto.getName());
			att.setPhone(suprelDto.getPhone());
			att.setAmount(suprelDto.getAmount());
			PaymentRepository.save(att);
			int amount = userRepo.getAmount(suprelDto.getName());
			userRepo.updateUserAmount(suprelDto.getName(), amount-suprelDto.getAmount());
			//suprelOriginatorService.createIncidentClaim(suprelMaster, file);
		
		 return  ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Incident Claim Created Successfully"));
		}catch(Exception e){
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
    @PostMapping(value="/history")
    public List<AttachmentsDto> getAttachments(@RequestParam("username") String username){
    	List<AttachmentsDto> dto = new ArrayList<AttachmentsDto>();
    	List<Attachments>  attchments = PaymentRepository.getAllAttachments(username);
    	
    	for(Attachments att:attchments) {
    		AttachmentsDto dt = new AttachmentsDto();
    		dt.setAddress(att.getAddress());
    		dt.setEmail(att.getEmail());
    		dt.setFileName(att.getFileName());
    		dt.setName(att.getName());
    		dt.setPhone(att.getPhone());
    		dt.setAmount(att.getAmount());
    		dto.add(dt);
    	}
    	return dto;
    }
    
    @PostMapping(value="/allhistory")
    public List<AttachmentsDto> getAllAttachments(){
    	List<AttachmentsDto> dto = new ArrayList<AttachmentsDto>();
    	List<Attachments>  attchments = PaymentRepository.findAll();
    	
    	for(Attachments att:attchments) {
    		AttachmentsDto dt = new AttachmentsDto();
    		dt.setAddress(att.getAddress());
    		dt.setEmail(att.getEmail());
    		dt.setFileName(att.getFileName());
    		dt.setName(att.getName());
    		dt.setPhone(att.getPhone());
    		dt.setAmount(att.getAmount());
    		dto.add(dt);
    	}
    	return dto;
    }
    
    @PostMapping(value="/history-wallet")
    public List<Order> getAttachmentsOrder(@RequestParam("username") String username){
    	///List<AttachmentsDto> dto = new ArrayList<AttachmentsDto>();
    	List<Order>  attchments = orderRepo.getAllAttachments(username);
    	
    	
    	return attchments;
    }
    
    @PostMapping(value="/allhistory-wallet")
    public List<Order> getAllAttachmentsOrder(){
    	///List<AttachmentsDto> dto = new ArrayList<AttachmentsDto>();
    	List<Order>  attchments = orderRepo.findAll();
    	
    	
    	return attchments;
    }
    
	@GetMapping(value="/downloadCertificate")
	public ResponseEntity< Resource > downloadCertificate(@RequestParam("email") String email,@RequestParam("fileName") String fileName){
		try {
			Attachments fileDet = PaymentRepository.getAttachments(email, fileName);
				return ResponseEntity.ok()
						.contentType(MediaType.parseMediaType("application/pdf"))
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
						.body(new ByteArrayResource(fileDet.getFile()));
		}catch(Exception e){
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	public AttachmentsDto getSupplierJson(String claimMesg){
		
		AttachmentsDto suppDto = new AttachmentsDto();
		
		try {
			ObjectMapper objMapper = new ObjectMapper();
			suppDto = objMapper.readValue(claimMesg, AttachmentsDto.class);
			
		}catch(IOException e) {
		
			return null;
		}
		return suppDto;
		
	}
    @RequestMapping(value = "/random/check/{value}/{username}", method = RequestMethod.GET, produces = "application/json")
    public List<RandomString> check(@PathVariable("value") String value, @PathVariable("username") String username) {
    	List<RandomString> result = _randomStringDao.getRandomDataById(value);
    	int count = userRepo.getCount(username);
    	if(result.size()>0) {
    		count = count+1;
    		userRepo.updateUserData(username,count);
    		if(count>=10) {
    			int staticAmount = 500;
    			int amount = userRepo.getAmount(username);
    			int final_amount = amount+staticAmount;
    			userRepo.updateUserAmount(username, final_amount);
    		}
    		result.get(0).setTime(count);
    	}
    	
        return result;
    }
    @RequestMapping(value = "/withdraw", method = RequestMethod.POST, produces = "application/json")
    public int withdraw( @RequestParam("username") String username) {

    	int amount = userRepo.getAmount(username);

    	return amount;
    }
    
    
    @RequestMapping(value = "/random/democheck/{value}", method = RequestMethod.GET, produces = "application/json")
    public List<RandomString> democheck(@PathVariable("value") String value) {
    	List<RandomString> result = _randomStringDao.getRandomDataById(value);

    	result.get(0).setTime(1);
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