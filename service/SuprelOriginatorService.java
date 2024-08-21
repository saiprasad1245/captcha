package com.stellantis.SUPREL.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.util.CollectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stellantis.SUPREL.dto.SearchSuprelMaster;
import com.stellantis.SUPREL.dto.StatusChangeDetails;
import com.stellantis.SUPREL.dto.SuprelMasterDto;
import com.stellantis.SUPREL.message.ResponseMessage;
import com.stellantis.SUPREL.model.SuprelAttachments;
import com.stellantis.SUPREL.model.SuprelAuditLog;
import com.stellantis.SUPREL.model.SuprelManager;
import com.stellantis.SUPREL.model.SuprelMaster;
import com.stellantis.SUPREL.model.SuprelNumbers;
import com.stellantis.SUPREL.model.SuprelSISData;
import com.stellantis.SUPREL.repository.ManagerRepository;
import com.stellantis.SUPREL.repository.NonConformityDetailsRepository;
import com.stellantis.SUPREL.repository.SuprelAttachmentRepository;
import com.stellantis.SUPREL.repository.SuprelAuditLogRepository;
import com.stellantis.SUPREL.repository.SuprelNumbersRepository;
import com.stellantis.SUPREL.repository.SuprelOriginatorRepository;
import com.stellantis.SUPREL.repository.TechnicalAreaDetailsRepository;
import com.stellantis.SUPREL.util.EmailUtil;

