package com.katalon.plugin.jira.core;

import com.katalon.platform.api.controller.ReportController;
import com.katalon.platform.api.controller.TestCaseController;
import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.model.HasIntegration;
import com.katalon.platform.api.model.Integration;
import com.katalon.platform.api.model.ReportEntity;
import com.katalon.platform.api.model.TestCaseEntity;
import com.katalon.plugin.jira.core.constant.StringConstants;
import com.katalon.plugin.jira.core.entity.*;
import com.katalon.plugin.jira.core.util.JsonUtil;
import com.katalon.plugin.jira.core.util.PlatformUtil;

import java.util.Map;
import java.util.Optional;

public class JiraObjectToEntityConverter {
    private static <T extends JiraIntegratedObject> Optional<T> getJiraObject(HasIntegration entity, Class<T> clazz) {
        Integration integratedEntity = entity.getIntegration(StringConstants.JIRA_NAME);
        if (integratedEntity == null) {
            return Optional.empty();
        }

        return Optional.of((T) JsonUtil
                .fromJson(integratedEntity.getProperties().get(StringConstants.INTEGRATED_VALUE_NAME), clazz));
    }

    public static Optional<JiraReport> getOptionalJiraReport(ReportEntity report) {
        return getJiraObject(report, JiraReport.class);
    }

    public static JiraReport getJiraReport(ReportEntity report) {
        return getJiraObject(report, JiraReport.class).map(jiraReport -> jiraReport).orElse(new JiraReport());
    }

    public static JiraIssue getJiraIssue(TestCaseEntity testCase) {
        return getJiraObject(testCase, JiraIntegratedIssue.class).map(integrated -> integrated.getJiraIssue())
                .orElse(null);
    }

    public static TestCaseEntity updateTestCase(TestCaseEntity testCase, TestCaseController.UpdateDescription updateDescription)
            throws JiraIntegrationException {
        try {
            return PlatformUtil.getPlatformController(TestCaseController.class)
                    .updateTestCase(PlatformUtil.getCurrentProject(), testCase, updateDescription);
        } catch (ResourceException e) {
            throw new JiraIntegrationException(e);
        }
    }

    public static TestCaseEntity updateTestCaseJiraIssueLink(JiraIssue issue, TestCaseEntity testCase)
            throws JiraIntegrationException {
        Integration jiraIntegratedEntity = new Integration() {

            @Override
            public Map<String, String> getProperties() {
                return new JiraIntegratedIssue(issue).getIntegratedValue();
            }

            @Override
            public String getName() {
                return StringConstants.JIRA_NAME;
            }
        };
        try {
            return PlatformUtil.getPlatformController(TestCaseController.class)
                    .updateIntegration(PlatformUtil.getCurrentProject(), testCase, jiraIntegratedEntity);
        } catch (ResourceException e) {
            throw new JiraIntegrationException(e);
        }
    }

    public static Optional<JiraIssueCollection> getOptionalJiraIssueCollection(ReportEntity report,
            int testCaseLogIndex) {
        return getOptionalJiraReport(report)
                .map(jiraReport -> Optional.ofNullable(jiraReport.getIssueCollectionMap().get(testCaseLogIndex)))
                .orElse(Optional.empty());
    }

    public static ReportEntity updateJiraReport(JiraReport jiraReport, ReportEntity report)
            throws JiraIntegrationException {
        Integration jiraIntegratedEntity = new Integration() {

            @Override
            public Map<String, String> getProperties() {
                return jiraReport.getIntegratedValue();
            }

            @Override
            public String getName() {
                return StringConstants.JIRA_NAME;
            }
        };
        try {
            return PlatformUtil.getPlatformController(ReportController.class)
                    .updateIntegration(PlatformUtil.getCurrentProject(), report, jiraIntegratedEntity);
        } catch (ResourceException e) {
            throw new JiraIntegrationException(e);
        }
    }
}
