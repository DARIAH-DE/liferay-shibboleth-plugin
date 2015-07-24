Cendari Liferay Shibboleth Plugin
=================================

The [Liferay Shibboleth Plugin](https://github.com/CENDARI/liferay-plugins/tree/master/hooks/shibboleth-hook)
adapted for CENDARI. These modifications add several options to the Shibboleth plugin to enable auto-creating
user accounts on first login and mapping of Shibboleth groups to Liferay roles.

**NB**: This altered version of the plugin also contains a workaround for the incorrect decoding of UTF-8
attribute values passed from Shibboleth to the servlet container. This can be enabled in the Shobboleth
plugin options and will re-encode ISO-8859-1 encoded values back to UTF-8. This option should be removed
if/when a better fix is found.

To build:

    mvn package

Then upload the resulting WAR via Liferay's Control Panel -> Apps -> Install action.

TODO
====

 - Figure out how to set a display name for the hook

Liferay Portal Community Edition License
========================================

This library, Liferay Portal Community Edition, is free software ("Licensed Software"); you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; including but not limited to,
the implied warranty of MERCHANTABILITY, NONINFRINGEMENT, or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