@Service
@Transactional(rollbackOn=RuntimeException.class)
public class SuprelOriginatorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SuprelOriginatorService.class);
	
	public static final String CERTNUMCONSTANT = "Suprel Number:";
	
	public static final String REGARDSCONST = "<br><br> Regards,";
	
	private static final String CONTEXT = "context";
	
	private static final String DRAFT = "Draft";
	
	private static final String PENDING = "Pending";
	
	private static final String ISSUED = "Issued";
	
	private static final String DISPUTED = "Disputed";
	
	private static final String CANCELLED = "Cancelled";
	
	private static final String CLOSED = "Closed";
	
	private static final String FINALEBSC = "Final";
	
	private static final String ADMINMAIL = "SuprelAdmin@fcagroup.com";
	
	private static final String SUPPLIER = "Supplier";
	
	private static final String IMAGE= "<img src=cid:myimagecid>";
	
	private static Pattern pattern1 = Pattern.compile("^[a-zA-Z0-9]*$");
	
	private static Pattern pattern2 = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9._%+-]+\\.[a-zA-Z]{2,}$");
	
	private static Pattern pattern3 = Pattern.compile("^[a-zA-Z0-9( )]*$");
	
	private static Pattern pattern = Pattern.compile("^[a-zA-Z0-9-( )]*$");
	 
	@Value("${email.host}")
	private String host;
	
	@Value("${url}")
	private String url;
	
	@Autowired
	private SuprelOriginatorRepository suprelOriginatorRepository;
	
	@Autowired
	private SuprelAttachmentRepository suprelAttachmentRepository;
	
	@Autowired
	private SuprelAuditLogRepository suprelAuditLogRepository;
	
	@Autowired
	private ManagerRepository managerRepo;
	
	@Autowired
	private SuprelNumbersRepository suprelNumbersRepository;
	
	@Autowired
	private TechnicalAreaDetailsRepository technicalAreaDetailsRepository;
	
	@Autowired
	private NonConformityDetailsRepository nonConformityDetailsRepository;
	
	public List<SuprelMaster> getAllIncidentClaimAPI(){
		return suprelOriginatorRepository.findAll();
	}
	
	/**
	 * 
	 * @param suprelMaster
	 * @param file
	 * @return
	 */
	public SuprelMaster createIncidentClaim(SuprelMaster suprelMaster, MultipartFile[] file) {
		Date date = new Date();
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int year = localDate.getYear();
		String suprelNumber = getRandomValue(year);
		suprelMaster.setSuprelNo(suprelNumber);
		String files = null;
		if(file.length>0) {
			List<String> filesList = new ArrayList<>();
			for(int i=0;i<file.length;i++) {
				try {
					if(file[i].getOriginalFilename() != null && !"blob".equals(file[i].getOriginalFilename())) {
						SuprelAttachments fileAttachments1 = new SuprelAttachments();
						fileAttachments1.setSuprelFileName(suprelMaster.getSuprelNo()+file[i].getOriginalFilename());
						fileAttachments1.setSupplierCode(suprelMaster.getSupplierCode());
						fileAttachments1.setSuprelNo(suprelMaster.getSuprelNo());
						fileAttachments1.setFile(file[i].getBytes());
						fileAttachments1.setFileName(file[i].getOriginalFilename());
						filesList.add(file[i].getOriginalFilename());
						suprelAttachmentRepository.save(fileAttachments1);
					}
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
				}
			}
			Collections.sort(filesList);
			files = String.join(", ", filesList);
		}

		if(files != null && files.length()<2000) {
			suprelMaster.setAttachmentNames(files);
		} else if(files != null && files.length()>=2000) {
			suprelMaster.setAttachmentNames(files.substring(0, 1999));
		}
		SuprelNumbers sNumber = new SuprelNumbers();
		sNumber.setSuprelNo(suprelMaster.getSuprelNo());
		sNumber.setCreatedBy(suprelMaster.getCreatedBy());
		sNumber.setCreatedOn(suprelMaster.getCreatedOn());
		suprelNumbersRepository.save(sNumber);
		return suprelOriginatorRepository.save(suprelMaster);
	}
	
	/**
	 * 
	 * @param suprelMaster
	 * @param file
	 * @return
	 */
	public SuprelMaster saveAndUpdateIncidentClaim(SuprelMaster suprelMaster, MultipartFile[] file, String deletedFiles){
		String files = null;

		String[] removeFiles = StringUtils.hasText(deletedFiles)?deletedFiles.split(","):new String[0];
		for(String removeFile:removeFiles) {
			deleteAttachments(suprelMaster.getSupplierCode(), removeFile, suprelMaster.getSuprelNo());
		}

		if(file.length>0) {
			for(int i=0;i<file.length;i++) {
				try {
					if(file[i].getOriginalFilename() != null && !"blob".equals(file[i].getOriginalFilename())) {
						SuprelAttachments attachments = new SuprelAttachments();
						attachments.setSuprelFileName(suprelMaster.getSuprelStatus().equals(DISPUTED) ? suprelMaster.getSuprelNo()+"_Disputed_"+file[i].getOriginalFilename()
								: suprelMaster.getSuprelNo()+file[i].getOriginalFilename());
						attachments.setSupplierCode(suprelMaster.getSupplierCode());
						attachments.setSuprelNo(suprelMaster.getSuprelNo());
						attachments.setFile(file[i].getBytes());
						attachments.setFileName(suprelMaster.getSuprelStatus().equals(DISPUTED) ? "Disputed_"+file[i].getOriginalFilename(): file[i].getOriginalFilename());
						suprelAttachmentRepository.save(attachments);
					}
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
				}
			}
		}

		List<String> filesList = suprelAttachmentRepository.getAttachmentNames(suprelMaster.getSuprelNo());
		Collections.sort(filesList);
		files = String.join(", ", filesList);
		if(files != null && files.length()<2000) {
			suprelMaster.setAttachmentNames(files);
		} else if(files != null && files.length()>=2000) {
			suprelMaster.setAttachmentNames(files.substring(0, 1999));
		}
		return suprelOriginatorRepository.save(suprelMaster);
	}
	
	/**
	 * 
	 * @param suprelNo
	 * @param managerName
	 * @return
	 */
	public List<SuprelMaster> incidentClaimAPI(String suprelNo, String managerName){
		SearchSuprelMaster suprelMaster = new SearchSuprelMaster();
		suprelMaster.setSuprelNo(suprelNo);
		suprelMaster.setRole("Manager");
		suprelMaster.setManagerName(managerName);
		return searchIncidentClaimAPI(suprelMaster);
	}
	
	/**
	 * 
	 * @param suprelMaster
	 * @return
	 */
	public List<SuprelMaster> searchIncidentClaimAPI(SearchSuprelMaster suprelMaster){
		if(!suprelMaster.getRole().equals(SUPPLIER)) {
			return suprelOriginatorRepository.searchIncidentClaim(suprelMaster.getSupplierCode(),suprelMaster.getSupplierName(),
					suprelMaster.getIncidentOriginator(),suprelMaster.getCity(), suprelMaster.getCountry(), suprelMaster.getState(), suprelMaster.getSuprelNo(),suprelMaster.getSuprelStatus(),
					suprelMaster.getPrimaryNonConformity(), suprelMaster.getIssueFromDate(),suprelMaster.getIssueToDate(), suprelMaster.getSupplierAddress(), suprelMaster.getTechnicalArea(),
					suprelMaster.getManagerName(), suprelMaster.getSecondaryNonConformity(), suprelMaster.getYear());
		}else if(suprelMaster.getRole().equals(SUPPLIER)){
			return searchSupplierClaimAPI(suprelMaster);
		}
		return new ArrayList<>();
	}
	
	/**
	 * 
	 * @param suprelMaster
	 * @return
	 */
	private List<SuprelMaster> searchSupplierClaimAPI(SearchSuprelMaster suprelMaster){
		Set<String> supplierCodeList = getSupplierDetailsForSupplierPage(suprelMaster.getUserId());
		if(supplierCodeList.contains(suprelMaster.getSupplierCode())) {
			return suprelOriginatorRepository.searchIncidentClaim(suprelMaster.getSupplierCode(),suprelMaster.getSupplierName(),
					suprelMaster.getIncidentOriginator(),suprelMaster.getCity(), suprelMaster.getCountry(), suprelMaster.getState(), suprelMaster.getSuprelNo(),suprelMaster.getSuprelStatus(),
					suprelMaster.getPrimaryNonConformity(), suprelMaster.getIssueFromDate(),suprelMaster.getIssueToDate(), suprelMaster.getSupplierAddress(), suprelMaster.getTechnicalArea(), 
					suprelMaster.getManagerName(), suprelMaster.getSecondaryNonConformity(), suprelMaster.getYear());
		}
		else if(!StringUtils.hasText(suprelMaster.getSupplierCode())) {
			List<SuprelMaster> supplierResultDetails = new ArrayList<>();
			if(!CollectionUtils.isNullOrEmpty(supplierCodeList)) {
				for(String supplierCode : supplierCodeList) {
					List<SuprelMaster> suprelMasterList = suprelOriginatorRepository.searchIncidentClaimSupplier(supplierCode,suprelMaster.getSupplierName(),
							suprelMaster.getIncidentOriginator(),suprelMaster.getCity(), suprelMaster.getCountry(), suprelMaster.getState(), suprelMaster.getSuprelNo(),suprelMaster.getSuprelStatus(),
							suprelMaster.getPrimaryNonConformity(), suprelMaster.getIssueFromDate(),suprelMaster.getIssueToDate(), suprelMaster.getSupplierAddress(), suprelMaster.getTechnicalArea(),
							suprelMaster.getManagerName(), suprelMaster.getSecondaryNonConformity(), suprelMaster.getYear());
					supplierResultDetails.addAll(suprelMasterList);
				}
			}
			return supplierResultDetails;
		}else {
			List<SuprelMaster> suprelMasterList = suprelOriginatorRepository.searchIncidentClaimSupplier(suprelMaster.getSupplierCode(),suprelMaster.getSupplierName(),
					suprelMaster.getIncidentOriginator(),suprelMaster.getCity(), suprelMaster.getCountry(), suprelMaster.getState(), suprelMaster.getSuprelNo(),suprelMaster.getSuprelStatus(),
					suprelMaster.getPrimaryNonConformity(), suprelMaster.getIssueFromDate(),suprelMaster.getIssueToDate(), suprelMaster.getSupplierAddress(), suprelMaster.getTechnicalArea(), 
					suprelMaster.getManagerName(), suprelMaster.getSecondaryNonConformity(), suprelMaster.getYear());
			Iterator<SuprelMaster> iter = suprelMasterList.iterator();
			while(iter.hasNext()){
				if(!supplierCodeList.contains(iter.next().getSupplierCode()))
					iter.remove();
			}
			return suprelMasterList;
		}
	}
	/**
	 * 
	 * @param supplierId
	 * @return
	 */
	public Set<String> getSupplierDetailsForSupplierPage(String supplierId){

		List<String> supplierManufCodeList = suprelOriginatorRepository.getSupplierCode(supplierId);
		// handling below code if we have supplier code with **
		HashSet<String> supplierCodeList =  new HashSet<>();
		if(!CollectionUtils.isNullOrEmpty(supplierManufCodeList)) {
			for(String supplierCode : supplierManufCodeList) {
				if(supplierCode.contains("**")) {
					supplierCodeList.addAll(suprelOriginatorRepository.getSupplierCodes(supplierCode.substring(0,5)));
				}else {
					supplierCodeList.add(supplierCode);
				}
			}
		}
		return supplierCodeList;
	}
	

	/**
	 * 
	 * @param suprelNo
	 */
	public void deleteIncidentClaim(String suprelNo) {
		suprelAttachmentRepository.deleteSuprelAttachments(suprelNo);
		suprelOriginatorRepository.deleteById(suprelNo);
	}
	
	/**
	 * 
	 * @param suppNum
	 * @param fileName
	 * @param suprelNo
	 */
	public void deleteAttachments(String suppNum, String fileName, String suprelNo) {
		 suprelAttachmentRepository.deleteAttachments(suppNum, fileName, suprelNo);
	}
	
	/**
	 * 
	 * @param claimMesg
	 * @return
	 */
	public SuprelMasterDto getSupplierJson(String claimMesg){
		
		SuprelMasterDto suppDto = new SuprelMasterDto();
		
		try {
			ObjectMapper objMapper = new ObjectMapper();
			suppDto = objMapper.readValue(claimMesg, SuprelMasterDto.class);
			
		}catch(IOException e) {
			LOGGER.error(CONTEXT, e);
			return null;
		}
		return suppDto;
		
	}
	
	/**
	 * 
	 * @param claimMesg
	 * @return
	 */
	public StatusChangeDetails getSupplierStatusJson(String claimMesg){
		
		StatusChangeDetails suppDto = new StatusChangeDetails();
		
		try {
			ObjectMapper objMapper = new ObjectMapper();
			suppDto = objMapper.readValue(claimMesg, StatusChangeDetails.class);
			
		}catch(IOException e) {
			LOGGER.error(CONTEXT, e);
			return null;
		}
		return suppDto;
		
	}
	
	/**
	 * 
	 * @param suprelMasterDto
	 * @return
	 */
	public SuprelMaster getSuprelMasterDetails(SuprelMasterDto suprelMasterDto) {
		SuprelMaster suprelMaster = new SuprelMaster();
		
		suprelMaster.setAmount(suprelMasterDto.getAmount());
		suprelMaster.setCity(suprelMasterDto.getCity());
		suprelMaster.setCountry(suprelMasterDto.getCountry());
		suprelMaster.setCreatedBy(suprelMasterDto.getCreatedBy());
		suprelMaster.setCreatedOn(suprelMasterDto.getCreatedOn());
		suprelMaster.setCurrencyType(suprelMasterDto.getCurrencyType());
		suprelMaster.setDetails(suprelMasterDto.getDetails());
		suprelMaster.setEmail1(suprelMasterDto.getEmail1());
		suprelMaster.setEmail2(suprelMasterDto.getEmail2());
		suprelMaster.setEmail3(suprelMasterDto.getEmail3());
		suprelMaster.setEmail4(suprelMasterDto.getEmail4());
		suprelMaster.setEmail5(suprelMasterDto.getEmail5());
		suprelMaster.setIssuedDate(suprelMasterDto.getIssuedDate());
		suprelMaster.setModifiedBy(suprelMasterDto.getModifiedBy());
		suprelMaster.setModifiedOn(suprelMasterDto.getModifiedOn());
		suprelMaster.setPrimaryNonConformity(suprelMasterDto.getPrimaryNonConformity());
		suprelMaster.setSecondaryNonConformity(suprelMasterDto.getSecondaryNonConformity());
		suprelMaster.setState(suprelMasterDto.getState());
		suprelMaster.setSupplierAddress(suprelMasterDto.getSupplierAddress());
		suprelMaster.setSupplierCode(suprelMasterDto.getSupplierCode());
		suprelMaster.setSupplierName(suprelMasterDto.getSupplierName());
		suprelMaster.setSuprelNo(suprelMasterDto.getSuprelNo());
		suprelMaster.setSuprelStatus(suprelMasterDto.getSuprelStatus());
		suprelMaster.setTechnicalArea(suprelMasterDto.getTechnicalArea());
		suprelMaster.setManagerName(suprelMasterDto.getManagerName());
		suprelMaster.setOriginatorEmail(suprelMasterDto.getOriginatorEmail());
		suprelMaster.setOriginatorName(suprelMasterDto.getOriginatorName());
		suprelMaster.setPostalCode(suprelMasterDto.getPostalCode());
		suprelMaster.setManagerComments(suprelMasterDto.getManagerComments());
		suprelMaster.setSupplierComments(suprelMasterDto.getSupplierComments());
		suprelMaster.setUserEmail1(suprelMasterDto.getUserEmail1());
		suprelMaster.setUserEmail2(suprelMasterDto.getUserEmail2());
		suprelMaster.setUserEmail3(suprelMasterDto.getUserEmail3());
		suprelMaster.setUserEmail4(suprelMasterDto.getUserEmail4());
		suprelMaster.setUserEmail5(suprelMasterDto.getUserEmail5());
		
		return suprelMaster;
		
	}
	
	/**
	 * 
	 * @param suprelNo
	 * @param suprelStatus
	 * @param supplierComments
	 * @return
	 */
	public SuprelMaster getSuprelDetails(StatusChangeDetails details) {
		String role = details.getRole();
		String suprelNo = details.getSuprelNo();
		SuprelMaster suprelMaster = suprelOriginatorRepository.getOne(suprelNo);

		String supplierToEmail = getEmailInfo(suprelMaster.getSupplierCode());
		String supplierToEmail1 = getEmailDetailsForScode(suprelMaster.getSupplierCode());
		supplierToEmail = concatEmails(supplierToEmail, supplierToEmail1, suprelMaster.getEmail1(), suprelMaster.getEmail2(), 
				suprelMaster.getEmail3(), suprelMaster.getEmail4(), suprelMaster.getEmail5());
		String ccEmail = concatCCEmails(suprelMaster.getOriginatorEmail(), suprelMaster.getUserEmail1(),suprelMaster.getUserEmail2(),
				suprelMaster.getUserEmail3(),suprelMaster.getUserEmail4(),suprelMaster.getUserEmail5());
		if(role.equals("Manager")) {
			suprelMaster = sendManagerRoleNotifications(details, suprelMaster, supplierToEmail, ccEmail);
		}else if(role.equals(SUPPLIER)) {
			suprelMaster = sendSupplierRoleNoti1fications(details, suprelMaster, supplierToEmail, ccEmail);
		}else if(role.equals("Admin")) {
			suprelMaster = sendAdminNotifications(details, suprelMaster, supplierToEmail, ccEmail);
		}

		suprelMaster.setPrimaryNonConformity(details.getPrimaryNonConformity());
		suprelMaster.setSecondaryNonConformity(details.getSecondaryNonConformity());
		suprelMaster.setTechnicalArea(details.getTechnicalArea());
		suprelMaster.setAmount(details.getAmount());
		suprelMaster.setCurrencyType(details.getCurrencyType());
		suprelMaster.setDetails(details.getDetails());
		suprelMaster.setModifiedBy(details.getModifiedBy());
		suprelMaster.setModifiedOn(details.getModifiedOn());

		return suprelOriginatorRepository.save(suprelMaster);
	}	
	
	/**
	 * Suprel Number generation Logic
	 * @param year
	 * @param locationcode
	 * @return
	 */
	public String getRandomValue(int year) {
		List<SuprelNumbers> suprelList =  suprelNumbersRepository.getClaimRecordsOnCreatedDate(year);
		String suprelNo;
		if(CollectionUtils.isNullOrEmpty(suprelList)) {
			suprelNo = "0";
		}else {
			suprelNo = suprelList.get(0).getSuprelNo().split("-")[1].split("\\s+")[0];
		}
		int randomValue = Integer.parseInt(suprelNo)+1;
		String randomString = String.format("%05d", randomValue);
		return year+"-"+randomString;
	}

	public SuprelAttachments getSuprelAttachment(String suprelNo,String supplierCode, String filename) {
		return suprelAttachmentRepository.getSuprelAttachment(supplierCode,filename,suprelNo);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<SuprelMaster> sendDraftEmailNotification() {
		
		return suprelOriginatorRepository.sendDraftEmailNotification();
	}
	
	public List<SuprelMaster> sendPendingEmailNotification() {
		
		return suprelOriginatorRepository.sendPendingEmailNotification();
	}

	public List<SuprelMaster> sendPendingEmailNotification1() {

		return suprelOriginatorRepository.sendPendingEmailNotification1();
	}

	public List<SuprelMaster> sendDisputedEmailNotification() {

		return suprelOriginatorRepository.sendDisputedEmailNotification();
	}
	
	public List<SuprelMaster> sendIssuedEmailNotification() {

		return suprelOriginatorRepository.sendIssuedEmailNotification();
	}
	
	public List<SuprelMaster> sendIssuedEmailNotificationForFiveDays() {
		
		return suprelOriginatorRepository.sendIssuedEmailNotificationForFiveDays();
	}
	
	/**
	 * 
	 * @param suprelCode
	 * @return
	 * @throws Exception 
	 */
	public SuprelSISData getSuprelSISData(String supplierCode){
		
		List<SuprelSISData> sisList =  suprelOriginatorRepository.getSuprelSISData(supplierCode);
		if(!CollectionUtils.isNullOrEmpty(sisList)) {
			return sisList.get(0);
		}else {
			throw new NullPointerException();
		}
	}
	
	
	public SuprelManager getManagerDetails(String managerName) {
		return suprelOriginatorRepository.getManagerDetails(managerName);
	}
	
	public SuprelManager getManagerDetailsTid(String tid) {
		return suprelOriginatorRepository.getManagerDetailsTid(tid);
	}
	
	public List<SuprelMaster> pendingCertification(String managerName) {
		 return suprelOriginatorRepository.pendingCertification(managerName); 
	}
	
	public List<SuprelAttachments> getSuprelAttachmentsList(String suprelNo){
		return suprelAttachmentRepository.getAttachmentsById(suprelNo);
	}
	
	public String getEmailInfo(String supplierCode) {
		return suprelOriginatorRepository.getEmailDetails(supplierCode);
	}
	
	public String getEmailDetailsForScode(String supplierCode) {
		return suprelOriginatorRepository.getEmailDetailsForScode(supplierCode);
	}
	
	public List<String> getStateList(){
		return suprelOriginatorRepository.getStateList();
	}
	
	public List<String> getCountryList(){
		return suprelOriginatorRepository.getCountryList();
	}
	
	/**
	 * 
	 * @param supplierToEmail
	 * @param email1
	 * @param email2
	 * @param email3
	 * @param email4
	 * @param email5
	 * @return
	 */
	public String concatEmails(String supplierToEmail, String supplierToEmail1, String email1, String email2, String email3, String email4, String email5) {
		StringBuilder sb = new StringBuilder();
		if(StringUtils.hasText(email1))
			sb.append(email1);
		if(StringUtils.hasText(email2)) {
			sb.append(",");
			sb.append(email2);
		}
		if(StringUtils.hasText(email3)) {
			sb.append(",");
			sb.append(email3);
		}
		if(StringUtils.hasText(email4)) {
			sb.append(",");
			sb.append(email4);
		}
		if(StringUtils.hasText(email5)) {
			sb.append(",");
			sb.append(email5);
		}
		if(StringUtils.hasText(supplierToEmail)) {
			sb.append(",");
			sb.append(supplierToEmail);
		}
		if(StringUtils.hasText(supplierToEmail1)) {
			sb.append(",");
			sb.append(supplierToEmail1);
		}
		return sb.toString();
	}
	
	public String concatCCEmails(String originatorEmail,String userEmail1,String userEmail2,String userEmail3,String userEmail4,String userEmail5) {
		
		StringBuilder sb = new StringBuilder();
		if(StringUtils.hasText(originatorEmail)) {
			sb.append(originatorEmail);
		}
		if(StringUtils.hasText(userEmail1)) {
			sb.append(",");
			sb.append(userEmail1);
		}
		if(StringUtils.hasText(userEmail2)) {
			sb.append(",");
			sb.append(userEmail2);
		}
		if(StringUtils.hasText(userEmail3)) {
			sb.append(",");
			sb.append(userEmail3);
		}
		if(StringUtils.hasText(userEmail4)) {
			sb.append(",");
			sb.append(userEmail4);
		}
		if(StringUtils.hasText(userEmail5)) {
			sb.append(",");
			sb.append(userEmail5);
		}
		
		return sb.toString();
	}
	
	
	/**
	 * 
	 * @param suprelMaster
	 * @param managerName
	 */
	public void pendingStatusMail(SuprelMaster suprelMaster, String managerName) {
		SuprelManager managerDetails = getManagerDetails(managerName);
		EmailUtil.sendEmail(suprelMaster.getOriginatorEmail(), managerDetails.getManagerEmail(),"",
				CERTNUMCONSTANT + suprelMaster.getSuprelNo() + " - " + PENDING,
				IMAGE+"<br><br>Hi," + "<br><br> A SUPREL Incident Claim has been submitted for your approval. "
						+ "<br><br> Incident #"+suprelMaster.getSuprelNo()
						+ "<br><br> Supplier Code:"+suprelMaster.getSupplierCode()
						+ "<br><br> Supplier Name:"+suprelMaster.getSupplierName()
						+ "<br><br> Primary Category:"+suprelMaster.getPrimaryNonConformity()
						+ "<br><br> Secondary Category:"+suprelMaster.getSecondaryNonConformity()
						+ "<br><br> Incident Date:"+suprelMaster.getCreatedOn()
						+ "<br><br> Open this link to approve or reject the claim. "+url+"incident-claims/"+suprelMaster.getSuprelNo()+"/"+managerDetails.getManagerTid()
						+ "<br><br><b> Note: Do not reply to this system generated email notification</b>", host);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<SuprelManager> getManagerNamesList() {
		return managerRepo.findAll();
	}
	
/**	public List<Object> getManagerNamesList() {
		List<Object> objList = new ArrayList<>();
		List<SuprelManager> managerList =  managerRepo.findAll();
		Object object1 = "";
		//Fetching the Manager Name from LDAP using User TID
		try {
			String tid = (String) httpSession.getAttribute("userName");
			if(tid!=null) {
				JSONObject obj1 = LDAPConnection.getUserBasicAttributes(tid);
				JSONObject manager = (JSONObject) obj1.get("manager");
				if(manager!=null) {
					String managerFName = manager.get("managerFName").toString();
					String managerLname = manager.get("managerLName").toString();
					String managerName = managerFName.toUpperCase() + " " + managerLname.toUpperCase();
					String managerTid = manager.get("managerTid").toString();	
					object1 = managerTid;
					String managerEmail = manager.get("managerEmail").toString();
					SuprelManager supManager = new SuprelManager();
					Boolean tidFound = managerList.stream().anyMatch(p -> p.getManagerTid().equalsIgnoreCase(managerTid));
					if(Boolean.FALSE.equals(tidFound)) {
						supManager.setManagerName(managerName);
						supManager.setManagerEmail(managerEmail);
						supManager.setManagerTid(managerTid);
						//saving this manager detail in SUPREL manager table
						managerRepo.save(supManager);
						//Adding to the mangerList sent to the front end
						managerList.add(supManager);
					}
				}
			}
		}catch(Exception e) {
			LOGGER.error(CONTEXT, e);
		}
		HashMap<String, Object> map = new HashMap<>();
		map.put("SelectedTID", object1);
		objList.add(map);
		Object object2 = managerList;
		objList.add(object2);
		return objList;
	}
	**/
	/**
	 * 
	 * @param suprelNo
	 * @return
	 */
	public List<SuprelAuditLog> getAuditLogDetailsForSuprelNum(String suprelNo) {
		return suprelAuditLogRepository.getAuditLogDetailsForSuprelNum(suprelNo);
	}
	
	/**
	 * 
	 * @param details
	 * @param suprelMaster
	 * @return
	 */
	public SuprelMaster sendManagerRoleNotifications(StatusChangeDetails details, SuprelMaster suprelMaster, String supplierToEmail, String ccEmail) {
		String suprelStatus = details.getSuprelStatus();
		String comments = details.getComments();
		String managerName = details.getManagerName();

		SuprelManager managerDetails = getManagerDetails(managerName);

		if(suprelStatus.equals(ISSUED) && suprelMaster.getSuprelStatus().equals(DISPUTED)) {//Disputed claim accepted by Manager
			//in this case email from manger to originator and to supplier as well.
			EmailUtil.sendEmail(managerDetails.getManagerEmail(), supplierToEmail, ccEmail,
					CERTNUMCONSTANT + suprelMaster.getSuprelNo() + " - " + CANCELLED, 
					IMAGE+"<br><br>Hi," + "<br><br> A SUPREL Incident Claim dispute has been accepted by Stellantis. "
							+ "<br><br> Incident #"+suprelMaster.getSuprelNo()
							+ "<br><br> Supplier Code:"+suprelMaster.getSupplierCode()
							+ "<br><br> Supplier Name:"+suprelMaster.getSupplierName()
							+ "<br><br> Primary Category:"+suprelMaster.getPrimaryNonConformity()
							+ "<br><br> Secondary Category:"+suprelMaster.getSecondaryNonConformity()
							+ "<br><br> Incident Date:"+suprelMaster.getCreatedOn()
							+ "<br><br> Incident Status: Cancelled"
							+ "<br><br> Suppliers may access the SUPREL Incident Claim system through eSupplierConnect to view incident claim details."
							+ "<br><br><b> Note: Do not reply to this system generated email notification</b>", host);
			suprelMaster.setManagerComments(comments);
			suprelMaster.setSuprelStatus(CANCELLED);
		}
		else if(suprelStatus.equals("Rejected") && suprelMaster.getSuprelStatus().equals(DISPUTED)) {//Disputed claim Rejected by Manager
			EmailUtil.sendEmail(managerDetails.getManagerEmail(), supplierToEmail, ccEmail,
					CERTNUMCONSTANT + suprelMaster.getSuprelNo() + " - " + FINALEBSC,
					IMAGE+"<br><br>Hi," + "<br><br> A SUPREL Incident Claim dispute has been rejected by Stellantis. "
							+ "<br><br> Incident #"+suprelMaster.getSuprelNo()
							+ "<br><br> Supplier Code:"+suprelMaster.getSupplierCode()
							+ "<br><br> Supplier Name:"+suprelMaster.getSupplierName()
							+ "<br><br> Primary Category:"+suprelMaster.getPrimaryNonConformity()
							+ "<br><br> Secondary Category:"+suprelMaster.getSecondaryNonConformity()
							+ "<br><br> Incident Date:"+suprelMaster.getCreatedOn()
							+ "<br><br> Reason for Rejection:"+comments
							+ "<br><br> Incident Status: Final"
							+ "<br><br> Access the SUPREL Incident Claim system through eSupplierConnect to view the incident claim for more details"
							+ "<br><br><b> Note: Do not reply to this system generated email notification</b>", host);
			suprelMaster.setSuprelStatus(FINALEBSC);
			suprelMaster.setManagerComments(comments);
		}
		else if(suprelStatus.equals("Rejected") && suprelMaster.getSuprelStatus().equals(PENDING)){//Rejected by manager
			EmailUtil.sendEmail(managerDetails.getManagerEmail(), ccEmail,"",
					CERTNUMCONSTANT + suprelMaster.getSuprelNo() + " - " + suprelStatus,
					IMAGE+"<br><br>Hi," + "<br><br> A SUPREL Incident Claim has been rejected by Stellantis. "
							+ "<br><br> Incident #"+suprelMaster.getSuprelNo()
							+ "<br><br> Supplier Code:"+suprelMaster.getSupplierCode()
							+ "<br><br> Supplier Name:"+suprelMaster.getSupplierName()
							+ "<br><br> Primary Category:"+suprelMaster.getPrimaryNonConformity()
							+ "<br><br> Secondary Category:"+suprelMaster.getSecondaryNonConformity()
							+ "<br><br> Incident Date:"+suprelMaster.getCreatedOn()
							+ "<br><br> Reason for Rejection:"+comments
							+ "<br><br> Open this link to edit and resubmit, or delete the claim. "+url
							+ "<br><br><b> Note: Do not reply to this system generated email notification</b>", host);
			suprelMaster.setManagerComments(comments);	
			suprelMaster.setSuprelStatus(DRAFT);
		} 
		else if(suprelStatus.equals(ISSUED) && suprelMaster.getSuprelStatus().equals(PENDING)) {//issued by manager
			suprelMaster.setIssuedDate(new Date());
			//in this case email from manger to originator and to supplier as well.
        if(suprelMaster.getPrimaryNonConformity().equalsIgnoreCase("Customer Containment") || suprelMaster.getPrimaryNonConformity().equalsIgnoreCase("Preventive In Field Action")) {
				
				EmailUtil.sendEmail(managerDetails.getManagerEmail(), supplierToEmail, ccEmail,
						"Supplier Relationship Incident Claim - "+CERTNUMCONSTANT+suprelMaster.getSuprelNo(),
						IMAGE+"<br><br> Supplier Code:"+suprelMaster.getSupplierCode()
						+"<br><br> Supplier Name:"+suprelMaster.getSupplierName()
						+"<br><br> City:"+suprelMaster.getCity()
						+"<br><br> State:"+suprelMaster.getState()
						+"<br><br> Country:"+suprelMaster.getCountry()
						+ "<br><br> Primary Category:"+suprelMaster.getPrimaryNonConformity()
						+ "<br><br> Secondary Category:"+suprelMaster.getSecondaryNonConformity()
						+ "<br><br> Dear Supplier:"
						+ "<br><br>Your manufacturing plant has been assessed a penalty associated with the primary non-conformity listed in this notification."

							+ "<br><br>Please review the Incident Claim details in the SUPREL application and accept or dispute the claim within 10 calendar days."

							+ "<br><br>Access the SUPREL Incident Claim system to view and respond to the incident claim."

							+ "<br><br><b> Note: Do not reply to this system generated email notification</b>",host);
				
			}else if(suprelMaster.getPrimaryNonConformity().equalsIgnoreCase("SQD Escalation") && (suprelMaster.getSecondaryNonConformity().equalsIgnoreCase("Escalation Level 1") || suprelMaster.getSecondaryNonConformity().equalsIgnoreCase("Escalation Level 2"))) {

				EmailUtil.sendEmail(managerDetails.getManagerEmail(), supplierToEmail, ccEmail,
						"Supplier Relationship Incident Claim - "+CERTNUMCONSTANT+suprelMaster.getSuprelNo(),
						IMAGE+"<br><br> Supplier Code:"+suprelMaster.getSupplierCode()
						+"<br><br> Supplier Name:"+suprelMaster.getSupplierName()
						+"<br><br> City:"+suprelMaster.getCity()
						+"<br><br> State:"+suprelMaster.getState()
						+"<br><br> Country:"+suprelMaster.getCountry()
						+ "<br><br> Primary Category:"+suprelMaster.getPrimaryNonConformity()
						+ "<br><br> Secondary Category:"+suprelMaster.getSecondaryNonConformity()
						+ "<br><br> Dear Supplier:"
						+ "<br><br> Based on the performance(s) of your plant, Stellantis has decided that your plant will enter the Escalation process. "
						+ "Consequently, an Incident Claim has been initialized with impact on your scoring Bidlist."
							+ "<br><br>The Escalation Process will be closed when significant performance and systemic process improvements are demonstrated "
							+ "in line with the exit criteria as defined in the attached Quad Report."
	
							+ "<br><br>Access the SUPREL Incident Claim system to view and download a copy of the required actions and timing. "

							+ "<br><br><b> Note: Do not reply to this system generated email notification</b>",host);
				suprelMaster.setManagerComments(comments);
				suprelMaster.setSuprelStatus(FINALEBSC);	
				return suprelMaster;

			} else if(suprelMaster.getPrimaryNonConformity().equalsIgnoreCase("SQD Escalation") && suprelMaster.getSecondaryNonConformity().equalsIgnoreCase("Group NBH")){

				EmailUtil.sendEmail(managerDetails.getManagerEmail(), supplierToEmail, ccEmail,
						"Supplier Relationship Incident Claim - "+CERTNUMCONSTANT+suprelMaster.getSuprelNo(),
						IMAGE+"<br><br> Supplier Code:"+suprelMaster.getSupplierCode()
						+"<br><br> Supplier Name:"+suprelMaster.getSupplierName()
						+"<br><br> City:"+suprelMaster.getCity()
						+"<br><br> State:"+suprelMaster.getState()
						+"<br><br> Country:"+suprelMaster.getCountry()
						+ "<br><br> Primary Category:"+suprelMaster.getPrimaryNonConformity()
						+ "<br><br> Secondary Category:"+suprelMaster.getSecondaryNonConformity()
						+ "<br><br> Dear Supplier:"
						+ "<br><br> It has been established that a unit, division, or all manufacturing plants for your company have been placed in New Business Hold (NBH) status. "
						+ "Consequently, an Incident Claim has been initialized with impact on your scoring Bidlist."

							+ "<br><br>The NBH will be closed only when significant performance and systemic process improvements are demonstrated in line with the criteria established by Stellantis Leadership."

							+ "<br><br>There is no dispute action allowed for NBH status. "

							+ "<br><br><b> Note: Do not reply to this system generated email notification</b>",host);

				suprelMaster.setManagerComments(comments);
				suprelMaster.setSuprelStatus(FINALEBSC);	
				return suprelMaster;
			} else if(suprelMaster.getPrimaryNonConformity().equalsIgnoreCase("SQD Escalation") && suprelMaster.getSecondaryNonConformity().equalsIgnoreCase("Warning")){

				EmailUtil.sendEmail(managerDetails.getManagerEmail(), supplierToEmail, ccEmail,
						"Supplier Relationship Incident Claim - "+CERTNUMCONSTANT+suprelMaster.getSuprelNo(),
						IMAGE+"<br><br> Supplier Code:"+suprelMaster.getSupplierCode()
						+"<br><br> Supplier Name:"+suprelMaster.getSupplierName()
						+"<br><br> City:"+suprelMaster.getCity()
						+"<br><br> State:"+suprelMaster.getState()
						+"<br><br> Country:"+suprelMaster.getCountry()
						+ "<br><br> Primary Category:"+suprelMaster.getPrimaryNonConformity()
						+ "<br><br> Secondary Category:"+suprelMaster.getSecondaryNonConformity()
						+ "<br><br> Dear Supplier:"
						+ "<br><br> Your manufacturing location did not achieve satisfactory performance results during the past 3 months and is a TOP concern site for Stellantis Group for your commodity."
						+ "As a result, we require you to propose an efficient solution for the issues referenced in the attached Warning letter."

							+ "<br><br>You will have 10 calendar days to develop a robust systemic action plan to share with the Stellantis team. "
							
							+ "<br><br>Access the SUPREL Incident Claim system to view incident details and to download a copy of the Warning letter. "

							+ "<br><br><b> Note: Do not reply to this system generated email notification</b>",host);
				suprelMaster.setManagerComments(comments);
				suprelMaster.setSuprelStatus(FINALEBSC);	
				return suprelMaster;

			}else if(suprelMaster.getPrimaryNonConformity().equalsIgnoreCase("Cost Recovery") && suprelMaster.getSecondaryNonConformity().equalsIgnoreCase("SQD Escalation Level 2")){

				EmailUtil.sendEmail(managerDetails.getManagerEmail(), supplierToEmail, ccEmail,
						"Supplier Relationship Incident Claim - "+CERTNUMCONSTANT+suprelMaster.getSuprelNo(),
						IMAGE+"<br><br> Supplier Code:"+suprelMaster.getSupplierCode()
						+"<br><br> Supplier Name:"+suprelMaster.getSupplierName()
						+"<br><br> City:"+suprelMaster.getCity()
						+"<br><br> State:"+suprelMaster.getState()
						+"<br><br> Country:"+suprelMaster.getCountry()
						+ "<br><br> Primary Category:"+suprelMaster.getPrimaryNonConformity()
						+ "<br><br> Secondary Category:"+suprelMaster.getSecondaryNonConformity()
						+ "<br><br> Dear Supplier:"
						+ "<br><br> Your manufacturing plant has been charged with Stellantis incurred costs associated with a recent SQD Escalation Level 2 Incident Claim."

							+ "<br><br>Please review the cost breakdown attached to the Incident Claim in the SUPREL application and accept or dispute the assigned cost within 10 calendar days."

							+ "<br><br>Access the SUPREL Incident Claim system to view and respond to the incident claim."

							+ "<br><br><b> Note: Do not reply to this system generated email notification</b>",host);


			}else {
				EmailUtil.sendEmail(managerDetails.getManagerEmail(), supplierToEmail, ccEmail,
						"Supplier Relationship Incident Claim - "+CERTNUMCONSTANT+suprelMaster.getSuprelNo(),
						IMAGE+"<br><br> Supplier Code:"+suprelMaster.getSupplierCode()
						+"<br><br> Supplier Name:"+suprelMaster.getSupplierName()
						+"<br><br> City:"+suprelMaster.getCity()
						+"<br><br> State:"+suprelMaster.getState()
						+"<br><br> Country:"+suprelMaster.getCountry()
						+ "<br><br> Primary Category:"+suprelMaster.getPrimaryNonConformity()
						+ "<br><br> Secondary Category:"+suprelMaster.getSecondaryNonConformity()
						+ "<br><br> Dear Supplier:"
						+ "<br><br> This letter serves as formal notification that Stellantis has issued a SUPREL (Supplier Relationship Incident Claim) for your supplier plant."
						+ " Details of the claim and the associated action(s) can be viewed within the SUPREL application."
						+ "<br><br> If your company does not dispute the SUPREL incident within 10 calendar days, Stellantis will assume that you are accepting the claim and "
						+ " will adjust your Incoming Material Quality (IMQ) score within the Global External Balanced Scorecard (GEBSC) based on the severity of the infraction."

						+ "<br><br>To avoid further SUPREL claims and IMQ infractions: "

						+ "<br><br>1.	Ensure Forever Requirement (FR) notifications are entered into the Stellantis Web-based Change Notice system (WebCN) and approved by Stellantis prior to implementation"
						+ "<br><br>2.	Provide a 90-day advance notification of proposed changes to manufacturing or shipping location; Tier 2 location change; or product or process change."
						+ "<br><br>3.	Meet all milestones and timing requirements; tasks and action plans"

						+ "<br><br>4.	Maintain acceptable IMQ and Warranty performance"

						+ "<br><br>5.	Notify Stellantis in advance of non-conforming material shipped to a customer plant"

						+ "<br><br> Access the SUPREL Incident Claim system through eSupplierConnect to view and respond to the incident claim."
						+ "<br><br><b> Note: Do not reply to this system generated email notification</b>", host);
			}
			suprelMaster.setManagerComments(comments);
			suprelMaster.setSuprelStatus(suprelStatus);	
		}

		else if(suprelStatus.equals(CANCELLED)) {// cancelled by manager
			EmailUtil.sendEmail(managerDetails.getManagerEmail(), ccEmail,"",
					CERTNUMCONSTANT + suprelMaster.getSuprelNo() + " - " + CANCELLED,
					IMAGE+"<br><br> Hi," + "<br><br> A SUPREL Incident Claim has been cancelled by stellantis."
							+ "<br><br> Incident #"+suprelMaster.getSuprelNo()
							+ "<br><br> Supplier Code:"+suprelMaster.getSupplierCode()
							+ "<br><br> Supplier Name:"+suprelMaster.getSupplierName()
							+ "<br><br> Primary Category:"+suprelMaster.getPrimaryNonConformity()
							+ "<br><br> Secondary Category:"+suprelMaster.getSecondaryNonConformity()
							+ "<br><br> Incident Date:"+suprelMaster.getCreatedOn()
							+ "<br><br> Incident Status: Cancelled"
							+ "<br><br> Reason for Cancellation:"+comments
							+ "<br><br> Access the SUPREL Incident Claim system to view incident claim details."
							+ "<br><br><b> Note: Do not reply to this system generated email notification</b>", host);
			suprelMaster.setManagerComments(comments);
			suprelMaster.setSuprelStatus(suprelStatus);
		}


		return suprelMaster;
	}
	
	/**
	 * 
	 * @param details
	 * @param suprelMaster
	 * @return
	 */
	public SuprelMaster sendSupplierRoleNoti1fications(StatusChangeDetails details, SuprelMaster suprelMaster, String supplierToEmail, String ccEmail) {
		String suprelStatus = details.getSuprelStatus();
		String comments = details.getComments();
		String managerName = details.getManagerName();

		SuprelManager managerDetails = getManagerDetails(managerName);

		if(suprelStatus.equals(FINALEBSC)){// Accepted by Supplier
			EmailUtil.sendEmail(ADMINMAIL,ccEmail,managerDetails.getManagerEmail(),
					CERTNUMCONSTANT + suprelMaster.getSuprelNo() + " - " + FINALEBSC,
					IMAGE+"<br><br>Hi," + "<br><br> A SUPREL Incident Claim has been accepted by the supplier. "
							+ "<br><br> Incident #"+suprelMaster.getSuprelNo()
							+ "<br><br> Supplier Code:"+suprelMaster.getSupplierCode()
							+ "<br><br> Supplier Name:"+suprelMaster.getSupplierName()
							+ "<br><br> Primary Category:"+suprelMaster.getPrimaryNonConformity()
							+ "<br><br> Secondary Category:"+suprelMaster.getSecondaryNonConformity()
							+ "<br><br> Incident Date:"+suprelMaster.getCreatedOn()
							+ "<br><br> Incident Status: Final"
							+ "<br><br> Access the SUPREL Incident Claim system to view incident claim details. "+url
							+ "<br><br><b> Note: Do not reply to this system generated email notification</b>", host);
			suprelMaster.setSupplierComments(null);
			suprelMaster.setSuprelStatus(FINALEBSC);
		}

		else if(suprelStatus.equals(DISPUTED)) {// Disputed by Supplier
			EmailUtil.sendEmail(ADMINMAIL,managerDetails.getManagerEmail(), ccEmail,
					CERTNUMCONSTANT + suprelMaster.getSuprelNo() + " - " + DISPUTED,
					IMAGE+"<br><br>Hi," + "<br><br> A SUPREL Incident Claim has been disputed by the supplier. "
							+ "<br><br> Incident #"+suprelMaster.getSuprelNo()
							+ "<br><br> Supplier Code:"+suprelMaster.getSupplierCode()
							+ "<br><br> Supplier Name:"+suprelMaster.getSupplierName()
							+ "<br><br> Primary Category:"+suprelMaster.getPrimaryNonConformity()
							+ "<br><br> Secondary Category:"+suprelMaster.getSecondaryNonConformity()
							+ "<br><br> Incident Date:"+suprelMaster.getCreatedOn()
							+ "<br><br> Incident Status: Disputed"
							+ "<br><br> Supplier Comment:"+comments
							+ "<br><br> Open this link to accept or reject the dispute. "+url+"incident-claims/"+suprelMaster.getSuprelNo()+"/"+managerDetails.getManagerTid()
							+ "<br><br><b> Note: Do not reply to this system generated email notification</b>", host);
			suprelMaster.setSuprelStatus(DISPUTED);
			suprelMaster.setSupplierComments(comments);
		}
		return suprelMaster;
	}
	
	/**
	 * 
	 * @param details
	 * @param suprelMaster
	 * @return
	 */
	public SuprelMaster sendAdminNotifications(StatusChangeDetails details, SuprelMaster suprelMaster, String supplierToEmail, String ccEmail) {
		String suprelStatus = details.getSuprelStatus();
		String managerName = details.getManagerName();
		
		SuprelManager managerDetails = getManagerDetails(managerName);
		if(suprelStatus.equals(CLOSED)) {// Admin
			if(!suprelStatus.equals(suprelMaster.getSuprelStatus())) {
				EmailUtil.sendEmail(ADMINMAIL, supplierToEmail, ccEmail+","+managerDetails.getManagerEmail(),
						CERTNUMCONSTANT + suprelMaster.getSuprelNo() + " - " + CLOSED,
						IMAGE+"<br><br>Hi," + "<br><br> A SUPREL Incident Claim has been Closed by Stellantis with no further penalty impact"
								+ " to the Global External Balanced Scorecard. "
								+ "<br><br> Incident #"+suprelMaster.getSuprelNo()
								+ "<br><br> Supplier Code:"+suprelMaster.getSupplierCode()
								+ "<br><br> Supplier Name:"+suprelMaster.getSupplierName()
								+ "<br><br> Primary Category:"+suprelMaster.getPrimaryNonConformity()
								+ "<br><br> Secondary Category:"+suprelMaster.getSecondaryNonConformity()
								+ "<br><br> Incident Date:"+suprelMaster.getCreatedOn()
								+ "<br><br> Incident Status: Closed"
								+ "<br><br> Suppliers may access the SUPREL Incident Claim system through eSupplierConnect to view incident claim details."
								+ "<br><br><b> Note: Do not reply to this system generated email notification</b>", host);
			}
			suprelMaster.setSuprelStatus(suprelStatus);
		}
		else if(suprelStatus.equals(ISSUED)) {// Admin
			if(!suprelStatus.equals(suprelMaster.getSuprelStatus())) {
				EmailUtil.sendEmail(ADMINMAIL, supplierToEmail, ccEmail+","+managerDetails.getManagerEmail(),
						CERTNUMCONSTANT + suprelMaster.getSuprelNo() + " - " + ISSUED,
						IMAGE+"<br><br>Hi," + "<br><br> A SUPREL Incident Claim status has been changed to issued by Stellantis."
								+ "<br><br> Incident #"+suprelMaster.getSuprelNo()
								+ "<br><br> Supplier Code:"+suprelMaster.getSupplierCode()
								+ "<br><br> Supplier Name:"+suprelMaster.getSupplierName()
								+ "<br><br> Primary Category:"+suprelMaster.getPrimaryNonConformity()
								+ "<br><br> Secondary Category:"+suprelMaster.getSecondaryNonConformity()
								+ "<br><br> Incident Date:"+suprelMaster.getCreatedOn()
								+ "<br><br> Incident Status: Issued"
								+ "<br><br> Access the SUPREL Incident Claim system through eSupplierConnect to view and respond to the incident claim."
								+ "<br><br><b> Note: Do not reply to this system generated email notification</b>", host);
			}
			suprelMaster.setSuprelStatus(suprelStatus);
		}
		else if(suprelStatus.equals(CANCELLED)) {// cancelled by Admin
			if(!suprelStatus.equals(suprelMaster.getSuprelStatus())) {
				EmailUtil.sendEmail(ADMINMAIL, supplierToEmail, ccEmail+","+managerDetails.getManagerEmail(),
						CERTNUMCONSTANT + suprelMaster.getSuprelNo() + " - " + CANCELLED,
						IMAGE+"<br><br> Hi," + "<br><br> A SUPREL Incident Claim has been cancelled by Stellantis."
								+ "<br><br> Incident #"+suprelMaster.getSuprelNo()
								+ "<br><br> Supplier Code:"+suprelMaster.getSupplierCode()
								+ "<br><br> Supplier Name:"+suprelMaster.getSupplierName()
								+ "<br><br> Primary Category:"+suprelMaster.getPrimaryNonConformity()
								+ "<br><br> Secondary Category:"+suprelMaster.getSecondaryNonConformity()
								+ "<br><br> Incident Date:"+suprelMaster.getCreatedOn()
								+ "<br><br> Incident Status: Cancelled"
								+ "<br><br> Suppliers may access the SUPREL Incident Claim system through eSupplierConnect to view incident claim details."
								+ "<br><br><b> Note: Do not reply to this system generated email notification</b>", host);
			}
			suprelMaster.setSuprelStatus(suprelStatus);
		}

		else if(suprelStatus.equals(FINALEBSC)) {//Admin
			if(!suprelStatus.equals(suprelMaster.getSuprelStatus())) {
				EmailUtil.sendEmail(ADMINMAIL,supplierToEmail, ccEmail+","+managerDetails.getManagerEmail(),
						CERTNUMCONSTANT + suprelMaster.getSuprelNo() + " - " +FINALEBSC,
						IMAGE+"<br><br>Hi," + "<br><br> A SUPREL Incident Claim status has been changed to Final by Stellantis. "
								+ "<br><br> Incident #"+suprelMaster.getSuprelNo()
								+ "<br><br> Supplier Code:"+suprelMaster.getSupplierCode()
								+ "<br><br> Supplier Name:"+suprelMaster.getSupplierName()
								+ "<br><br> Primary Category:"+suprelMaster.getPrimaryNonConformity()
								+ "<br><br> Secondary Category:"+suprelMaster.getSecondaryNonConformity()
								+ "<br><br> Incident Date:"+suprelMaster.getCreatedOn()
								+ "<br><br> Incident Status: Final"
								+ "<br><br> Suppliers may access the SUPREL Incident Claim system through eSupplierConnect to view incident claim details."
								+ "<br><br><b> Note: Do not reply to this system generated email notification</b>", host);
			}
			suprelMaster.setSuprelStatus(suprelStatus);
		}
		else if(suprelStatus.equals(DRAFT)) {//Admin
			if(!suprelStatus.equals(suprelMaster.getSuprelStatus())) {
				EmailUtil.sendEmail(ADMINMAIL,suprelMaster.getOriginatorEmail(), supplierToEmail+","+managerDetails.getManagerEmail(),
						CERTNUMCONSTANT + suprelMaster.getSuprelNo() + " - " + suprelStatus,
						IMAGE+"<br><br>Hi," + "<br><br> A SUPREL Incident Claim status has been changed to Draft by Stellantis. "
								+ "<br><br> Incident #"+suprelMaster.getSuprelNo()
								+ "<br><br> Supplier Code:"+suprelMaster.getSupplierCode()
								+ "<br><br> Supplier Name:"+suprelMaster.getSupplierName()
								+ "<br><br> Primary Category:"+suprelMaster.getPrimaryNonConformity()
								+ "<br><br> Secondary Category:"+suprelMaster.getSecondaryNonConformity()
								+ "<br><br> Incident Date:"+suprelMaster.getCreatedOn()
								+ "<br><br> Incident Status:"+suprelStatus
								+ "<br><br> Open this link to view the Incident details. "+url
								+ "<br><br><b> Note: Do not reply to this system generated email notification</b>", host);
				suprelMaster.setManagerComments(null);
			}
			suprelMaster.setSuprelStatus(suprelStatus);
		} 

		if(managerName!= null && !managerName.equals(suprelMaster.getManagerName())) {
			suprelMaster.setManagerName(managerName);
			if(suprelStatus.equals(PENDING) || suprelStatus.equals(DISPUTED)){
				EmailUtil.sendEmail(ADMINMAIL,managerDetails.getManagerEmail(),ccEmail,
						CERTNUMCONSTANT + suprelMaster.getSuprelNo(),
						IMAGE+"<br><br>Hi," + "<br><br> A SUPREL Incident Claim has been assigned to you for review by Stellantis"
								+ "<br><br> Incident #"+suprelMaster.getSuprelNo()
								+ "<br><br> Supplier Code:"+suprelMaster.getSupplierCode()
								+ "<br><br> Supplier Name:"+suprelMaster.getSupplierName()
								+ "<br><br> Primary Category:"+suprelMaster.getPrimaryNonConformity()
								+ "<br><br> Secondary Category:"+suprelMaster.getSecondaryNonConformity()
								+ "<br><br> Incident Date:"+suprelMaster.getCreatedOn()
								+ "<br><br> Open this link to approve or reject the claim. "+url+"incident-claims/"+suprelMaster.getSuprelNo()+"/"+managerDetails.getManagerTid()
								+ "<br><br><b> Note: Do not reply to this system generated email notification</b>", host);
			}
		}
		return suprelMaster;
	}
	
	
	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public ResponseEntity<ResponseMessage> importApproverDetails(MultipartFile filePath) {
		try
		{
			@SuppressWarnings("resource")
			XSSFSheet excelSheet = new XSSFWorkbook(filePath.getInputStream()).getSheetAt(0);
			int rows = excelSheet.getPhysicalNumberOfRows();
			int column = excelSheet.getRow(0).getPhysicalNumberOfCells(); 
			if(column != 4 || rows < 2) {
				return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has empty or invalid data, Please try again with valid data"));
			}
			// If More than 100 records Bulk upload will not support
			if(rows >101) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Manager/Approver Records exceed maximum of count 100, please try again with 100 records at a time."));
			}
			List<SuprelManager> managerListFromExcel = new ArrayList<SuprelManager>();
			List<SuprelManager> managerListFromDB = managerRepo.findAll();
			List<SuprelManager> inactiveList = new ArrayList<SuprelManager>();
			for(int i = 1 ; i< rows;i++)
			{
				for(int j=0;j<column;j++) {
					if(excelSheet.getRow(i).getCell(j)==null || excelSheet.getRow(i).getCell(j).toString().trim().isEmpty()) {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has empty column at row : "+(i+1)+", Please correct and try again."));
					}
				}
				SuprelManager suprelManager = new SuprelManager();
				String managerTid= excelSheet.getRow(i).getCell(0).toString().trim().toUpperCase();
				String managerEmail= excelSheet.getRow(i).getCell(2).toString().trim();
				String managerName = excelSheet.getRow(i).getCell(1).toString().trim().toUpperCase();
				String flag = excelSheet.getRow(i).getCell(3).toString().trim().toUpperCase();
				if(flag.equalsIgnoreCase("Y")) {
				if(!CollectionUtils.isNullOrEmpty(managerListFromDB)) {
					for(SuprelManager list :managerListFromDB) {
						if(list.getManagerTid().equalsIgnoreCase(managerTid)) {
							return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has Tid already in system at row : "+(i+1)+", Please correct and try again."));
						}
						if(list.getManagerName().equalsIgnoreCase(managerName)) {
							return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has Manager Name already in system at row : "+(i+1)+", Please correct and try again."));
						}
						if(list.getManagerEmail().equalsIgnoreCase(managerEmail)) {
							return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has Manager Email Id already in system at row : "+(i+1)+", Please correct and try again."));
						}
					}
				}
				if(managerTid != null && (isAlphaNumeric1(managerTid) || managerTid.length() !=7)) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid Tid at row : "+(i+1)+", Please correct and try again."));
				}

				if(managerEmail != null && isEmailInValid(managerEmail)) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid Email at row : "+(i+1)+", Please correct and try again."));
				}
				suprelManager.setManagerEmail(managerEmail);
				suprelManager.setManagerName(managerName);
				suprelManager.setManagerTid(managerTid);
				
				Boolean tidFound = managerListFromExcel.stream().anyMatch(p -> p.getManagerTid().equalsIgnoreCase(managerTid));
				if(tidFound) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has duplicate TID at row : "+(i+1)+", Please correct and try again."));
				}
				Boolean nameFound = managerListFromExcel.stream().anyMatch(p -> p.getManagerName().equalsIgnoreCase(managerName));
				if(nameFound) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has duplicate Manager Name at row : "+(i+1)+", Please correct and try again."));
				}
				Boolean emailFound = managerListFromExcel.stream().anyMatch(p -> p.getManagerEmail().equalsIgnoreCase(managerEmail));
				if(emailFound) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has duplicate Manager Email Id at row : "+(i+1)+", Please correct and try again."));
				}
				
				managerListFromExcel.add(suprelManager);
				//managerRepo.save(suprelManager);
				}else {
					//To delete the inactive users in Database.
					suprelManager.setManagerEmail(managerEmail);
					suprelManager.setManagerName(managerName);
					suprelManager.setManagerTid(managerTid);
					Boolean tidFound = managerListFromDB.stream().anyMatch(p -> p.getManagerTid().equalsIgnoreCase(managerTid));
					if(tidFound) {
					inactiveList.add(suprelManager);
					}
				}
			}
			managerRepo.saveAll(managerListFromExcel);
			managerRepo.deleteAll(inactiveList);
		} catch (Exception e) {
			LOGGER.error("context", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Approver Details imported Successfully"));
	}
	
	public ResponseEntity<ResponseMessage> saveIncidentClaimsData(MultipartFile filePath) {
		try
		{
			@SuppressWarnings("resource")
			XSSFSheet excelSheet = new XSSFWorkbook(filePath.getInputStream()).getSheetAt(0);
			int rows = excelSheet.getPhysicalNumberOfRows();
			int column = excelSheet.getRow(0).getPhysicalNumberOfCells(); 
			if(column != 36 || rows < 2) {
				return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has missmatch colums or invalid data, Please try again with valid data"));
			}
			// If More than 50 records Bulk upload will not support
			if(rows >51) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Bulkupload Records exceed maximum of count 50, please try again with 50 records at a time."));
			}
			List<SuprelMaster> masterList = new ArrayList<SuprelMaster>();
			List<SuprelNumbers> suprelNumList = new ArrayList<SuprelNumbers>();
			List<String> technicalAreaList = technicalAreaDetailsRepository.getDistinctTechnicalAreaList();
			List<String> primaryNonConf = nonConformityDetailsRepository.getDistinctPrimaryNonConfList();
			List<String> countryList = suprelOriginatorRepository.getDistinctCountryList();
			List<SuprelNumbers> suprelNumDBList = suprelNumbersRepository.findAll();
			List<SuprelManager> managerListFromDB = managerRepo.findAll();
			List<String> managerList = new ArrayList<String>();
			if(!CollectionUtils.isNullOrEmpty(managerListFromDB)) {
				for(SuprelManager list :managerListFromDB) {
					managerList.add(list.getManagerName());
				}
			}
			String createdBySupNum = "Admin";
			Timestamp createdOnSupNum = getTodaysDateWithTimestamp();

			for(int i = 1 ; i< rows;i++)
			{
				if(excelSheet.getRow(i)== null) {
					break;	
				}

				for(int j=0;j<29;j++) {
					if(j==0 || j==7 || j==8 || j==10 || j==11 || j==13 || j==14 || j==19 || j==20 || j==22 || j==23 || j==24 || j==25 || j==26|| j==27 || j==28) {
						if(excelSheet.getRow(i).getCell(j)==null || excelSheet.getRow(i).getCell(j).toString().trim().isEmpty()) {
							return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has empty column at : "+(j+1)+" for row : "+(i+1)+", Please correct and try again."));
						}
					}
					if(j==9 || j==12) {
						if(excelSheet.getRow(i).getCell(12)!=null && !excelSheet.getRow(i).getCell(12).toString().trim().isEmpty()) {
							if(excelSheet.getRow(i).getCell(9)==null || excelSheet.getRow(i).getCell(9).toString().trim().isEmpty()) {
								return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has empty column for amount based on currency type at row : "+(i+1)+", Please correct and try again."));
							}
						}
						if(excelSheet.getRow(i).getCell(9)!=null && !excelSheet.getRow(i).getCell(9).toString().trim().isEmpty()) {
							if(excelSheet.getRow(i).getCell(12)==null || excelSheet.getRow(i).getCell(12).toString().trim().isEmpty()) {
								return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has empty column for currency type based on amount entered at row : "+(i+1)+", Please correct and try again."));
							}
						}
					}
				}

				SuprelMaster suprelMaster = new SuprelMaster();
				String suppCode = null;
				if(excelSheet.getRow(i).getCell(0)!=null && !excelSheet.getRow(i).getCell(0).toString().trim().isEmpty()) {
					Cell cell = excelSheet.getRow(i).getCell(0);
					CellType type = cell.getCellTypeEnum();
					switch (type) 
					{
					case NUMERIC:
						suppCode = excelSheet.getRow(i).getCell(0).getRawValue().trim();
						break;
					case STRING:
						suppCode = excelSheet.getRow(i).getCell(0).getStringCellValue().trim();
						break;
					}
				}
				//String suppCode= excelSheet.getRow(i).getCell(0).toString().trim().toUpperCase();
				if(suppCode != null && (!isAlphaNumeric(suppCode) || (suppCode.length()<5 || suppCode.length()>7)) ) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid SupplierCode at row : "+(i+1)+", Please correct and try again."));
				}
				suprelMaster.setSupplierCode(suppCode.toUpperCase());

				SuprelSISData sisData = getSuprelSISData(suppCode.toUpperCase());

				String suppName= excelSheet.getRow(i).getCell(1)!= null ? excelSheet.getRow(i).getCell(1).toString().trim().toUpperCase() : "";
				if(StringUtils.hasText(suppName)) {
					suprelMaster.setSupplierName(suppName);
				}else {
					suprelMaster.setSupplierName(sisData.getSupplierName());
				}

				String suppAdd = excelSheet.getRow(i).getCell(2)!=null? excelSheet.getRow(i).getCell(2).toString().trim().toUpperCase() : "";
				if(StringUtils.hasText(suppAdd)) {
					suprelMaster.setSupplierAddress(suppAdd);
				}else {
					suprelMaster.setSupplierAddress(sisData.getSupplierAddress());
				}

				String city = excelSheet.getRow(i).getCell(3)!= null ? excelSheet.getRow(i).getCell(3).toString().trim().toUpperCase() : "";
				if(StringUtils.hasText(city)) {
					suprelMaster.setCity(city);
				}else {
					suprelMaster.setCity(sisData.getCity());
				}
				
				String state = excelSheet.getRow(i).getCell(4)!=null ? excelSheet.getRow(i).getCell(4).toString().trim().toUpperCase() : "";
				List<String> stateList = suprelOriginatorRepository.getDistinctStateList();
				if(!StringUtils.isEmpty(state)) {
					if(stateList.contains(state)) {
						suprelMaster.setState(state);
					}else {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid State at row : "+(i+1)+", Please correct and try again."));
					}
				}else {
					suprelMaster.setState(sisData.getState());
				}

				String postalCode = excelSheet.getRow(i).getCell(5)!=null ? excelSheet.getRow(i).getCell(5).toString().trim():"";
				if(excelSheet.getRow(i).getCell(5)!=null && !excelSheet.getRow(i).getCell(5).toString().trim().isEmpty()) {
					Cell cell = excelSheet.getRow(i).getCell(5);
					CellType type = cell.getCellTypeEnum();
					switch (type) 
					{
					case NUMERIC:
						postalCode = excelSheet.getRow(i).getCell(5).getRawValue().trim();
						break;
					case STRING:
						postalCode = excelSheet.getRow(i).getCell(5).getStringCellValue().trim();
						break;
					}
				}
				//Add postal code validation
					if(postalCode != null && (!isAlphaNumericPostal(postalCode) || postalCode.length()>10)) {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Invalid postal code for supplierCode : "+suppCode+", Please correct and try again."));
					}
					if(StringUtils.hasText(postalCode)) {
						suprelMaster.setPostalCode(postalCode.toUpperCase());
					}else {
						suprelMaster.setPostalCode(sisData.getPostalcode());	
					}
				

				String country = excelSheet.getRow(i).getCell(6)!=null ? excelSheet.getRow(i).getCell(6).toString().trim().toUpperCase() : "";
				if(!StringUtils.isEmpty(country)) {
					if(countryList.contains(country)) {
						suprelMaster.setCountry(country);
					}else {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid Country at row : "+(i+1)+", Please correct and try again."));
					}
				}else {
					suprelMaster.setCountry(sisData.getCountry());
				}

				

				String suprelNum = excelSheet.getRow(i).getCell(7).toString().trim().toUpperCase();
				if(suprelNum.length()!=10) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Suprel Number in BulkUpload file should be 10 characters at row : "+(i+1)+", Please correct and try again."));
				}
				Boolean suprelNumFound = masterList.stream().anyMatch(p -> p.getSuprelNo().equalsIgnoreCase(suprelNum));
				if(suprelNumFound) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has duplicate Suprel Number at row : "+(i+1)+", Please correct and try again."));
				}
				Boolean suprelNumDBCheck = suprelNumDBList.stream().anyMatch(p -> p.getSuprelNo().equalsIgnoreCase(suprelNum));
				if(suprelNumDBCheck) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has Suprel Number already in system at row : "+(i+1)+", Please correct and try again."));
				}
				SuprelNumbers sn = new SuprelNumbers();
				sn.setSuprelNo(suprelNum);
				sn.setCreatedBy(createdBySupNum);
				sn.setCreatedOn(createdOnSupNum);
				suprelMaster.setSuprelNo(suprelNum);

				String suprelStatus = excelSheet.getRow(i).getCell(8).toString().trim();
				if(!checkSuprelStatus(suprelStatus)) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid Suprel Status at row : "+(i+1)+", Please correct and try again."));
				}
				suprelMaster.setSuprelStatus(suprelStatus);

				String amount = null;
				if(excelSheet.getRow(i).getCell(9)!=null && !excelSheet.getRow(i).getCell(9).toString().trim().isEmpty()) {
					Cell cell = excelSheet.getRow(i).getCell(9);
					CellType type = cell.getCellTypeEnum();
					if(type == CellType.NUMERIC) {
						amount = excelSheet.getRow(i).getCell(9).getRawValue().trim();
					}else {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Amount must be in Numbers in Bulk Upload File at row : "+(i+1)+", Please correct and try again."));
					}
					suprelMaster.setAmount(amount);
				}

				String technicalArea = excelSheet.getRow(i).getCell(10).toString().trim();
				if(technicalAreaList.contains(technicalArea)) {
					suprelMaster.setTechnicalArea(technicalArea);
				}else {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid Technical Area at row : "+(i+1)+", Please correct and try again."));

				}

				String managerName = excelSheet.getRow(i).getCell(11).toString().trim().toUpperCase();
				if(managerList.contains(managerName)) {
					suprelMaster.setManagerName(managerName);
				}else {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has Manager Name not available in system at row : "+(i+1)+", Please add manager details and upload bulk file."));
				}


				if(excelSheet.getRow(i).getCell(12)!=null && !excelSheet.getRow(i).getCell(12).toString().trim().isEmpty()) {
					String currencyType = excelSheet.getRow(i).getCell(12).toString().trim().toUpperCase();
					if(!checkCurrencyType(currencyType)) {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid CurrencyType at row : "+(i+1)+", Please correct and try again."));
					}
					suprelMaster.setCurrencyType(currencyType);
				}

				String details = excelSheet.getRow(i).getCell(13).toString().trim();
				if(details.length()<50 || details.length()>2000) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Details Column should have less than 2000 Characters and more than 50 characters in Bulk Upload File at row : "+(i+1)+", Please correct and try again."));
				}
				suprelMaster.setDetails(details);

				String email1 = excelSheet.getRow(i).getCell(14).toString().trim();
				if(isEmailInValid(email1)) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid Supplier Email_1 at row : "+(i+1)+", Please correct and try again."));
				}
				suprelMaster.setEmail1(email1);

				if(excelSheet.getRow(i).getCell(15)!=null && !excelSheet.getRow(i).getCell(15).toString().trim().isEmpty()) {
					String email2 = excelSheet.getRow(i).getCell(15).toString().trim();
					if(isEmailInValid(email2)) {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid Supplier Email_2 at row : "+(i+1)+", Please correct and try again."));
					}
					suprelMaster.setEmail2(email2);
				}

				if(excelSheet.getRow(i).getCell(16)!=null && !excelSheet.getRow(i).getCell(16).toString().trim().isEmpty()) {
					String email3 = excelSheet.getRow(i).getCell(16).toString().trim();
					if(isEmailInValid(email3)) {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid Supplier Email_3 at row : "+(i+1)+", Please correct and try again."));
					}
					suprelMaster.setEmail3(email3);
				}

				if(excelSheet.getRow(i).getCell(17)!=null && !excelSheet.getRow(i).getCell(17).toString().trim().isEmpty()) {
					String email4 = excelSheet.getRow(i).getCell(17).toString().trim();
					if(isEmailInValid(email4)) {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid Supplier Email_4 at row : "+(i+1)+", Please correct and try again."));
					}
					suprelMaster.setEmail4(email4);
				}

				if(excelSheet.getRow(i).getCell(18)!=null && !excelSheet.getRow(i).getCell(18).toString().trim().isEmpty()) {
					String email5 = excelSheet.getRow(i).getCell(18).toString().trim();
					if(isEmailInValid(email5)) {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid Supplier Email_5 at row : "+(i+1)+", Please correct and try again."));
					}
					suprelMaster.setEmail5(email5);
				}

				String primaryNonConformity = excelSheet.getRow(i).getCell(19).toString().trim();
				Boolean isValid = false;
				for(String primary:primaryNonConf) {
					if(primary.equals(primaryNonConformity)) {
						suprelMaster.setPrimaryNonConformity(primaryNonConformity);
						isValid = true;
						break;
					}
				}
				if("Cost Recovery".equals(primaryNonConformity)) {
					if(excelSheet.getRow(i).getCell(9)==null || excelSheet.getRow(i).getCell(9).toString().trim().isEmpty()) {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has empty amount for Cost Recovery at row : "+(i+1)+", Please correct and try again."));
					}
				}
				
				if(!isValid) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid Primary NonConformity at row : "+(i+1)+", Please correct and try again."));
				}

				List<String> secondaryNonConfList = nonConformityDetailsRepository.getDistinctSecondaryNonConfList(primaryNonConformity);
				String secondaryNonConformity = excelSheet.getRow(i).getCell(20).toString().trim();
				Boolean isValid1 = false;
				for(String secondary:secondaryNonConfList) {
					if(secondary.equals(secondaryNonConformity)) {
						suprelMaster.setSecondaryNonConformity(secondaryNonConformity);
						isValid1 = true;
						break;
					}
				}
				if(!isValid1) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid Secondary NonConformity at row : "+(i+1)+", Please correct and try again."));
				}
				if(excelSheet.getRow(i).getCell(21)!=null && !excelSheet.getRow(i).getCell(21).toString().trim().isEmpty()) {
					String supplierComments = excelSheet.getRow(i).getCell(21).toString().trim();
					if(supplierComments.length()<5 || supplierComments.length()>2000) {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Supplier Comments Column should have less than 2000 Characters and more than 5 characters in Bulk Upload File at row : "+(i+1)+", Please correct and try again."));
					}
					suprelMaster.setSupplierComments(supplierComments);
				}

				Cell cell = excelSheet.getRow(i).getCell(22);
				CellType type = cell.getCellTypeEnum();
				Date issuedDate1 = null;
				if(type == CellType.NUMERIC) {
					if (isDateValid(cell)) {
						issuedDate1 = cell.getDateCellValue();
					} else {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Issued Date column should be in yyyy-mm-dd Format in Bulk Upload File at row : "+(i+1)+", Please correct and try again."));	
					}
				}else {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Issued Date column should be in yyyy-mm-dd Format in Bulk Upload File at row : "+(i+1)+", Please correct and try again."));	
				}
				suprelMaster.setIssuedDate(issuedDate1);

				if(excelSheet.getRow(i).getCell(23)!=null && !excelSheet.getRow(i).getCell(23).toString().trim().isEmpty()) {
					String createdBy = excelSheet.getRow(i).getCell(23).toString().trim().toUpperCase();
					suprelMaster.setCreatedBy(createdBy);
				}

				Cell cell1 = excelSheet.getRow(i).getCell(24);
				CellType type1 = cell.getCellTypeEnum();
				Date createdOn = null;
				if(type1 == CellType.NUMERIC) {
					if (isDateValid(cell1)) {
						createdOn = cell1.getDateCellValue();
					} else {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Created On column should be in yyyy-mm-dd Format in Bulk Upload File at row : "+(i+1)+", Please correct and try again."));	
					}
				}else {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Created On column should be in yyyy-mm-dd Format in Bulk Upload File at row : "+(i+1)+", Please correct and try again."));	
				}

				Timestamp createdOn1 = new Timestamp(createdOn.getTime());
				suprelMaster.setCreatedOn(createdOn1);

				if(excelSheet.getRow(i).getCell(25)!=null && !excelSheet.getRow(i).getCell(25).toString().trim().isEmpty()) {
					String modifiedBy = excelSheet.getRow(i).getCell(25).toString().trim();
					suprelMaster.setModifiedBy(modifiedBy.toUpperCase());
				}

				Cell cell2 = excelSheet.getRow(i).getCell(26);
				CellType type2 = cell.getCellTypeEnum();
				Date modifiedOn = null;
				if(type2 == CellType.NUMERIC) {
					if (isDateValid(cell2)) {
						modifiedOn = cell2.getDateCellValue();
					} else {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Modified On column should be in yyyy-mm-dd Format in Bulk Upload File at row : "+(i+1)+", Please correct and try again."));	
					}
				}else {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Modified On column should be in yyyy-mm-dd Format in Bulk Upload File at row : "+(i+1)+", Please correct and try again."));	
				}

				Timestamp modifiedOn1 = new Timestamp(modifiedOn.getTime());
				suprelMaster.setModifiedOn(modifiedOn1);

				String originatorEmail = excelSheet.getRow(i).getCell(27).toString().trim();
				if(isEmailInValid(originatorEmail)) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid Originator Email at row : "+(i+1)+", Please correct and try again."));
				}
				suprelMaster.setOriginatorEmail(originatorEmail);

				String originatorName = excelSheet.getRow(i).getCell(28).toString().trim();
				suprelMaster.setOriginatorName(originatorName);

				if(excelSheet.getRow(i).getCell(29)!=null && !excelSheet.getRow(i).getCell(29).toString().trim().isEmpty()) {
					String managerComments = excelSheet.getRow(i).getCell(29).toString().trim();
					if(managerComments.length()<5 || managerComments.length()>2000) {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Manager Comments Column should have less than 2000 Characters and more than 5 characters in Bulk Upload File at row : "+(i+1)+", Please correct and try again."));
					}
					suprelMaster.setManagerComments(managerComments);
				}

				if(excelSheet.getRow(i).getCell(30)!=null && !excelSheet.getRow(i).getCell(30).toString().trim().isEmpty()) {
					String attachmentName = excelSheet.getRow(i).getCell(30).toString().trim();
					suprelMaster.setAttachmentNames(attachmentName);
				}

				if(excelSheet.getRow(i).getCell(31)!=null && !excelSheet.getRow(i).getCell(31).toString().trim().isEmpty()) {
					String userEmail1 = excelSheet.getRow(i).getCell(31).toString().trim();
					if(isEmailInValid(userEmail1)) {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid User Email1 at row : "+(i+1)+", Please correct and try again."));
					}
					suprelMaster.setUserEmail1(userEmail1);
				}

				if(excelSheet.getRow(i).getCell(32)!=null && !excelSheet.getRow(i).getCell(32).toString().trim().isEmpty()) {
					String userEmail2 = excelSheet.getRow(i).getCell(32).toString().trim();
					if(isEmailInValid(userEmail2)) {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid User Email2 at row : "+(i+1)+", Please correct and try again."));
					}
					suprelMaster.setUserEmail2(userEmail2);
				}

				if(excelSheet.getRow(i).getCell(33)!=null && !excelSheet.getRow(i).getCell(33).toString().trim().isEmpty()) {
					String userEmail3 = excelSheet.getRow(i).getCell(33).toString().trim();
					if(isEmailInValid(userEmail3)) {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid User Email3 at row : "+(i+1)+", Please correct and try again."));
					}
					suprelMaster.setUserEmail3(userEmail3);
				}

				if(excelSheet.getRow(i).getCell(34)!=null && !excelSheet.getRow(i).getCell(34).toString().trim().isEmpty()) {
					String userEmail4 = excelSheet.getRow(i).getCell(34).toString().trim();
					if(isEmailInValid(userEmail4)) {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid User Email4 at row : "+(i+1)+", Please correct and try again."));
					}
					suprelMaster.setUserEmail4(userEmail4);
				}

				if(excelSheet.getRow(i).getCell(35)!=null && !excelSheet.getRow(i).getCell(35).toString().trim().isEmpty()) {
					String userEmail5 = excelSheet.getRow(i).getCell(35).toString().trim();
					if(isEmailInValid(userEmail5)) {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("BulkUpload file has invalid User Email5 at row : "+(i+1)+", Please correct and try again."));
					}
					suprelMaster.setUserEmail5(userEmail5);
				}
				masterList.add(suprelMaster);
				suprelNumList.add(sn);
			}
			suprelNumbersRepository.saveAll(suprelNumList);
			suprelOriginatorRepository.saveAll(masterList);
		}catch (Exception e) {
			LOGGER.error("context", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Internal Server Error"+e.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Incident Claims Data imported Successfully"));
	}
	
	private boolean isDateValid(Cell cell) {
		Boolean valid = true;
		try {
		valid = DateUtil.isCellDateFormatted(cell);
		}catch (Exception e) {
			LOGGER.error("context", e);
			valid = false;
		}
		return valid;
	}
	public static boolean isAlphaNumeric1(String s) {
		return !pattern1.matcher(s).find();
	}
	public static boolean isAlphaNumericPostal(String s) {
		return pattern.matcher(s).find();
	}
	
	public static boolean isAlphaNumeric(String s) {
		return pattern3.matcher(s).find();
	}
	
	public static boolean isEmailInValid(String s) {
		return !pattern2.matcher(s).find();
	}
	
	public Timestamp getTodaysDateWithTimestamp() throws ParseException {
		Date today = Calendar.getInstance().getTime();
		String pat = "yyyy-MM-dd";
		DateFormat df = new SimpleDateFormat(pat);
		String todayAsString = df.format(today);
		todayAsString = todayAsString+" 00:00:00";
		Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(todayAsString);  
		return new Timestamp(date1.getTime());
	}
	
	public Boolean checkCurrencyType(String currType) {
		ArrayList<String> currList = new ArrayList<String>();
		currList.add("USA");
		currList.add("CAN");
		currList.add("MEX");
		currList.add("EURO");
	
		return currList.contains(currType);
	}
	
	public Boolean checkSuprelStatus(String supStatus){
		ArrayList<String> suprelStatusList = new ArrayList<String>();
		suprelStatusList.add(DRAFT);
		suprelStatusList.add(PENDING);
		suprelStatusList.add(ISSUED);
		suprelStatusList.add(DISPUTED);
		suprelStatusList.add(CANCELLED);
		suprelStatusList.add(CLOSED);
		suprelStatusList.add(FINALEBSC);
		
		return suprelStatusList.contains(supStatus);

		}
	public List<String> getYearList(){
		return suprelOriginatorRepository.getYearList();
	}

	public void updateMailStatus(String suprelNo, String mailStatus) {
		 suprelOriginatorRepository.updateMailStatus(suprelNo, mailStatus);
	}

	public List<SuprelMaster> getSuprelDetailsByManagerName(String managerName, String mailStatus) {
		return suprelOriginatorRepository.getSuprelMasterDetails(managerName, mailStatus);
		
	}
	
}
