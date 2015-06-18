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

package com.liferay.shibboleth.util;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;

/**
 * @author Eric Chin
 */
public class PropsValues {

	public static final boolean SHIBBOLETH_AUTH_ENABLED = GetterUtil.getBoolean(
		PropsUtil.get(PropsKeys.SHIBBOLETH_AUTH_ENABLED));

	public static final boolean SHIBBOLETH_USER_AUTO_CREATE =  GetterUtil.getBoolean(
		PropsUtil.get(PropsKeys.SHIBBOLETH_USER_AUTO_CREATE));
	  	
	public static final boolean SHIBBOLETH_IMPORT_FROM_LDAP =
		GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.SHIBBOLETH_IMPORT_FROM_LDAP));

	public static final String SHIBBOLETH_LOGOUT_URL = GetterUtil.getString(
		PropsUtil.get(PropsKeys.SHIBBOLETH_LOGOUT_URL));

	public static final String SHIBBOLETH_USERNAME_HEADER = GetterUtil.getString(
		PropsUtil.get(PropsKeys.SHIBBOLETH_USERNAME_HEADER));

	public static final String SHIBBOLETH_EMAIL_HEADER = GetterUtil.getString(
		PropsUtil.get(PropsKeys.SHIBBOLETH_EMAIL_HEADER));

	public static final String SHIBBOLETH_FIRSTNAME_HEADER = GetterUtil.getString(
		PropsUtil.get(PropsKeys.SHIBBOLETH_FIRSTNAME_HEADER));

	public static final String SHIBBOLETH_LASTNAME_HEADER = GetterUtil.getString(
		PropsUtil.get(PropsKeys.SHIBBOLETH_LASTNAME_HEADER));

	public static final boolean SHIBBOLETH_GROUPS_ENABLEMAPPING = GetterUtil.getBoolean(
		PropsUtil.get(PropsKeys.SHIBBOLETH_GROUPS_ENABLEMAPPING));

	public static final String SHIBBOLETH_GROUPS_HEADER = GetterUtil.getString(
		PropsUtil.get(PropsKeys.SHIBBOLETH_GROUPS_HEADER));

	public static final String SHIBBOLETH_GROUPS_HEADER_SPLIT = GetterUtil.getString(
		PropsUtil.get(PropsKeys.SHIBBOLETH_GROUPS_HEADER_SPLIT));

	public static final boolean SHIBBOLETH_HEADERS_ENABLED = GetterUtil.getBoolean(
		PropsUtil.get(PropsKeys.SHIBBOLETH_HEADERS_ENABLED));

}
