package com.stellantis.SUPREL.config;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collections;
import java.util.function.Function;

import org.apache.commons.codec.binary.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * Utility class to decode ALB Jwt token.
 * 
 * @T0151MP
 *
 */
@Component
public class JwtTokenUtil {

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

	private String publicKey;
	private ECPublicKey decodPK;
	private URL jwtPublicKeyUrl;
	private String uri = "https://public-keys.auth.elb.us-east-1.amazonaws.com/";
	
	private RestOperations restOperations = new RestTemplate();
	ALBTokenPayload payload = new ALBTokenPayload();
	
	/**
	 * Method to Decode JWT Processor
	 * @throws MalformedURLException
	 */
	public Claims decode(String jwt)
			throws UnsupportedEncodingException, JsonMappingException, JsonProcessingException, MalformedURLException {
		String headar = jwt.split("\\.")[0];
		String payload = jwt.split("\\.")[1];
		String signa = jwt.split("\\.")[2];

		String jwtHeadar = new String(Base64.decodeBase64(headar), "UTF-8");
		logger.info("Headar =====>> " + jwtHeadar);

		String jwtPayload = new String(Base64.decodeBase64(payload), "UTF-8");
		logger.info("Payload =====>> " + jwtPayload);

		String jwtSigna = new String(Base64.decodeBase64(signa), "UTF-8");
		logger.info("Signa =====>> " + jwtSigna);

		ObjectMapper json = new ObjectMapper();
		JsonNode jsonNode = json.readTree(jwtHeadar);
		String kid = jsonNode.get("kid").textValue();
		logger.info("kid =====>> " + kid);

		if (kid != null) {
			jwtPublicKeyUrl = new URL(uri + kid);
			logger.info("Append Kid Into URL =====>> " + jwtPublicKeyUrl);

			try {

				logger.info("Call RetrieveResource Methode to Get Public Key");
				publicKey = retrieveResource(jwtPublicKeyUrl);
				logger.info("Get Public Key From RetrieveResource Method" + publicKey);

				if (publicKey != null) {
					logger.info("Call Parsed Public Key Method" + publicKey);
					decodPK = getParsedPublicKey(publicKey);
					logger.info("Get Parsed Public Key From GetParsedPublicKey" + decodPK);
				}
			} catch (IOException e) {
				e.printStackTrace();
				e.getMessage().toString();
			}
		} else {
			logger.error(" KID IS NUll ");
		}

		logger.info("Call ExtractUserName Method to Get Claims Details");
		Claims claims = extractAllClaim(jwt);
		logger.info("Get Claims From ExtractUserName Method" + claims);
		return claims;

	}
	
	/**
	 * Method to call the AWS Public Key provider API.
	 *
	 */
	public String retrieveResource(URL url) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		ResponseEntity<String> response = null;
		try {
			RequestEntity<Void> request = new RequestEntity<>(headers, HttpMethod.GET, url.toURI());
			try {
				response = restOperations.exchange(request, String.class);
			} catch (Exception e) {

				e.printStackTrace();
			}
		} catch (Exception ex) {
			throw new IOException(ex);
		}
		logger.info("response from request =====>> " + response.toString());
		if (response.getStatusCodeValue() != 200) {
			throw new IOException(response.toString());
		}
		logger.info("Response From Rest Template =====>> " + response.getBody().toString());

		return response.getBody();

	}

	/**
	 * Method to Get Parsed Public Key From the AWS Public Key provider API.
	 *
	 */
	public ECPublicKey getParsedPublicKey(String publicKey) {

		String pubKey = publicKey.replace("-----BEGIN PUBLIC KEY-----\n", "").replace("\n-----END PUBLIC KEY-----", "")
				.trim();
		logger.info("pubKey new =====>> " + pubKey);

		String PUB_KEY = pubKey;

		// removes white spaces or char 20

		String PUBLIC_KEY = "";

		if (!PUB_KEY.isEmpty()) {
			PUBLIC_KEY = PUB_KEY.replace(" ", "");
			logger.info("After Removeing White Space From PUBLIC_KEY =====>>" + PUBLIC_KEY);
		}

		try {
			byte[] decode = Base64.decodeBase64(PUBLIC_KEY);
			logger.info("Decode The PublicKey =====>>" + decode);

			X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(decode);
			logger.info("Get keySpecX509 =====>>" + keySpecX509);

			KeyFactory keyFactory = KeyFactory.getInstance("EC");
			logger.info(" Geting keyFactory =====>>" + keyFactory);

			ECPublicKey key = (ECPublicKey) keyFactory.generatePublic(keySpecX509);
			logger.info("Geting Key =====>>" + key);

			return key;

		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			logger.error("Exception block | Public key parsing error ");
			return null;
		}
	}

	/**
	 * Method For Extract User Name From Token
	 *
	 */
	public <T> ALBTokenPayload extractClaim(String token, Function<Claims, T> claimsResolver) {
		Claims claims = extractAllClaim(token);
		logger.info(" Claims =====>>" + claims);
		return (ALBTokenPayload) claimsResolver.apply(claims);
	}

	/**
	 * Method For Extract All Claim From Token
	 *
	 */
	public Claims extractAllClaim(String token) {
		Claims claims = Jwts.parser().setSigningKey(decodPK).parseClaimsJws(token).getBody();
		logger.info(" Claims =====>>" + claims.toString());
		
		logger.info("GetSubject =====>>" + claims.getSubject());
		logger.info("GetExpiration =====>>" + claims.getExpiration());
		
		payload.setId((String) claims.get("sub"));
		payload.setUser_type((String) claims.get("user_type"));
		payload.setName((String) claims.get("given_name"));
		payload.setLastName((String) claims.get("family_name"));
		payload.setEmail((String) claims.get("email"));
		
		logger.info("Sub =====>>" + claims.get("sub"));
		logger.info("User Type =====>>" + claims.get("user_type"));
		logger.info("Given_name =====>>" + claims.get("given_name"));
		logger.info("Last Name =====>>" + claims.get("family_name"));
		logger.info("Email =====>>" + claims.get("email"));
		return claims;

	}

	}
