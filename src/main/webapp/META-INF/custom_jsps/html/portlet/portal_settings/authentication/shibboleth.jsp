<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * Copyright (c) 2014 Georg-August-Universität Göttingen
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
--%>

<%@ include file="/html/portlet/portal_settings/init.jsp" %>

<%
boolean shibbolethAuthEnabled = PrefsPropsUtil.getBoolean(company.getCompanyId(), _SHIBBOLETH_AUTH_ENABLED_KEY, _SHIBBOLETH_AUTH_ENABLED_VALUE);
boolean shibbolethImportFromLdap = PrefsPropsUtil.getBoolean(company.getCompanyId(), _SHIBBOLETH_IMPORT_FROM_LDAP_KEY, _SHIBBOLETH_IMPORT_FROM_LDAP_VALUE);
boolean shibbolethUserAutoCreate = PrefsPropsUtil.getBoolean(company.getCompanyId(), _SHIBBOLETH_USER_AUTO_CREATE_KEY, _SHIBBOLETH_USER_AUTO_CREATE_VALUE);
String shibbolethLogoutURL = PrefsPropsUtil.getString(company.getCompanyId(), _SHIBBOLETH_LOGOUT_URL_KEY, _SHIBBOLETH_LOGOUT_URL_VALUE);
String shibbolethUserNameHeader = PrefsPropsUtil.getString(company.getCompanyId(), _SHIBBOLETH_USERNAME_HEADER_KEY, _SHIBBOLETH_USERNAME_HEADER_VALUE);
String shibbolethUserEMailHeader = PrefsPropsUtil.getString(company.getCompanyId(), _SHIBBOLETH_EMAIL_HEADER_KEY, _SHIBBOLETH_EMAIL_HEADER_VALUE);
String shibbolethUserFirstNameHeader = PrefsPropsUtil.getString(company.getCompanyId(), _SHIBBOLETH_FIRSTNAME_HEADER_KEY, _SHIBBOLETH_FIRSTNAME_HEADER_VALUE);
String shibbolethUserLastNameHeader = PrefsPropsUtil.getString(company.getCompanyId(), _SHIBBOLETH_LASTNAME_HEADER_KEY, _SHIBBOLETH_LASTNAME_HEADER_VALUE);
boolean shibbolethGroupsEnableMapping = PrefsPropsUtil.getBoolean(company.getCompanyId(), _SHIBBOLETH_GROUPS_ENABLEMAPPING_KEY, _SHIBBOLETH_GROUPS_ENABLEMAPPING_VALUE);
String shibbolethGroupsHeader = PrefsPropsUtil.getString(company.getCompanyId(), _SHIBBOLETH_GROUPS_HEADER_KEY, _SHIBBOLETH_GROUPS_HEADER_VALUE);
String shibbolethGroupsHeaderSplit = PrefsPropsUtil.getString(company.getCompanyId(), _SHIBBOLETH_GROUPS_HEADER_SPLIT_KEY, _SHIBBOLETH_GROUPS_HEADER_SPLIT_VALUE);
boolean shibbolethHeadersEnabled = PrefsPropsUtil.getBoolean(company.getCompanyId(), _SHIBBOLETH_HEADERS_ENABLED_KEY, _SHIBBOLETH_HEADERS_ENABLED_VALUE);
	boolean shibbolethReEncodeEnabled = PrefsPropsUtil.getBoolean(company.getCompanyId(), _SHIBBOLETH_REENCODE_ENABLED_KEY, _SHIBBOLETH_REENCODE_ENABLED_VALUE);
%>

