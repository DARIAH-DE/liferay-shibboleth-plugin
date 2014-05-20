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

import java.util.Collections;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.liferay.portal.kernel.exception.PortalException;
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
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.security.auth.BaseAutoLogin;
import com.liferay.portal.security.ldap.PortalLDAPImporterUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserGroupLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.shibboleth.util.PropsKeys;
import com.liferay.shibboleth.util.PropsValues;
import com.liferay.shibboleth.util.ShibbolethUtil;
import com.liferay.util.PwdGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

		return UserLocalServiceUtil.addUser(
			creatorUserId, companyId, autoPassword, password1, password2,
			autoScreenName, screenName, emailAddress, facebookId, openId,
			locale, firstName, middleName, lastName, prefixId, suffixId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
			organizationIds, roleIds, userGroupIds, sendEmail, serviceContext);
	}

	@Override
	protected String[] doLogin(
			HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		Company company = PortalUtil.getCompany(request);
		
		long companyId = company.getCompanyId();

		if (!ShibbolethUtil.isEnabled(companyId)) {
			return null;
		}

		String shibbolethUserHeader = request.getHeader(
			PrefsPropsUtil.getString(
				companyId, PropsKeys.SHIBBOLETH_USER_HEADER,
				PropsValues.SHIBBOLETH_USER_HEADER));

		if (Validator.isNull(shibbolethUserHeader)) {
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
						companyId, shibbolethUserHeader, StringPool.BLANK);
				}
				else {
					user = PortalLDAPImporterUtil.importLDAPUser(
						companyId, StringPool.BLANK, shibbolethUserHeader);
				}
			}
			catch (SystemException se) {
			}
		}

		// log in the user
		if (user == null) {
			if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
				user = UserLocalServiceUtil.fetchUserByEmailAddress(
					companyId, shibbolethUserHeader);
			}
			else {
				user = UserLocalServiceUtil.fetchUserByScreenName(
					companyId, shibbolethUserHeader);
			}
		}

		// Gather Shibboleth user data from environment
		String shibbolethUserNameHeader = request.getHeader(
			PrefsPropsUtil.getString(
				companyId, PropsKeys.SHIBBOLETH_USERNAME_HEADER,
				PropsValues.SHIBBOLETH_USERNAME_HEADER));
		String screenName = shibbolethUserNameHeader.replaceAll("@", ".at.");

		String shibbolethUserEMailHeader = request.getHeader(
			PrefsPropsUtil.getString(
				companyId, PropsKeys.SHIBBOLETH_EMAIL_HEADER,
				PropsValues.SHIBBOLETH_EMAIL_HEADER));

		String shibbolethUserFirstNameHeader = request.getHeader(
			PrefsPropsUtil.getString(
				companyId, PropsKeys.SHIBBOLETH_FIRSTNAME_HEADER,
				PropsValues.SHIBBOLETH_FIRSTNAME_HEADER));

		String shibbolethUserLastNameHeader = request.getHeader(
			PrefsPropsUtil.getString(
				companyId, PropsKeys.SHIBBOLETH_LASTNAME_HEADER,
				PropsValues.SHIBBOLETH_LASTNAME_HEADER));


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

		// Gather Shibboleth group data from environment and Lifery groups

		String shibbolethGroupsHeader = request.getHeader(
			PrefsPropsUtil.getString(
				companyId, PropsKeys.SHIBBOLETH_GROUPS_HEADER,
				PropsValues.SHIBBOLETH_GROUPS_HEADER));

		String shibbolethGroupsTouse = PrefsPropsUtil.getString(
				companyId, PropsKeys.SHIBBOLETH_GROUPS_TOUSE,
				PropsValues.SHIBBOLETH_GROUPS_TOUSE);

		// map Shibboleth groups to Liferay groups  
		if ((null != shibbolethGroupsTouse) && (null != shibbolethGroupsHeader) && (shibbolethGroupsHeader.length()) > 0 && (shibbolethGroupsTouse.length() > 0)) {
		  	if (PrefsPropsUtil.getBoolean(companyId, PropsKeys.SHIBBOLETH_GROUPS_ENABLEMAPPING,PropsValues.SHIBBOLETH_GROUPS_ENABLEMAPPING)) {

			  	// remove all groups
			  	UserGroupLocalServiceUtil.setUserUserGroups(user.getUserId(), new long[0]);

				// prepare group arrays
		 		String shibbolethGroups[] = shibbolethGroupsHeader.split(";");
				String liferayGroups[] = shibbolethGroupsTouse.split(";");

				List liferayGroupsList = new ArrayList();
				Collections.addAll(liferayGroupsList, liferayGroups); 
				
				// check for every Shibboleth group, wether the group should be mapped to Lifay
				for (String element: shibbolethGroups)
				{
					List<Long> userGroups = new ArrayList<Long>();
					if (liferayGroupsList.contains(element)) {
						userGroups.add(UserGroupLocalServiceUtil.getUserGroup(companyId, element).getUserGroupId());
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
		}
		
		String[] credentials = new String[3];

		credentials[0] = String.valueOf(user.getUserId());
		credentials[1] = user.getPassword();
		credentials[2] = Boolean.TRUE.toString();

		return credentials;
	}



}
