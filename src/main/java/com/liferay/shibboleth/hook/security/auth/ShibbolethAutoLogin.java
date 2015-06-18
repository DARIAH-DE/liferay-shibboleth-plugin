/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * Copyright (c) 2014 Georg-August-Universität Göttingen
 *
 * Liferay Shibboleth hook based on Liferay plugins by Liferay, Inc.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.shibboleth.hook.security.auth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.BaseAutoLogin;
import com.liferay.portal.security.ldap.PortalLDAPImporterUtil;
import com.liferay.portal.service.ClassNameLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserGroupLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;
import com.liferay.shibboleth.util.PropsKeys;
import com.liferay.shibboleth.util.PropsValues;
import com.liferay.shibboleth.util.ShibbolethUtil;

/**
 * @author Eric Chin
 * @author Carsten Thiel
 *
 */

public class ShibbolethAutoLogin extends BaseAutoLogin {

        private static Log _log = LogFactoryUtil.getLog(ShibbolethAutoLogin.class);

	// addUser taken from SSO plugin
	protected User addUser(
			long companyId, String firstName, String lastName,
			String emailAddress, String screenName, Locale locale)
		throws Exception {

		long creatorUserId = 0;
		boolean autoPassword = true;
		String password1 = null;
		String password2 = password1;
		boolean autoScreenName = false;
		long facebookId = 0;
		String openId = StringPool.BLANK;
		String middleName = StringPool.BLANK;
		int prefixId = 0;
		int suffixId = 0;
		boolean male = true;
		int birthdayMonth = Calendar.JANUARY;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = StringPool.BLANK;
		long[] groupIds = null;
		long[] organizationIds = null;
		long[] roleIds = null;
		long[] userGroupIds = null;
		boolean sendEmail = false;
		ServiceContext serviceContext = null;

		User user = UserLocalServiceUtil.addUser(
			creatorUserId, companyId, autoPassword, password1, password2,
			autoScreenName, screenName, emailAddress, facebookId, openId,
			locale, firstName, middleName, lastName, prefixId, suffixId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
			organizationIds, roleIds, userGroupIds, sendEmail, serviceContext);
		
		return user;
		
	}

	@Override
	protected String[] doLogin(
			HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		String shibbolethUserNameHeader = null;
		String screenName = null;
		String shibbolethUserEMailHeader = null;
		String shibbolethUserFirstNameHeader = null;
		String shibbolethUserLastNameHeader = null;
		String shibbolethGroupsHeader = null;


		Company company = PortalUtil.getCompany(request);
		
		long companyId = company.getCompanyId();

		if (!ShibbolethUtil.isEnabled(companyId)) {
			return null;
		}

		// Gather Shibboleth user data
		if (PrefsPropsUtil.getBoolean(
			companyId, PropsKeys.SHIBBOLETH_HEADERS_ENABLED,
			PropsValues.SHIBBOLETH_HEADERS_ENABLED)) {
			
			// use headers
			shibbolethUserNameHeader = (String) request.getHeader(
				PrefsPropsUtil.getString(
					companyId, PropsKeys.SHIBBOLETH_USERNAME_HEADER,
					PropsValues.SHIBBOLETH_USERNAME_HEADER));
			screenName = shibbolethUserNameHeader.replaceAll("@", "");

			shibbolethUserEMailHeader = (String) request.getHeader(
				PrefsPropsUtil.getString(
					companyId, PropsKeys.SHIBBOLETH_EMAIL_HEADER,
					PropsValues.SHIBBOLETH_EMAIL_HEADER));

			shibbolethUserFirstNameHeader = (String) request.getHeader(
				PrefsPropsUtil.getString(
					companyId, PropsKeys.SHIBBOLETH_FIRSTNAME_HEADER,
					PropsValues.SHIBBOLETH_FIRSTNAME_HEADER));

			shibbolethUserLastNameHeader = (String) request.getHeader(
				PrefsPropsUtil.getString(
					companyId, PropsKeys.SHIBBOLETH_LASTNAME_HEADER,
					PropsValues.SHIBBOLETH_LASTNAME_HEADER));
			
		}
		else {
			// use environment
			shibbolethUserNameHeader = (String) request.getAttribute(
				PrefsPropsUtil.getString(
					companyId, PropsKeys.SHIBBOLETH_USERNAME_HEADER,
					PropsValues.SHIBBOLETH_USERNAME_HEADER));
			screenName = shibbolethUserNameHeader.replaceAll("@", "");
	
			shibbolethUserEMailHeader = (String) request.getAttribute(
				PrefsPropsUtil.getString(
					companyId, PropsKeys.SHIBBOLETH_EMAIL_HEADER,
					PropsValues.SHIBBOLETH_EMAIL_HEADER));

			shibbolethUserFirstNameHeader = (String) request.getAttribute(
				PrefsPropsUtil.getString(
					companyId, PropsKeys.SHIBBOLETH_FIRSTNAME_HEADER,
					PropsValues.SHIBBOLETH_FIRSTNAME_HEADER));

			shibbolethUserLastNameHeader = (String) request.getAttribute(
				PrefsPropsUtil.getString(
					companyId, PropsKeys.SHIBBOLETH_LASTNAME_HEADER,
					PropsValues.SHIBBOLETH_LASTNAME_HEADER));

		}

		if ((Validator.isNull(shibbolethUserNameHeader)) || (Validator.isNull(shibbolethUserEMailHeader))) {
			return null;
		}

		String authType = company.getAuthType();

		User user = null;

		if (PrefsPropsUtil.getBoolean(
				companyId, PropsKeys.SHIBBOLETH_IMPORT_FROM_LDAP,
				PropsValues.SHIBBOLETH_IMPORT_FROM_LDAP)) {

			try {
				if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
					user = PortalLDAPImporterUtil.importLDAPUser(
						companyId, shibbolethUserEMailHeader, StringPool.BLANK);
				}
				else {
					user = PortalLDAPImporterUtil.importLDAPUser(
						companyId, StringPool.BLANK, shibbolethUserNameHeader);
				}
			}
			catch (SystemException se) {
			}
		}