<aui:fieldset>
	<aui:input label="enabled" name='<%= "settings--" + _SHIBBOLETH_AUTH_ENABLED_KEY + "--" %>' type="checkbox" value="<%= shibbolethAuthEnabled %>" />

	<aui:input helpMessage="import-shibboleth-users-from-ldap-help" label="import-shibboleth-users-from-ldap" name='<%= "settings--" + _SHIBBOLETH_IMPORT_FROM_LDAP_KEY + "--" %>' type="checkbox" value="<%= shibbolethImportFromLdap %>" />
	<aui:input label="shibboleth-user-autocreate" helpMessage="shibboleth-user-autocreate-help" name='<%= "settings--" + _SHIBBOLETH_USER_AUTO_CREATE_KEY + "--" %>' type="checkbox" value="<%= shibbolethUserAutoCreate %>" />

	<aui:input cssClass="lfr-input-text-container" label="logout-url" name='<%= "settings--" + _SHIBBOLETH_LOGOUT_URL_KEY + "--" %>' type="text" value="<%= shibbolethLogoutURL %>" />

	<aui:input cssClass="lfr-input-text-container" helpMessage="shibboleth-username-header-help" label="shibboleth-username-header" name='<%= "settings--" + _SHIBBOLETH_USERNAME_HEADER_KEY + "--" %>' type="text" value="<%= shibbolethUserNameHeader %>" />

	<aui:input cssClass="lfr-input-text-container" helpMessage="shibboleth-useremail-header-help" label="shibboleth-useremail-header" name='<%= "settings--" + _SHIBBOLETH_EMAIL_HEADER_KEY + "--" %>' type="text" value="<%= shibbolethUserEMailHeader %>" />

	<aui:input cssClass="lfr-input-text-container" helpMessage="shibboleth-userfirstname-header-help" label="shibboleth-userfirstname-header" name='<%= "settings--" + _SHIBBOLETH_FIRSTNAME_HEADER_KEY + "--" %>' type="text" value="<%= shibbolethUserFirstNameHeader %>" />

	<aui:input cssClass="lfr-input-text-container" helpMessage="shibboleth-userlastname-header-help" label="shibboleth-userlastname-header" name='<%= "settings--" + _SHIBBOLETH_LASTNAME_HEADER_KEY + "--" %>' type="text" value="<%= shibbolethUserLastNameHeader %>" />

	<aui:input label="shibboleth-groups-enablemapping" helpMessage="shibboleth-groups-enablemapping-help" name='<%= "settings--" + _SHIBBOLETH_GROUPS_ENABLEMAPPING_KEY + "--" %>' type="checkbox" value="<%= shibbolethGroupsEnableMapping %>" />

	<aui:input cssClass="lfr-input-text-container" helpMessage="shibboleth-groups-header-help" label="shibboleth-groups-header" name='<%= "settings--" + _SHIBBOLETH_GROUPS_HEADER_KEY + "--" %>' type="text" value="<%= shibbolethGroupsHeader %>" />

	<aui:input cssClass="lfr-input-text-container" helpMessage="shibboleth-groups-header-split-help" label="shibboleth-groups-header-split" name='<%= "settings--" + _SHIBBOLETH_GROUPS_HEADER_SPLIT_KEY + "--" %>' type="text" value="<%= shibbolethGroupsHeaderSplit %>" />

	<aui:input label="shibboleth-headers-enabled" helpMessage="shibboleth-headers-enabled-help" name='<%= "settings--" + _SHIBBOLETH_HEADERS_ENABLED_KEY + "--" %>' type="checkbox" value="<%= shibbolethHeadersEnabled %>" />

	<aui:input label="shibboleth-reencode-enabled" helpMessage="shibboleth-reencode-enabled-help" name='<%= "settings--" + _SHIBBOLETH_REENCODE_ENABLED_KEY + "--" %>' type="checkbox" value="<%= shibbolethReEncodeEnabled %>" />

</aui:fieldset>

<%!
private static final String _SHIBBOLETH_AUTH_ENABLED_KEY = "shibboleth.auth.enabled";

private static final boolean _SHIBBOLETH_AUTH_ENABLED_VALUE = GetterUtil.getBoolean(PropsUtil.get(_SHIBBOLETH_AUTH_ENABLED_KEY));

