package com.captcha.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.captcha.validator.PasswordMatches;

import lombok.Data;

/**
 * @author Chinna
 * @since 26/3/18
 */

@PasswordMatches
public class SignUpRequest {

	private Long userID;

	private String providerUserId;

	@NotEmpty
	private String displayName;

	@NotEmpty
	private String email;

	private SocialProvider socialProvider;

	@Size(min = 6, message = "{Size.userDto.password}")
	private String password;

	@NotEmpty
	private String matchingPassword;

	private boolean using2FA;
	
	private int sValue;

	private int refundAmount;
	
	public SignUpRequest(String providerUserId, String displayName, String email, String password, String matchingPassword, SocialProvider socialProvider, int sValue, int refundAmount) {
		this.providerUserId = providerUserId;
		this.displayName = displayName;
		this.email = email;
		this.password = password;
		this.matchingPassword = matchingPassword;
		this.socialProvider = socialProvider;
		this.sValue = sValue;
		this.refundAmount = refundAmount;
	}

	public static Builder getBuilder() {
		return new Builder();
	}

	public static class Builder {
		private String providerUserID;
		private String displayName;
		private String email;
		private String password;
		private String matchingPassword;
		private SocialProvider socialProvider;
		private int sValue;
		private int refundAmount;

		public Builder addProviderUserID(final String userID) {
			this.providerUserID = userID;
			return this;
		}

		public Builder addDisplayName(final String displayName) {
			this.displayName = displayName;
			return this;
		}

		public Builder addEmail(final String email) {
			this.email = email;
			return this;
		}

		public Builder addsValue(final int sValue) {
			this.sValue = sValue;
			return this;
		}
		public Builder addrefundAmount(final int refundAmount) {
			this.refundAmount = refundAmount;
			return this;
		}
		public Builder addPassword(final String password) {
			this.password = password;
			return this;
		}

		public Builder addMatchingPassword(final String matchingPassword) {
			this.matchingPassword = matchingPassword;
			return this;
		}

		public Builder addSocialProvider(final SocialProvider socialProvider) {
			this.socialProvider = socialProvider;
			return this;
		}

		public SignUpRequest build() {
			return new SignUpRequest(providerUserID, displayName, email, password, matchingPassword, socialProvider,sValue,refundAmount);
		}
	}

	public Long getUserID() {
		return userID;
	}

	public void setUserID(Long userID) {
		this.userID = userID;
	}

	public String getProviderUserId() {
		return providerUserId;
	}

	public void setProviderUserId(String providerUserId) {
		this.providerUserId = providerUserId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getsValue() {
		return sValue;
	}

	public void setsValue(int sValue) {
		this.sValue = sValue;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public SocialProvider getSocialProvider() {
		return socialProvider;
	}

	public void setSocialProvider(SocialProvider socialProvider) {
		this.socialProvider = socialProvider;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMatchingPassword() {
		return matchingPassword;
	}

	public void setMatchingPassword(String matchingPassword) {
		this.matchingPassword = matchingPassword;
	}

	public int getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(int refundAmount) {
		this.refundAmount = refundAmount;
	}

	public boolean isUsing2FA() {
		return using2FA;
	}

	public void setUsing2FA(boolean using2fa) {
		using2FA = using2fa;
	}
	
	
}
