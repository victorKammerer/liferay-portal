/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.rest.resource.v1_0.test;

import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.constants.NotificationRecipientConstants;
import com.liferay.notification.constants.NotificationRecipientSettingConstants;
import com.liferay.notification.constants.NotificationTemplateConstants;
import com.liferay.notification.rest.client.dto.v1_0.Creator;
import com.liferay.notification.rest.client.dto.v1_0.NotificationTemplate;
import com.liferay.notification.rest.client.pagination.Page;
import com.liferay.notification.rest.client.pagination.Pagination;
import com.liferay.notification.rest.client.permission.Permission;
import com.liferay.notification.rest.resource.v1_0.NotificationTemplateResource;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Gabriel Albuquerque
 */
@RunWith(Arquillian.class)
public class NotificationTemplateResourceTest
	extends BaseNotificationTemplateResourceTestCase {

	@Override
	@Test
	public void testGetNotificationTemplate() throws Exception {
		super.testGetNotificationTemplate();

		_testGetNotificationTemplateCreator();
		_testGetNotificationTemplatePermissions();
	}

	@Override
	@Test
	public void testGetNotificationTemplateByExternalReferenceCode()
		throws Exception {

		super.testGetNotificationTemplateByExternalReferenceCode();

		_testGetNotificationTemplateByExternalReferenceCodeNameTranslations();
	}

	@Override
	@Test
	public void testGetNotificationTemplatesPage() throws Exception {
		super.testGetNotificationTemplatesPage();

		_testGetNotificationTemplatesPageWithSystemFilter();
	}

	@Override
	@Test
	public void testGetNotificationTemplatesPageWithSortInteger()
		throws Exception {

		testGetNotificationTemplatesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, notificationTemplate1, notificationTemplate2) -> {
				if (BeanTestUtil.hasProperty(
						notificationTemplate1, entityField.getName())) {

					BeanTestUtil.setProperty(
						notificationTemplate1, entityField.getName(), 0);
				}

				if (BeanTestUtil.hasProperty(
						notificationTemplate2, entityField.getName())) {

					BeanTestUtil.setProperty(
						notificationTemplate2, entityField.getName(), 1);
				}
			});
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetNotificationTemplate() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetNotificationTemplateByExternalReferenceCode()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetNotificationTemplateByExternalReferenceCodeNotFound() {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetNotificationTemplateNotFound() {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetNotificationTemplatesPage() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostNotificationTemplate() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostNotificationTemplateCopy() throws Exception {
	}

	@Override
	@Test
	public void testPatchNotificationTemplate() throws Exception {
		super.testPatchNotificationTemplate();

		NotificationTemplate notificationTemplate =
			randomNotificationTemplate();

		notificationTemplate.setRecipientType(
			NotificationRecipientConstants.TYPE_EMAIL);
		notificationTemplate.setRecipients(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"from", RandomTestUtil.randomString()
				).put(
					"fromName",
					Collections.singletonMap(
						"en_US", RandomTestUtil.randomString())
				).put(
					"to",
					Collections.singletonMap(
						"en_US", RandomTestUtil.randomString())
				).put(
					"toType", NotificationRecipientConstants.TYPE_EMAIL
				).build()
			});
		notificationTemplate.setType(NotificationConstants.TYPE_EMAIL);

		notificationTemplate = _addNotificationTemplate(notificationTemplate);

		JSONObject recipientsJSONObject = JSONUtil.put(
			"from", RandomTestUtil.randomString()
		).put(
			"fromName", JSONUtil.put("en_US", RandomTestUtil.randomString())
		).put(
			"to", JSONUtil.put("en_US", RandomTestUtil.randomString())
		).put(
			"toType", NotificationRecipientConstants.TYPE_EMAIL
		);

		JSONAssert.assertEquals(
			recipientsJSONObject.toString(),
			JSONUtil.getValueAsString(
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"recipients", JSONUtil.put(recipientsJSONObject)
					).toString(),
					"notification/v1.0/notification-templates/" +
						notificationTemplate.getId(),
					Http.Method.PATCH),
				"JSONArray/recipients", "JSONObject/0"),
			JSONCompareMode.LENIENT);
	}

	@Override
	@Test
	public void testPostNotificationTemplate() throws Exception {
		super.testPostNotificationTemplate();

		// Notification template recipient type email

		_testPostNotificationTemplate(
			JSONUtil.put(
				"to", JSONUtil.put("en_US", RandomTestUtil.randomString())
			).put(
				"toType", NotificationRecipientConstants.TYPE_EMAIL
			));

		// Notification template recipient type role

		_testPostNotificationTemplate(
			JSONUtil.put(
				"to",
				JSONUtil.putAll(
					JSONUtil.put(
						NotificationRecipientSettingConstants.NAME_ROLE_NAME,
						AccountRoleConstants.
							REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR),
					JSONUtil.put(
						NotificationRecipientSettingConstants.NAME_ROLE_NAME,
						AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MEMBER),
					JSONUtil.put(
						NotificationRecipientSettingConstants.NAME_ROLE_NAME,
						RoleConstants.ORGANIZATION_ADMINISTRATOR),
					JSONUtil.put(
						NotificationRecipientSettingConstants.NAME_ROLE_NAME,
						RoleConstants.ORGANIZATION_OWNER))
			).put(
				"toType", NotificationRecipientConstants.TYPE_ROLE
			));

		// Notification template recipient type subscribers

		_testPostNotificationTemplate(
			JSONUtil.put(
				"toType", NotificationRecipientConstants.TYPE_SUBSCRIBERS));
	}

	@Override
	@Test
	public void testPostNotificationTemplateCopy() throws Exception {
		super.testPostNotificationTemplateCopy();

		NotificationTemplate systemNotificationTemplate =
			randomNotificationTemplate();

		systemNotificationTemplate.setSystem(true);

		systemNotificationTemplate = _addNotificationTemplate(
			systemNotificationTemplate);

		Assert.assertTrue(systemNotificationTemplate.getSystem());

		NotificationTemplate notificationTemplate =
			notificationTemplateResource.postNotificationTemplateCopy(
				systemNotificationTemplate.getId());

		Assert.assertFalse(notificationTemplate.getSystem());
	}

	@Override
	@Test
	public void testPutNotificationTemplate() throws Exception {
		super.testPutNotificationTemplate();

		_testPutNotificationTemplatePermissions();
		_testPutNotificationTemplatePermissionsWithOmittedRole();
	}

	@Override
	protected NotificationTemplate randomNotificationTemplate()
		throws Exception {

		NotificationTemplate notificationTemplate =
			super.randomNotificationTemplate();

		notificationTemplate.setBody(
			LocalizedMapUtil.getI18nMap(
				RandomTestUtil.randomLocaleStringMap()));
		notificationTemplate.setEditorType(
			NotificationTemplate.EditorType.RICH_TEXT);
		notificationTemplate.setObjectDefinitionExternalReferenceCode(
			StringPool.BLANK);
		notificationTemplate.setObjectDefinitionId(0L);
		notificationTemplate.setRecipients(new Object[0]);
		notificationTemplate.setRecipientType(
			NotificationRecipientConstants.TYPE_USER);
		notificationTemplate.setSubject(
			LocalizedMapUtil.getI18nMap(
				RandomTestUtil.randomLocaleStringMap()));
		notificationTemplate.setType(
			NotificationConstants.TYPE_USER_NOTIFICATION);

		return notificationTemplate;
	}

	@Override
	protected NotificationTemplate
			testDeleteNotificationTemplate_addNotificationTemplate()
		throws Exception {

		return _addNotificationTemplate(randomNotificationTemplate());
	}

	@Override
	protected NotificationTemplate
			testDeleteNotificationTemplateByExternalReferenceCode_addNotificationTemplate()
		throws Exception {

		return _addNotificationTemplate(randomNotificationTemplate());
	}

	@Override
	protected NotificationTemplate
			testGetNotificationTemplate_addNotificationTemplate()
		throws Exception {

		return _addNotificationTemplate(randomNotificationTemplate());
	}

	@Override
	protected NotificationTemplate
			testGetNotificationTemplateByExternalReferenceCode_addNotificationTemplate()
		throws Exception {

		return _addNotificationTemplate(randomNotificationTemplate());
	}

	@Override
	protected NotificationTemplate
			testGetNotificationTemplatesPage_addNotificationTemplate(
				NotificationTemplate notificationTemplate)
		throws Exception {

		return _addNotificationTemplate(notificationTemplate);
	}

	@Override
	protected NotificationTemplate
			testGraphQLNotificationTemplate_addNotificationTemplate()
		throws Exception {

		return _addNotificationTemplate(randomNotificationTemplate());
	}

	@Override
	protected NotificationTemplate
			testPatchNotificationTemplate_addNotificationTemplate()
		throws Exception {

		return _addNotificationTemplate(randomNotificationTemplate());
	}

	@Override
	protected NotificationTemplate
			testPostNotificationTemplate_addNotificationTemplate(
				NotificationTemplate notificationTemplate)
		throws Exception {

		return _addNotificationTemplate(notificationTemplate);
	}

	@Override
	protected NotificationTemplate
			testPostNotificationTemplateCopy_addNotificationTemplate(
				NotificationTemplate notificationTemplate)
		throws Exception {

		return _addNotificationTemplate(notificationTemplate);
	}

	@Override
	protected NotificationTemplate
			testPutNotificationTemplate_addNotificationTemplate()
		throws Exception {

		return _addNotificationTemplate(randomNotificationTemplate());
	}

	@Override
	protected NotificationTemplate
			testPutNotificationTemplateByExternalReferenceCode_addNotificationTemplate()
		throws Exception {

		return _addNotificationTemplate(randomNotificationTemplate());
	}

	private NotificationTemplate _addNotificationTemplate(
			NotificationTemplate notificationTemplate)
		throws Exception {

		notificationTemplate =
			notificationTemplateResource.postNotificationTemplate(
				notificationTemplate);

		_notificationTemplates.add(
			_notificationTemplateLocalService.fetchNotificationTemplate(
				notificationTemplate.getId()));

		return notificationTemplate;
	}

	private List<String> _getRoleNames(Long notificationTemplateId)
		throws Exception {

		return JSONUtil.toList(
			JSONUtil.getValueAsJSONArray(
				HTTPTestUtil.invokeToJSONObject(
					null,
					"notification/v1.0/notification-templates/" +
						notificationTemplateId + "?nestedFields=permissions",
					Http.Method.GET),
				"JSONArray/permissions"),
			permissionJSONObject -> permissionJSONObject.getString("roleName"));
	}

	private NotificationTemplate _randomNotificationTemplate(String roleName)
		throws Exception {

		NotificationTemplate notificationTemplate =
			randomNotificationTemplate();

		Permission permission = new Permission();

		permission.setActionIds(new Object[] {ActionKeys.VIEW});
		permission.setRoleName(roleName);

		notificationTemplate.setPermissions(new Permission[] {permission});

		notificationTemplate.setSystem(false);

		return notificationTemplate;
	}

	private void _testGetNotificationTemplateByExternalReferenceCodeNameTranslations()
		throws Exception {

		String name = RandomTestUtil.randomString();
		String translatedName = RandomTestUtil.randomString();

		NotificationTemplate notificationTemplate =
			randomNotificationTemplate();

		notificationTemplate.setName(() -> name);
		notificationTemplate.setName_i18n(
			HashMapBuilder.put(
				"en_US", name
			).put(
				"pt_BR", translatedName
			).build());

		notificationTemplate = _addNotificationTemplate(notificationTemplate);

		notificationTemplate =
			notificationTemplateResource.
				getNotificationTemplateByExternalReferenceCode(
					notificationTemplate.getExternalReferenceCode());

		Assert.assertEquals(name, notificationTemplate.getName());

		Map<String, String> name_i18n = notificationTemplate.getName_i18n();

		Assert.assertEquals(name, name_i18n.get("en_US"));
		Assert.assertEquals(translatedName, name_i18n.get("pt_BR"));
	}

	private void _testGetNotificationTemplateCreator() throws Exception {
		NotificationTemplate notificationTemplate = _addNotificationTemplate(
			randomNotificationTemplate());

		notificationTemplate =
			notificationTemplateResource.getNotificationTemplate(
				notificationTemplate.getId());

		Creator creator = notificationTemplate.getCreator();

		Assert.assertEquals(
			Long.valueOf(TestPropsValues.getUserId()), creator.getId());
	}

	private void _testGetNotificationTemplatePermissions() throws Exception {
		NotificationTemplate notificationTemplate = _addNotificationTemplate(
			_randomNotificationTemplate(RoleConstants.ADMINISTRATOR));

		List<String> roleNames = _getRoleNames(notificationTemplate.getId());

		Assert.assertTrue(
			roleNames.toString(),
			roleNames.contains(RoleConstants.ADMINISTRATOR));
		Assert.assertFalse(
			roleNames.toString(), roleNames.contains(RoleConstants.GUEST));
	}

	private void _testGetNotificationTemplatesPageWithSystemFilter()
		throws Exception {

		NotificationTemplate notificationTemplate =
			randomNotificationTemplate();

		notificationTemplate.setSystem(false);

		notificationTemplate = _addNotificationTemplate(notificationTemplate);

		NotificationTemplate systemNotificationTemplate =
			randomNotificationTemplate();

		systemNotificationTemplate.setSystem(true);

		systemNotificationTemplate = _addNotificationTemplate(
			systemNotificationTemplate);

		Page<NotificationTemplate> page =
			notificationTemplateResource.getNotificationTemplatesPage(
				null, null, "system eq false", Pagination.of(1, 100), null);

		List<Long> ids = TransformUtil.transform(
			page.getItems(), NotificationTemplate::getId);

		Assert.assertTrue(
			ids.toString(), ids.contains(notificationTemplate.getId()));
		Assert.assertFalse(
			ids.toString(), ids.contains(systemNotificationTemplate.getId()));
	}

	private void _testPostNotificationTemplate(JSONObject recipientJSONObject)
		throws Exception {

		recipientJSONObject.put(
			"from", RandomTestUtil.randomString()
		).put(
			"fromName", JSONUtil.put("en_US", RandomTestUtil.randomString())
		);

		JSONObject notificationTemplateJSONObject = JSONUtil.put(
			"editorType", NotificationTemplateConstants.EDITOR_TYPE_RICH_TEXT
		).put(
			"name", RandomTestUtil.randomString()
		).put(
			"recipients", JSONUtil.putAll(recipientJSONObject)
		).put(
			"subject",
			JSONUtil.put(
				LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
				RandomTestUtil.randomString())
		).put(
			"type", NotificationConstants.TYPE_EMAIL
		);

		JSONAssert.assertEquals(
			recipientJSONObject.toString(),
			JSONUtil.getValueAsString(
				HTTPTestUtil.invokeToJSONObject(
					notificationTemplateJSONObject.toString(),
					"notification/v1.0/notification-templates",
					Http.Method.POST),
				"JSONArray/recipients", "JSONObject/0"),
			JSONCompareMode.NON_EXTENSIBLE);

		NotificationTemplateResource.Builder
			notificationTemplateResourceBuilder =
				_notificationTemplateResourceFactory.create();

		NotificationTemplateResource notificationTemplateResource =
			notificationTemplateResourceBuilder.user(
				TestPropsValues.getUser()
			).build();

		Assert.assertNotNull(
			notificationTemplateResource.postNotificationTemplate(
				com.liferay.notification.rest.dto.v1_0.NotificationTemplate.
					toDTO(notificationTemplateJSONObject.toString())));
	}

	private void _testPutNotificationTemplatePermissions() throws Exception {
		NotificationTemplate notificationTemplate = _addNotificationTemplate(
			_randomNotificationTemplate(RoleConstants.ADMINISTRATOR));

		notificationTemplateResource.putNotificationTemplate(
			notificationTemplate.getId(),
			_randomNotificationTemplate(RoleConstants.GUEST));

		List<String> roleNames = _getRoleNames(notificationTemplate.getId());

		Assert.assertTrue(
			roleNames.toString(), roleNames.contains(RoleConstants.GUEST));
	}

	private void _testPutNotificationTemplatePermissionsWithOmittedRole()
		throws Exception {

		NotificationTemplate notificationTemplate = _addNotificationTemplate(
			_randomNotificationTemplate(RoleConstants.GUEST));

		notificationTemplateResource.putNotificationTemplate(
			notificationTemplate.getId(),
			_randomNotificationTemplate(RoleConstants.ADMINISTRATOR));

		List<String> roleNames = _getRoleNames(notificationTemplate.getId());

		Assert.assertFalse(
			roleNames.toString(), roleNames.contains(RoleConstants.GUEST));
	}

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Inject
	private NotificationTemplateResource.Factory
		_notificationTemplateResourceFactory;

	@DeleteAfterTestRun
	private List<com.liferay.notification.model.NotificationTemplate>
		_notificationTemplates = new ArrayList<>();

}