private static final String _SHIBBOLETH_IMPORT_FROM_LDAP_KEY = "shibboleth.import.from.ldap";

private static final boolean _SHIBBOLETH_IMPORT_FROM_LDAP_VALUE = GetterUtil.getBoolean(PropsUtil.get(_SHIBBOLETH_IMPORT_FROM_LDAP_KEY));

private static final String _SHIBBOLETH_USER_AUTO_CREATE_KEY = "shibboleth.user.auto.create";

private static final boolean _SHIBBOLETH_USER_AUTO_CREATE_VALUE = GetterUtil.getBoolean(PropsUtil.get(_SHIBBOLETH_USER_AUTO_CREATE_KEY));

private static final String _SHIBBOLETH_LOGOUT_URL_KEY = "shibboleth.logout.url";

private static final String _SHIBBOLETH_LOGOUT_URL_VALUE = GetterUtil.getString(PropsUtil.get(_SHIBBOLETH_LOGOUT_URL_KEY));

private static final String _SHIBBOLETH_USERNAME_HEADER_KEY = "shibboleth.user.username.header";

private static final String _SHIBBOLETH_USERNAME_HEADER_VALUE = GetterUtil.getString(PropsUtil.get(_SHIBBOLETH_USERNAME_HEADER_KEY));

private static final String _SHIBBOLETH_EMAIL_HEADER_KEY = "shibboleth.user.email.header";

private static final String _SHIBBOLETH_EMAIL_HEADER_VALUE = GetterUtil.getString(PropsUtil.get(_SHIBBOLETH_EMAIL_HEADER_KEY));

private static final String _SHIBBOLETH_FIRSTNAME_HEADER_KEY = "shibboleth.user.firstName.header";

private static final String _SHIBBOLETH_FIRSTNAME_HEADER_VALUE = GetterUtil.getString(PropsUtil.get(_SHIBBOLETH_FIRSTNAME_HEADER_KEY));

private static final String _SHIBBOLETH_LASTNAME_HEADER_KEY = "shibboleth.user.lastName.header";

private static final String _SHIBBOLETH_LASTNAME_HEADER_VALUE = GetterUtil.getString(PropsUtil.get(_SHIBBOLETH_LASTNAME_HEADER_KEY));

private static final String _SHIBBOLETH_GROUPS_ENABLEMAPPING_KEY = "shibboleth.groups.enable.mapping";

private static final boolean _SHIBBOLETH_GROUPS_ENABLEMAPPING_VALUE = GetterUtil.getBoolean(PropsUtil.get(_SHIBBOLETH_GROUPS_ENABLEMAPPING_KEY));

private static final String _SHIBBOLETH_GROUPS_HEADER_KEY = "shibboleth.groups.header";

private static final String _SHIBBOLETH_GROUPS_HEADER_VALUE = GetterUtil.getString(PropsUtil.get(_SHIBBOLETH_GROUPS_HEADER_KEY));

private static final String _SHIBBOLETH_GROUPS_HEADER_SPLIT_KEY = "shibboleth.groups.header.split";

private static final String _SHIBBOLETH_GROUPS_HEADER_SPLIT_VALUE = GetterUtil.getString(PropsUtil.get(_SHIBBOLETH_GROUPS_HEADER_SPLIT_KEY));

private static final String _SHIBBOLETH_HEADERS_ENABLED_KEY = "shibboleth.headers.enabled";

private static final boolean _SHIBBOLETH_HEADERS_ENABLED_VALUE = GetterUtil.getBoolean(PropsUtil.get(_SHIBBOLETH_HEADERS_ENABLED_KEY));

private static final String _SHIBBOLETH_REENCODE_ENABLED_KEY = "shibboleth.reencode.enabled";

private static final boolean _SHIBBOLETH_REENCODE_ENABLED_VALUE = GetterUtil.getBoolean(PropsUtil.get(_SHIBBOLETH_REENCODE_ENABLED_KEY));
%>

