package com.katalon.plugin.jira.core.issue;

import com.atlassian.jira.rest.client.api.domain.Field;
import com.katalon.platform.api.controller.TestCaseController;
import com.katalon.plugin.jira.core.JiraCredential;
import com.katalon.plugin.jira.core.entity.JiraIssue;
import com.katalon.plugin.jira.core.setting.JiraIntegrationSettingStore;

import java.util.Optional;

public class NewTestCaseIssueDescription extends TestCaseIssueProxyDescription implements TestCaseController.NewDescription {
    public static class Builder {
        private String testCaseName;
        private JiraIssue issue;
        private JiraCredential jiraCredential;
        private JiraIntegrationSettingStore settingStore;
        private Optional<Field> katalonCommentField;

        public static Builder create() {
            return new Builder();
        }

        public Builder setTestCaseName(String testCaseName) {
            this.testCaseName = testCaseName;
            return this;
        }

        public Builder setIssue(JiraIssue issue) {
            this.issue = issue;
            return this;
        }

        public Builder setJiraCredential(JiraCredential jiraCredential) {
            this.jiraCredential = jiraCredential;
            return this;
        }

        public Builder setSettingStore(JiraIntegrationSettingStore settingStore) {
            this.settingStore = settingStore;
            return this;
        }

        public Builder setKatalonCommentField(Optional<Field> katalonCommentField) {
            this.katalonCommentField = katalonCommentField;
            return this;
        }

        public NewTestCaseIssueDescription build()  {
            return new NewTestCaseIssueDescription(jiraCredential, issue, settingStore, katalonCommentField, testCaseName);
        }
    }

    private final String testCaseName;


    public NewTestCaseIssueDescription(JiraCredential jiraCredential, JiraIssue linkedJiraIssue, JiraIntegrationSettingStore settingStore, Optional<Field> katalonCommentField, String testCaseName) {
        super(jiraCredential, linkedJiraIssue, settingStore, katalonCommentField);
        this.testCaseName = testCaseName;
    }

    @Override
    public String getName() {
        return testCaseName;
    }

    @Override
    public String getDescription() {
        return getTestCaseDescriptionFromJiraIssue();
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
