package com.katalon.plugin.jira.core.issue;

import com.atlassian.jira.rest.client.api.domain.Field;
import com.katalon.plugin.jira.composer.constant.StringConstants;
import com.katalon.plugin.jira.core.JiraCredential;
import com.katalon.plugin.jira.core.entity.ImprovedIssue;
import com.katalon.plugin.jira.core.entity.JiraIssue;
import com.katalon.plugin.jira.core.setting.JiraIntegrationSettingStore;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A base class for concrete implementation of com.katalon.platform.api.controller.TestCaseController.NewDescription and
 * TestCaseController.NewDescription where the concrete implementation is for creating/changing the object of
 * com.katalon.platform.api.model.TestCaseEntity along with the linked com.katalon.plugin.jira.core.entity.JiraIssue
 */
public abstract class TestCaseIssueProxyDescription {
    private static class TestCaseCommentBag {
        private final Field katalonCommentField;
        private final ImprovedIssue jiraImprovedIssue;

        public TestCaseCommentBag(Field katalonCommentField, ImprovedIssue jiraImprovedIssue) {
            this.katalonCommentField = katalonCommentField;
            this.jiraImprovedIssue = jiraImprovedIssue;
        }

        public Field getKatalonCommentField() {
            return katalonCommentField;
        }

        public ImprovedIssue getJiraImprovedIssue() {
            return jiraImprovedIssue;
        }
    }

    protected static final String JIRA_INTEGRATION_TEST_CASE_TAG_VALUE = "jira-integration";

    protected final JiraCredential jiraCredential;
    protected final JiraIssue linkedJiraIssue;
    protected final JiraIntegrationSettingStore settingStore;
    protected final Optional<Field> katalonCommentField;

    protected TestCaseIssueProxyDescription(JiraCredential jiraCredential, JiraIssue linkedJiraIssue, JiraIntegrationSettingStore settingStore, Optional<Field> katalonCommentField) {
        if (Objects.isNull(katalonCommentField)) {
            throw new IllegalArgumentException("The katalonCommentField field must not be null.");
        }

        if (Objects.isNull(linkedJiraIssue)) {
            throw new IllegalArgumentException("The linkedJiraIssue field must not be null.");
        }

        this.jiraCredential = jiraCredential;
        this.linkedJiraIssue = linkedJiraIssue;
        this.settingStore = settingStore;
        this.katalonCommentField = katalonCommentField;
    }

    private Optional<TestCaseCommentBag> getKatalonTestCaseComment() {
        if (!katalonCommentField.isPresent()) {
            return Optional.empty();
        }

        ImprovedIssue fields = linkedJiraIssue.getFields();
        if (Objects.isNull(fields)) {
            return Optional.empty();
        }

        return Optional.of(new TestCaseCommentBag(katalonCommentField.get(), fields));
    }

    protected String getTestCaseComment() {
        return getKatalonTestCaseComment().map(testCaseCommentBag -> {
            Map<String, Object> customFields = testCaseCommentBag.getJiraImprovedIssue().getCustomFields();
            String customFieldId = testCaseCommentBag.getKatalonCommentField().getId();
            Object jsonCommentValue = customFields.get(customFieldId);

            return Objects.nonNull(jsonCommentValue) ? jsonCommentValue.toString() : StringUtils.EMPTY;
        }).orElse(StringUtils.EMPTY);
    }

    protected String getTestCaseDescriptionFromJiraIssue() {
        return String.format("%1$s: %2$s%n%3$s: %4$s",
                StringConstants.SUMMARY,
                StringUtils.defaultString(linkedJiraIssue.getFields().getSummary()),
                StringConstants.DESCRIPTION,
                StringUtils.defaultString(linkedJiraIssue.getFields().getDescription()));
    }

    public Optional<Boolean> isKatalonCommentFieldPresentInJiraIssue() {
        return getKatalonTestCaseComment().map(testCaseCommentBag -> {
            Map<String, Object> customFields = testCaseCommentBag.getJiraImprovedIssue().getCustomFields();
            String customFieldId = testCaseCommentBag.getKatalonCommentField().getId();

            return customFields.containsKey(customFieldId);
        });
    }
}
