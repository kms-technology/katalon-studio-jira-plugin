package com.katalon.plugin.jira.core.issue;

import com.atlassian.jira.rest.client.api.domain.Field;
import com.katalon.platform.api.controller.TestCaseController;
import com.katalon.platform.api.model.TestCaseEntity;
import com.katalon.plugin.jira.core.JiraCredential;
import com.katalon.plugin.jira.core.entity.JiraIssue;
import com.katalon.plugin.jira.core.setting.JiraIntegrationSettingStore;

import java.util.Optional;

public class UpdateTestCaseIssueDescription extends TestCaseIssueProxyDescription implements TestCaseController.UpdateDescription {
    public static class Builder {
        private TestCaseEntity testCase;
        private JiraIssue issue;
        private JiraCredential jiraCredential;
        private JiraIntegrationSettingStore settingStore;
        private boolean overrideTestCaseDescriptionFromIssue = false;
        private Optional<Field> katalonCommentField;

        public static UpdateTestCaseIssueDescription.Builder create() {
            return new UpdateTestCaseIssueDescription.Builder();
        }

        public UpdateTestCaseIssueDescription.Builder setTestCase(TestCaseEntity testCase) {
            this.testCase = testCase;
            return this;
        }

        public UpdateTestCaseIssueDescription.Builder setIssue(JiraIssue issue) {
            this.issue = issue;
            return this;
        }

        public UpdateTestCaseIssueDescription.Builder setJiraCredential(JiraCredential jiraCredential) {
            this.jiraCredential = jiraCredential;
            return this;
        }

        public UpdateTestCaseIssueDescription.Builder setSettingStore(JiraIntegrationSettingStore settingStore) {
            this.settingStore = settingStore;
            return this;
        }

        public UpdateTestCaseIssueDescription.Builder setOverrideTestCaseDescriptionFromIssue(boolean overrideTestCaseDescriptionFromIssue) {
            this.overrideTestCaseDescriptionFromIssue = overrideTestCaseDescriptionFromIssue;
            return this;
        }

        public UpdateTestCaseIssueDescription.Builder setKatalonCommentField(Optional<Field> katalonCommentField) {
            this.katalonCommentField = katalonCommentField;
            return this;
        }

        public UpdateTestCaseIssueDescription build() {
            return new UpdateTestCaseIssueDescription(jiraCredential, issue, settingStore, katalonCommentField, testCase, overrideTestCaseDescriptionFromIssue);
        }
    }

    private final TestCaseEntity testCase;
    private final boolean overrideTestCaseDescriptionFromIssue;

    public UpdateTestCaseIssueDescription(JiraCredential jiraCredential, JiraIssue linkedJiraIssue, JiraIntegrationSettingStore settingStore, Optional<Field> katalonCommentField, TestCaseEntity testCase, boolean overrideTestCaseDescriptionFromIssue) {
        super(jiraCredential, linkedJiraIssue, settingStore, katalonCommentField);

        this.testCase = testCase;
        this.overrideTestCaseDescriptionFromIssue = overrideTestCaseDescriptionFromIssue;
    }

    @Override
    public String getDescription() {
        return overrideTestCaseDescriptionFromIssue ? getTestCaseDescriptionFromJiraIssue() : testCase.getDescription();
    }

    @Override
    public String getComment() {
        return getTestCaseComment();
    }

    @Override
    public String getTag() {
        return JIRA_INTEGRATION_TEST_CASE_TAG_VALUE;
    }
}
