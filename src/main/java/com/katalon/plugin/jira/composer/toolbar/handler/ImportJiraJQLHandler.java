package com.katalon.plugin.jira.composer.toolbar.handler;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.atlassian.jira.rest.client.api.domain.Field;
import com.katalon.platform.api.controller.TestCaseController;
import com.katalon.platform.api.exception.PlatformException;
import com.katalon.platform.api.model.FolderEntity;
import com.katalon.platform.api.model.ProjectEntity;
import com.katalon.platform.api.model.TestCaseEntity;
import com.katalon.platform.api.ui.DialogActionService;
import com.katalon.platform.api.ui.TestExplorerActionService;
import com.katalon.platform.api.ui.UISynchronizeService;
import com.katalon.plugin.jira.composer.JiraUIComponent;
import com.katalon.plugin.jira.composer.constant.ComposerJiraIntegrationMessageConstant;
import com.katalon.plugin.jira.composer.constant.StringConstants;
import com.katalon.plugin.jira.composer.toolbar.dialog.ImportJiraJQLDialog;
import com.katalon.plugin.jira.composer.toolbar.dialog.IssueSelectionDialog;
import com.katalon.plugin.jira.core.JiraCredential;
import com.katalon.plugin.jira.core.JiraIntegrationAuthenticationHandler;
import com.katalon.plugin.jira.core.JiraIntegrationException;
import com.katalon.plugin.jira.core.JiraObjectToEntityConverter;
import com.katalon.plugin.jira.core.entity.ImprovedIssue;
import com.katalon.plugin.jira.core.entity.JiraFilter;
import com.katalon.plugin.jira.core.entity.JiraIssue;
import com.katalon.plugin.jira.core.util.PlatformUtil;

public class ImportJiraJQLHandler implements JiraUIComponent {

    public void execute(Shell activeShell) {
        ImportJiraJQLDialog dialog = new ImportJiraJQLDialog(activeShell);
        if (dialog.open() != ImportJiraJQLDialog.OK) {
            return;
        }
        JiraFilter filter = dialog.getFilter();
        try {
            FolderEntity folder = PlatformUtil.getUIService(DialogActionService.class)
                    .showTestCaseFolderSelectionDialog(activeShell, "Test Case Folder Selection");

            if (folder != null) {
                IssueSelectionDialog selectionDialog = new IssueSelectionDialog(activeShell, folder,
                        filter.getIssues());
                if (selectionDialog.open() != IssueSelectionDialog.OK) {
                    return;
                }
                createTestCasesAsIssues(folder, selectionDialog.getSelectedIssues());
            }
        } catch (PlatformException e) {
            MessageDialog.openError(activeShell, StringConstants.ERROR, e.getMessage());
        }
    }

    public void createTestCasesAsIssues(FolderEntity folder, List<JiraIssue> issues) {
        if (folder == null || issues.isEmpty()) {
            return;
        }
        final TestCaseController testCaseController = PlatformUtil.getPlatformController(TestCaseController.class);
        final ProjectEntity currentProject = getCurrentProject();
        Job job = new Job(ComposerJiraIntegrationMessageConstant.JOB_TASK_IMPORTING_ISSUES) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask(StringUtils.EMPTY, issues.size());
                try {
                    monitor.setTaskName(ComposerJiraIntegrationMessageConstant.JOB_SUB_TASK_FETCHING_KATALON_FIELD);
                    Optional<Field> katalonCommentField = getKatalonCommentField(getCredential());
                    monitor.worked(1);
                    List<TestCaseEntity> testCases = new ArrayList<>();
                    for (JiraIssue issue : issues) {
                        if (monitor.isCanceled()) {
                            return Status.CANCEL_STATUS;
                        }
                        String newTestCaseName = testCaseController.getAvailableTestCaseName(currentProject, folder,
                                issue.getKey());
                        monitor.setTaskName(MessageFormat.format(
                                ComposerJiraIntegrationMessageConstant.JOB_SUB_TASK_IMPORTING_ISSUE, newTestCaseName));
                        String description = getDescriptionFromIssue(issue);
                        String comment = getComment(katalonCommentField, issue);
                        TestCaseEntity testCase = testCaseController.newTestCase(currentProject, folder,
                                new NewTestCaseIssueDescription(newTestCaseName, description, comment));

                        testCase = JiraObjectToEntityConverter.updateTestCase(issue, testCase);

                        FileUtils.write(testCase.getScriptFile(), getScriptAsComment(comment), true);
                        testCases.add(testCase);
                        monitor.worked(1);
                    }

                    TestExplorerActionService explorerActionService = PlatformUtil.getUIService(TestExplorerActionService.class);
                    explorerActionService.refreshFolder(currentProject, folder);
                    explorerActionService.selectTestCases(currentProject,
                            testCases);
                    return Status.OK_STATUS;
                } catch (PlatformException | JiraIntegrationException | IOException e) {
                    PlatformUtil.getUIService(UISynchronizeService.class).syncExec(() -> {
                        MessageDialog.openError(null, StringConstants.ERROR, e.getMessage());
                    });
                    return Status.CANCEL_STATUS;
                } finally {
                    monitor.done();
                }
            }

            private Optional<Field> getKatalonCommentField(JiraCredential jiraCredential) throws IOException {
                try {
                    return new JiraIntegrationAuthenticationHandler().getKatalonCustomField(jiraCredential);
                } catch (JiraIntegrationException e) {
                    return Optional.empty();
                }
            }

            private String getComment(Optional<Field> katalonField, JiraIssue issue) {
                if (!katalonField.isPresent()) {
                    return StringUtils.EMPTY;
                }
                ImprovedIssue fields = issue.getFields();
                if (fields == null) {
                    return StringUtils.EMPTY;
                }
                Map<String, Object> customFields = fields.getCustomFields();
                String customFieldId = katalonField.get().getId();
                if (!customFields.containsKey(customFieldId)) {
                    return StringUtils.EMPTY;
                }
                Object jsonComment = customFields.get(customFieldId);
                return jsonComment != null ? jsonComment.toString() : "";
            }

            private String getScriptAsComment(String comment) {
                StringBuilder commentBuilder = new StringBuilder();
                Arrays.asList(StringUtils.split(comment, "\r\n")).forEach(line -> {
                    commentBuilder.append(String.format("WebUI.comment('%s')\n", StringEscapeUtils.escapeJava(line)));
                });
                return commentBuilder.toString();
            }

            private String getDescriptionFromIssue(JiraIssue issue) {
                return String.format("%s: %s\n%s: %s", StringConstants.SUMMARY,
                        StringUtils.defaultString(issue.getFields().getSummary()), StringConstants.DESCRIPTION,
                        StringUtils.defaultString(issue.getFields().getDescription()));
            }

        };
        job.setUser(true);
        job.schedule();

    }

    private static class NewTestCaseIssueDescription implements TestCaseController.NewDescription {
        private final String name;

        private final String description;

        private final String comment;

        public NewTestCaseIssueDescription(String name, String description, String comment) {
            this.name = name;
            this.description = description;
            this.comment = comment;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getComment() {
            return comment;
        }

    }
}