		// log in the user
		if (user == null) {
			if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
				user = UserLocalServiceUtil.fetchUserByEmailAddress(
					companyId, shibbolethUserEMailHeader);
			}
			else {
				user = UserLocalServiceUtil.fetchUserByScreenName(
					companyId, shibbolethUserNameHeader);
			}
		}


		// create a liferay user if none exists
		// taken from SSO plugin
		if (user == null) {
		  	if (PrefsPropsUtil.getBoolean(
				companyId, PropsKeys.SHIBBOLETH_USER_AUTO_CREATE,
				PropsValues.SHIBBOLETH_USER_AUTO_CREATE)) {


				ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
				WebKeys.THEME_DISPLAY);

				Locale locale = LocaleUtil.getDefault();

				if (themeDisplay != null) {
		
					// ThemeDisplay should never be null, but some users complain of
					// this error. Cause is unknown.
					locale = themeDisplay.getLocale();
				}
		
				if (_log.isDebugEnabled()) {
					_log.debug("Adding user " + screenName);
				}
		
				user = addUser(
					companyId, shibbolethUserFirstNameHeader, shibbolethUserLastNameHeader,  shibbolethUserEMailHeader, screenName,
					locale);
			}
		}

			ExpandoValue expandoObject = ExpandoValueLocalServiceUtil.getValue(ClassNameLocalServiceUtil.getClassNameId(User.class), "CUSTOM_FIELDS", "eppn", user.getUserId());
		if(expandoObject != null){
			System.out.println("EPPN: " + expandoObject.getData());
		}
		
		//add eppn to user 
		if (!user.getExpandoBridge().hasAttribute("eppn")) {
			user.getExpandoBridge().addAttribute("eppn");
		};
		long classNameId = ClassNameLocalServiceUtil.getClassNameId(User.class);
		ExpandoValueLocalServiceUtil.addValue(ClassNameLocalServiceUtil.getClassName(classNameId).getValue(), "CUSTOM_FIELDS", "eppn", user.getUserId(), shibbolethUserNameHeader); 

		// Gather Shibboleth group data
		if (PrefsPropsUtil.getBoolean(
			companyId, PropsKeys.SHIBBOLETH_HEADERS_ENABLED,
			PropsValues.SHIBBOLETH_HEADERS_ENABLED)) {

			// use headers
			shibbolethGroupsHeader = (String) request.getHeader(
				PrefsPropsUtil.getString(
					companyId, PropsKeys.SHIBBOLETH_GROUPS_HEADER,
					PropsValues.SHIBBOLETH_GROUPS_HEADER));
		}
		else {
			// use envirenment
			shibbolethGroupsHeader = (String) request.getAttribute(
				PrefsPropsUtil.getString(
					companyId, PropsKeys.SHIBBOLETH_GROUPS_HEADER,
					PropsValues.SHIBBOLETH_GROUPS_HEADER));

		}

		// Get used Liferay groups
		String shibbolethGroupsHeaderSplit = PrefsPropsUtil.getString(
				companyId, PropsKeys.SHIBBOLETH_GROUPS_HEADER_SPLIT,
				PropsValues.SHIBBOLETH_GROUPS_HEADER_SPLIT);

		// map Shibboleth groups to Liferay groups  
		if ((null != shibbolethGroupsHeaderSplit) && (null != shibbolethGroupsHeader) && (shibbolethGroupsHeader.length()) > 0) {
		  	if (PrefsPropsUtil.getBoolean(companyId, PropsKeys.SHIBBOLETH_GROUPS_ENABLEMAPPING,PropsValues.SHIBBOLETH_GROUPS_ENABLEMAPPING)) {

			  	// remove all groups
			  	UserGroupLocalServiceUtil.setUserUserGroups(user.getUserId(), new long[0]);

				// try to map each Shibboleth group to a Liferay group
		 		String shibbolethGroups[] = shibbolethGroupsHeader.split(shibbolethGroupsHeaderSplit);
				List<Long> userGroups = new ArrayList<Long>();
				for (String element: shibbolethGroups)
				{
					try {
						userGroups.add(UserGroupLocalServiceUtil.getUserGroup(companyId, element).getUserGroupId());
					} catch(Exception e) {
					  	// ignore Shibboleth group if no matching Liferay group exists
					}
				}
				// convert List<Long> to long[]
				long[] userGroupIds = new long[userGroups.size()];
				for (int i = 0; i < userGroups.size(); i++){
			        	userGroupIds[i] = userGroups.get(i);
				}
				// set usergroups
				UserGroupLocalServiceUtil.setUserUserGroups(user.getUserId(), userGroupIds);

			}
		}
		
		String[] credentials = new String[3];

		credentials[0] = String.valueOf(user.getUserId());
		credentials[1] = user.getPassword();
		credentials[2] = Boolean.TRUE.toString();

		return credentials;
	}



}
