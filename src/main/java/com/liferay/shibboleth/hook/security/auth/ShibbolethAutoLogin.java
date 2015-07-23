/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * Copyright (c) 2014 Georg-August-Universität Göttingen
 * <p/>
 * Liferay Shibboleth hook based on Liferay plugins by Liferay, Inc.
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.shibboleth.hook.security.auth;

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
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserGroupLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.shibboleth.util.PropsKeys;
import com.liferay.shibboleth.util.PropsValues;
import com.liferay.shibboleth.util.ShibbolethUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

/**
 * @author Eric Chin
 * @author Carsten Thiel
 * @author Mike Bryant
 */

public class ShibbolethAutoLogin extends BaseAutoLogin {

    private static Log _log = LogFactoryUtil.getLog(ShibbolethAutoLogin.class);

    private static class AttributeFetcher {
        private final HttpServletRequest request;
        private final long companyId;
        private final boolean fromHeader;
        private final boolean reEncode;

        public AttributeFetcher(HttpServletRequest request, long companyId, boolean fromHeader, boolean reEncode) {
            this.request = request;
            this.companyId = companyId;
            this.fromHeader = fromHeader;
            this.reEncode = reEncode;
        }

        public String getAttribute(String key, String defaultValue) throws Exception {
            String fullKey = PrefsPropsUtil.getString(companyId, key, defaultValue);
            String value = (String) (fromHeader
                    ? request.getHeader(fullKey)
                    : request.getAttribute(fullKey));
            System.out.println("Fetching " + fullKey + " from " + (fromHeader ? "headers" : "attrs") + " -> " + value);
            return reEncode ? reEncode(value) : value;
        }

        private String reEncode(String s) throws UnsupportedEncodingException {
            // For reasons that are not entirely clear, UTF-8 values set by
            // Shibboleth are read by the servlet as ISO-8859-1, and thus
            // end up garbled. This function converts them back again, but
            // may throw an error if the encoding is not supported.
            return new String(s.getBytes("ISO-8859-1"), "UTF-8");
        }
    }

    // addUser taken from SSO plugin
    protected User addUser(
            long companyId, String firstName, String lastName,
            String emailAddress, String screenName, Locale locale)
            throws Exception {

        long creatorUserId = 0;
        boolean autoPassword = true;
        String password1 = null;
        String password2 = null;
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

        request.setCharacterEncoding("UTF-8");
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = (String) headerNames.nextElement();
            System.out.println(String.format(" - Header: %-20s : %s", name,
                    request.getHeader(name)));
        }

        Company company = PortalUtil.getCompany(request);

        long companyId = company.getCompanyId();

        if (!ShibbolethUtil.isEnabled(companyId)) {
            return null;
        }

        // Gather attribute fetching options.
        boolean fetchFromHeaders = PrefsPropsUtil.getBoolean(
                companyId, PropsKeys.SHIBBOLETH_HEADERS_ENABLED,
                PropsValues.SHIBBOLETH_HEADERS_ENABLED);

        boolean reEncodeValues = PrefsPropsUtil.getBoolean(
                companyId, PropsKeys.SHIBBOLETH_REENCODE_ENABLED,
                PropsValues.SHIBBOLETH_REENCODE_ENABLED);

        AttributeFetcher fetcher = new AttributeFetcher(request, companyId,
                fetchFromHeaders, reEncodeValues);
        String shibbolethUserName = fetcher
                .getAttribute(PropsKeys.SHIBBOLETH_USERNAME_HEADER, PropsValues.SHIBBOLETH_USERNAME_HEADER);
        String screenName = shibbolethUserName != null ? shibbolethUserName.replaceAll("@", "") : null;
        String shibbolethUserEmail = fetcher
                .getAttribute(PropsKeys.SHIBBOLETH_EMAIL_HEADER, PropsValues.SHIBBOLETH_EMAIL_HEADER);
        String shibbolethUserFirstName = fetcher
                .getAttribute(PropsKeys.SHIBBOLETH_FIRSTNAME_HEADER, PropsValues.SHIBBOLETH_FIRSTNAME_HEADER);
        String shibbolethUserLastName = fetcher
                .getAttribute(PropsKeys.SHIBBOLETH_LASTNAME_HEADER, PropsValues.SHIBBOLETH_LASTNAME_HEADER);
        String shibbolethGroups = fetcher
                .getAttribute(PropsKeys.SHIBBOLETH_GROUPS_HEADER, PropsValues.SHIBBOLETH_GROUPS_HEADER);

        if (Validator.isNull(shibbolethUserName) || Validator.isNull(shibbolethUserEmail)) {
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
                            companyId, shibbolethUserEmail, StringPool.BLANK);
                } else {
                    user = PortalLDAPImporterUtil.importLDAPUser(
                            companyId, StringPool.BLANK, shibbolethUserName);
                }
            } catch (SystemException se) {
            }
        }

        // log in the user
        if (user == null) {
            if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
                user = UserLocalServiceUtil.fetchUserByEmailAddress(
                        companyId, shibbolethUserEmail);
            } else {
                user = UserLocalServiceUtil.fetchUserByScreenName(
                        companyId, shibbolethUserName);
            }
        }

        // create a liferay user if none exists
        // taken from SSO plugin
        if (user == null) {
            boolean autoCreateUser = PrefsPropsUtil.getBoolean(
                    companyId, PropsKeys.SHIBBOLETH_USER_AUTO_CREATE,
                    PropsValues.SHIBBOLETH_USER_AUTO_CREATE);

            if (autoCreateUser) {
                ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(
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
                        companyId, shibbolethUserFirstName, shibbolethUserLastName, shibbolethUserEmail, screenName,
                        locale);
            } else {
                // We can't continue...
                return null;
            }
        }

        // Get used Liferay groups
        String shibbolethGroupsDelimiter = PrefsPropsUtil.getString(
                companyId, PropsKeys.SHIBBOLETH_GROUPS_HEADER_SPLIT,
                PropsValues.SHIBBOLETH_GROUPS_HEADER_SPLIT);

        boolean groupsEnableMapping = PrefsPropsUtil
                .getBoolean(companyId, PropsKeys.SHIBBOLETH_GROUPS_ENABLEMAPPING, PropsValues.SHIBBOLETH_GROUPS_ENABLEMAPPING);

        // map Shibboleth groups to Liferay groups
        if (shibbolethGroupsDelimiter != null
                && shibbolethGroups != null
                && shibbolethGroups.length() > 0
                && groupsEnableMapping) {

            // remove all groups
            UserGroupLocalServiceUtil.setUserUserGroups(user.getUserId(), new long[0]);

            // try to map each Shibboleth group to a Liferay group
            String shibbolethGroupList[] = shibbolethGroups.split(shibbolethGroupsDelimiter);
            List<Long> userGroups = new ArrayList<Long>();
            for (String element : shibbolethGroupList) {
                try {
                    userGroups.add(UserGroupLocalServiceUtil.getUserGroup(companyId, element).getUserGroupId());
                } catch (Exception e) {
                    // ignore Shibboleth group if no matching Liferay group exists
                }
            }
            // convert List<Long> to long[]
            long[] userGroupIds = new long[userGroups.size()];
            for (int i = 0; i < userGroups.size(); i++) {
                userGroupIds[i] = userGroups.get(i);
            }
            // set usergroups
            UserGroupLocalServiceUtil.setUserUserGroups(user.getUserId(), userGroupIds);
        }

        String[] credentials = new String[3];

        credentials[0] = String.valueOf(user.getUserId());
        credentials[1] = user.getPassword();
        credentials[2] = Boolean.TRUE.toString();

        return credentials;
    }
}
