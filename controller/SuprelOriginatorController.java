package com.stellantis.SUPREL.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.stellantis.SUPREL.dto.SearchSuprelMaster;
import com.stellantis.SUPREL.dto.StatusChangeDetails;
import com.stellantis.SUPREL.dto.SuprelMasterDto;
import com.stellantis.SUPREL.message.ResponseMessage;
import com.stellantis.SUPREL.model.SuprelAttachments;
import com.stellantis.SUPREL.model.SuprelAuditLog;
import com.stellantis.SUPREL.model.SuprelManager;
import com.stellantis.SUPREL.model.SuprelMaster;
import com.stellantis.SUPREL.model.SuprelSISData;
import com.stellantis.SUPREL.service.SuprelOriginatorService;

import io.jsonwebtoken.lang.Collections;

@RestController
public class SuprelOriginatorController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SuprelOriginatorController.class);
	
	private static final String LOCATION = "Location";
	
	@Value("${url}")
	private String url;
	
	@Autowired
	private SuprelOriginatorService suprelOriginatorService;
	
	@GetMapping(value = "/incident-claims")  
	public void supplierListRedirect(HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader(LOCATION, url);
		httpServletResponse.setStatus(302);
		LOGGER.info("incident claim List reloading Completed");
	}
	
	@GetMapping(value = "/incident-claims/{suprelNo}/{tId}")  
	public void claimsRedirect(HttpServletResponse httpServletResponse,  @PathVariable("suprelNo") String suprelNo, @PathVariable("tId") String tId) throws IOException {
		suprelOriginatorService.updateMailStatus(suprelNo,"Y"+"-"+tId);
		httpServletResponse.setHeader(LOCATION, url);
		httpServletResponse.setStatus(302);
	}
	
	@GetMapping(value = "/claims-incident")  
	public ResponseEntity<List<SuprelMaster>> supplierListFromMail(HttpServletRequest httpRequest, @RequestParam("tId") String tId) {
		SearchSuprelMaster suprelMaster = new SearchSuprelMaster();
		SuprelManager details = suprelOriginatorService.getManagerDetailsTid(tId);
		if(details != null){
			suprelMaster.setManagerName(details.getManagerName());
		}else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		List<SuprelMaster> master = suprelOriginatorService.getSuprelDetailsByManagerName(suprelMaster.getManagerName(),"Y"+"-"+tId);
		if(!Collections.isEmpty(master)) {
			suprelMaster.setSuprelNo(master.get(0).getSuprelNo());
		}else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		suprelMaster.setRole("Manager");
		List<SuprelMaster> list = suprelOriginatorService.searchIncidentClaimAPI(suprelMaster);
		suprelOriginatorService.updateMailStatus(suprelMaster.getSuprelNo(),"N"+"-"+tId);
		if(!Collections.isEmpty(list) && list.get(0).getManagerName().equals(details.getManagerName())) {
			 return ResponseEntity.status(HttpStatus.OK).body(list);
		}else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}
	
	@GetMapping(value = "/error")  
	public void errorSupplierListRedirect(HttpServletResponse httpServletResponse) {
	    httpServletResponse.setHeader(LOCATION, url);
	    httpServletResponse.setStatus(302);
	    LOGGER.info("Error occured but reloading Completed");
	}
	
	@PostMapping(value="/createNewIncidentClaim")
	public ResponseEntity<ResponseMessage> createIncidentClaims(@RequestParam("claimFile") MultipartFile[] file, @RequestParam("supplr") String claimMesg){
		try {
			SuprelMasterDto suprelDto = suprelOriginatorService.getSupplierJson(claimMesg);
			if(null == suprelDto) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			SuprelMaster suprelMaster = suprelOriginatorService.getSuprelMasterDetails(suprelDto);		
			suprelOriginatorService.createIncidentClaim(suprelMaster, file);
		if(suprelMaster.getSuprelStatus().equals("Pending")) {
			suprelOriginatorService.pendingStatusMail(suprelMaster, suprelMaster.getManagerName());
		}
		 return  ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Incident Claim Created Successfully"));
		}catch(Exception e){
			LOGGER.error("context", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping(value="/getSearchClaim")
	public List<SuprelMaster> searchIncidentClaimAPI(@RequestBody SearchSuprelMaster suprelMaster){
		return suprelOriginatorService.searchIncidentClaimAPI(suprelMaster);
	}
	
	@PostMapping(value="/saveAndUpdateIncidentClaim")
	public ResponseEntity<ResponseMessage> saveAndUpdateIncidentClaimAPI(@RequestParam("claimFile") MultipartFile[] file, @RequestParam("supplr") String claimMesg, @RequestParam("deleteFiles") String deleteFiles){
		try {
			SuprelMasterDto suprelDto = suprelOriginatorService.getSupplierJson(claimMesg);
			if(null == suprelDto) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			SuprelMaster suprelMaster = suprelOriginatorService.getSuprelMasterDetails(suprelDto);
		suprelOriginatorService.saveAndUpdateIncidentClaim(suprelMaster, file, deleteFiles);
		if(suprelMaster.getSuprelStatus().equals("Pending")) {
			suprelOriginatorService.pendingStatusMail(suprelMaster, suprelMaster.getManagerName());
		}
		return  ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Incident Claim Updated Successfully"));
		}catch(Exception e){
			LOGGER.error("context", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping(value="/deleteIncidentClaim")
	public void deleteIncidentClaimAPI(@RequestParam String suprelNo){
		suprelOriginatorService.deleteIncidentClaim(suprelNo);
	}
	
	@PostMapping(value="/getAttachmentsById")
	public List<SuprelAttachments> getAttachmentsById(@RequestParam String suprelNo){
		return suprelOriginatorService.getSuprelAttachmentsList(suprelNo);
	}
	
	@PostMapping(value="/incidentStatusChange")
	public ResponseEntity<ResponseMessage> incidentStatusChangeAPI(@RequestParam("statusChange") String statusChangeDetails){
		try {
			StatusChangeDetails details = suprelOriginatorService.getSupplierStatusJson(statusChangeDetails);
			if(null == details) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			suprelOriginatorService.getSuprelDetails(details);
			return  ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Incident Claim "+details.getSuprelStatus()+" Successfully"));
		}catch(Exception e){
			LOGGER.error("context", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping(value="/downloadCertificate")
	public ResponseEntity< Resource > downloadCertificate(@RequestParam("suprelCode") String suprelCode, @RequestParam("supplierCode") String supplierCode,@RequestParam("fileName") String fileName){
		try {
			SuprelAttachments fileDet = suprelOriginatorService.getSuprelAttachment(suprelCode, supplierCode, fileName);
				return ResponseEntity.ok()
						.contentType(MediaType.parseMediaType("application/pdf"))
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
						.body(new ByteArrayResource(fileDet.getFile()));
		}catch(Exception e){
			LOGGER.error("context", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping(value="/supplierCodeDetails")
	public SuprelSISData getSupplierCodeDetails(@RequestParam("supplierCode") String supplierCode){
		try {
			return suprelOriginatorService.getSuprelSISData(supplierCode);
		}catch(Exception e){
			LOGGER.error("context", e);
		}
		return null;
	}
	
	@GetMapping(value="/pendingCertification")
	public List<SuprelMaster> pendingCertification(@RequestParam("managerName") String managerName){
		return suprelOriginatorService.pendingCertification(managerName);
	}
	
	@GetMapping(value="/managerNamesList")
	public List<SuprelManager> getManagerNamesList(){
		return suprelOriginatorService.getManagerNamesList();
	}
	
	@PostMapping(value="/getAuditLogDetailsForSuprelNum")
	public List<SuprelAuditLog> getAuditLogDetailsForSuprelNum(@RequestParam("suprelNo") String suprelNo){
		
		return suprelOriginatorService.getAuditLogDetailsForSuprelNum(suprelNo);
	}
	
	@GetMapping(value="/getStateList")
	public List<String> getStateList(){
		return suprelOriginatorService.getStateList();
	}
	
	@GetMapping(value="/getCountryList")
	public List<String> getCountryList(){
		return suprelOriginatorService.getCountryList();
	}
	
	@PostMapping(value="/saveBulkUploadData") 
	public ResponseEntity<ResponseMessage> saveOrUpdateBulkUploadData(@RequestParam("selectedFile") MultipartFile filePath) throws NullPointerException{
		try
		{
			ResponseEntity<ResponseMessage> response = suprelOriginatorService.importApproverDetails(filePath);
			return response;
		}
		catch (Exception e) 
		{
			LOGGER.error("context", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping(value="/saveIncidentClaimsData") 
	public ResponseEntity<ResponseMessage> saveIncidentClaimsData(@RequestParam("selectedFile") MultipartFile filePath) throws NullPointerException{
		try
		{
			ResponseEntity<ResponseMessage> response = suprelOriginatorService.saveIncidentClaimsData(filePath);
			return response;
		}
		catch (Exception e) 
		{
			LOGGER.error("context", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping(value="/getYearsList")
	public List<String> getYearsList(){
		return suprelOriginatorService.getYearList();
	}
	
}
