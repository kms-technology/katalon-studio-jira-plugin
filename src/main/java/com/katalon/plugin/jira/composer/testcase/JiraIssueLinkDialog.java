package com.katalon.plugin.jira.composer.testcase;

import ch.qos.logback.classic.Logger;
import com.katalon.plugin.jira.composer.JiraUIComponent;
import com.katalon.plugin.jira.composer.constant.ComposerJiraIntegrationMessageConstant;
import com.katalon.plugin.jira.composer.util.Theme;
import com.katalon.plugin.jira.core.JiraIntegrationAuthenticationHandler;
import com.katalon.plugin.jira.core.JiraIntegrationException;
import com.katalon.plugin.jira.core.JiraInvalidURLException;
import com.katalon.plugin.jira.core.entity.JiraIssue;
import com.katalon.plugin.jira.core.setting.JiraIntegrationSettingStore;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import org.slf4j.LoggerFactory;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import java.util.Optional;
import java.util.function.Consumer;

public class JiraIssueLinkDialog extends TitleAreaDialog implements JiraUIComponent {
    public static class Result {
        private final JiraIssue foundJiraIssue;
        private final boolean testCaseDescriptionOverride;

        public Result(JiraIssue jiraIssue, boolean testCaseDescriptionOverride) {
            foundJiraIssue = jiraIssue;
            this.testCaseDescriptionOverride = testCaseDescriptionOverride;
        }

        public JiraIssue getJiraIssue() {
            return foundJiraIssue;
        }

        public boolean getTestCaseDescriptionOverride() {
            return testCaseDescriptionOverride;
        }
    }

    private final Logger logger = (Logger)LoggerFactory.getLogger(JiraIssueLinkDialog.class);

    private final JiraIntegrationAuthenticationHandler jiraIntegrationAuthenticationHandler;
    private final JiraIntegrationSettingStore jiraIntegrationSettingStore;

    private Text txtJiraIssueKey;

    private Label lblLoadingProgress;

    private Text lblOperationError;

    private final Optional<String> linkedIssueKey;

    private Button btnOverrideTestCaseDescription;

    private final Consumer<Result> informInputsCollected;

    public JiraIssueLinkDialog(Shell parentShell, Consumer<Result> onOkButtonPressing, String linkedIssueKey) {
        super(parentShell);
        jiraIntegrationAuthenticationHandler = new JiraIntegrationAuthenticationHandler();
        informInputsCollected = onOkButtonPressing;
        this.linkedIssueKey = StringUtils.isBlank(linkedIssueKey) ? Optional.empty() : Optional.of(linkedIssueKey);
        jiraIntegrationSettingStore = getSettingStore();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        setMessage(ComposerJiraIntegrationMessageConstant.DIA_MESSAGE_LINK_TO_EXISTING_ISSUE, IMessageProvider.INFORMATION);
        setTitle(getDialogTitle());

        Composite composite = (Composite) super.createDialogArea(parent);
        Composite mainComposite = new Composite(composite, SWT.NONE);
        mainComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).margins(10, 10).spacing(10, SWT.DEFAULT).create());
        mainComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

        Label lblLinkJIRAIssue = new Label(mainComposite, SWT.NONE);
        lblLinkJIRAIssue.setLayoutData(GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.TOP).create());
        lblLinkJIRAIssue.setText(ComposerJiraIntegrationMessageConstant.DIA_LBL_LINK_TO_EXISTING_ISSUE);

        Composite jiraIssueKeyContainer = new Composite(mainComposite, SWT.NONE);
        jiraIssueKeyContainer.setLayout(GridLayoutFactory.fillDefaults().numColumns(1).spacing(SWT.DEFAULT, 8).create());
        jiraIssueKeyContainer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

        txtJiraIssueKey = new Text(jiraIssueKeyContainer, SWT.BORDER);
        txtJiraIssueKey.setMessage(ComposerJiraIntegrationMessageConstant.TXT_JIRA_ISSUE_KEY_PLACEHOLDER);
        txtJiraIssueKey.setLayoutData(
                GridDataFactory.fillDefaults().hint(200, SWT.DEFAULT).align(SWT.LEFT, SWT.TOP).create());

        btnOverrideTestCaseDescription = new Button(jiraIssueKeyContainer, SWT.CHECK);
        btnOverrideTestCaseDescription.setText(ComposerJiraIntegrationMessageConstant.BTN_OVERRIDE_TEST_CASE_DESCRIPTION_LABEL);
        btnOverrideTestCaseDescription.setLayoutData(GridDataFactory.fillDefaults().create());

        lblLoadingProgress = new Label(jiraIssueKeyContainer, SWT.NONE);
        lblLoadingProgress.setForeground(Theme.getSecondaryColor());
        lblLoadingProgress.setText(ComposerJiraIntegrationMessageConstant.LBL_LINKING_JIRA_ISSUE_TEXT);
        lblLoadingProgress.setLayoutData(
                GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).create());

        // The Label doesn't support wrapping text
        lblOperationError = new Text(jiraIssueKeyContainer, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
        lblOperationError.setForeground(Theme.getErrorColor());
        lblOperationError.setBackground(parent.getBackground());
        lblOperationError.setLayoutData(
                GridDataFactory.fillDefaults()
                        .hint(getInitialSize().x, SWT.DEFAULT)
                        .align(SWT.FILL, SWT.CENTER).grab(true, false).create());

        toggleLoading(false);
        hideError();
        populateUiControls();
        registerControlEvents();

        return composite;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(getDialogTitle());
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(OK).setEnabled(linkedIssueKey.isPresent());
    }

    @Override
    protected void okPressed() {
        final String issueKey = getSanitizedJiraIssueKeyInput();
        final boolean overrideTestCaseDescription = btnOverrideTestCaseDescription.getSelection();

        // This method is executed in the UI thread so changes in the UI that we want to do in this method
        // (like hiding the label) won't be reflected until the method is finished. So a simple solution is to
        // run the actual processing in another thread so the UI thread can update the UI.
        Job job = new Job(String.format("Job %s.okPress()", getClass().getName())) {
            @Override
            protected IStatus run(IProgressMonitor iProgressMonitor) {
                return asyncHandleOkButtonClick(iProgressMonitor, issueKey, overrideTestCaseDescription);
            }
        };

        job.setPriority(Job.INTERACTIVE);
        job.setSystem(true);
        job.schedule();
   }

    private void populateUiControls() {
        linkedIssueKey.ifPresent(txtJiraIssueKey::setText);
        btnOverrideTestCaseDescription.setSelection(jiraIntegrationSettingStore.getTestCaseDescriptionJiraIssueOverridden());
    }

    private void toggleLoading(boolean visible) {
        lblLoadingProgress.setVisible(visible);
        if (lblLoadingProgress.getLayoutData() instanceof GridData) {
            ((GridData) lblLoadingProgress.getLayoutData()).exclude = !lblLoadingProgress.getVisible();
        }

        txtJiraIssueKey.setEnabled(!visible);
        btnOverrideTestCaseDescription.setEnabled(!visible);

        // One of caller of this method is the event of rendering the
        // dialog content when the buttons are not yet created.
        Optional.ofNullable(getButton(OK)).ifPresent(b -> b.setEnabled(!visible));
        Optional.ofNullable(getButton(CANCEL)).ifPresent(b -> b.setEnabled(!visible));
        reLayout(lblLoadingProgress.getParent());
    }

    private void showError(String text) {
        lblOperationError.setVisible(true);
        lblOperationError.setText(text);
        if (lblOperationError.getLayoutData() instanceof GridData) {
            ((GridData) lblOperationError.getLayoutData()).exclude = !lblOperationError.getVisible();
        }

        reLayout(lblOperationError.getParent());
    }

    private void hideError() {
        lblOperationError.setVisible(false);
        if (lblOperationError.getLayoutData() instanceof GridData) {
            ((GridData) lblOperationError.getLayoutData()).exclude = !lblOperationError.getVisible();
        }

        reLayout(lblOperationError.getParent());
    }

    private void registerControlEvents() {
        txtJiraIssueKey.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                getButton(OK).setEnabled(StringUtils.isNotBlank(getSanitizedJiraIssueKeyInput()));
            }
        });
    }

    private String getDialogTitle() {
        return ComposerJiraIntegrationMessageConstant.DIA_TITLE_LINK_TO_EXISTING_ISSUE;
    }

    private String getSanitizedJiraIssueKeyInput() {
        return StringUtils.strip(txtJiraIssueKey.getText());
    }

    private void reLayout(Composite container) {
        container.layout();
        container.pack();
    }

    private IStatus asyncHandleOkButtonClick(IProgressMonitor iProgressMonitor, String issueKey, boolean overrideTestCaseDescription) {
        Display display = getShell().getDisplay();
        try {
            display.syncExec(() -> {
                hideError();
                toggleLoading(true);
            });

            final JiraIssue issue = jiraIntegrationAuthenticationHandler.getJiraIssue(getCredential(), issueKey);
            display.syncExec(() -> informInputsCollected.accept(new Result(issue, overrideTestCaseDescription)));

            // Only persists when the processing in the caller side is successful
            jiraIntegrationSettingStore.setTestCaseDescriptionJiraIssueOverridden(overrideTestCaseDescription);

            // We want to end before the dialog is closed to ensure that any code that
            // depends on the dialog being open can still access it.
            display.asyncExec(super::okPressed);
        }
        catch (Exception e) {
            display.syncExec(() -> toggleLoading(false));
            logger.error("Error on invoking the handler for JIRA Issue key", e);

            if (e instanceof JiraInvalidURLException) {
                display.syncExec(() -> showError(ComposerJiraIntegrationMessageConstant.LBL_JIRA_ISSUE_NOT_EXISTING_TEXT));
            }
            else {
                String displayMessage = e.getMessage();
                if (e instanceof RuntimeException) {
                    // threw by the invocation from Consumer<JiraIssueLinkDialog. Result>
                    if (e.getCause() instanceof JiraIntegrationException) {
                        displayMessage = String.format("Error on linking the test case to JIRA issue key %s: %s", issueKey, e.getCause().getMessage());
                    }
                }

                final String m = displayMessage;
                display.syncExec(() -> showError(m));
            }
        }
        finally {
            iProgressMonitor.done();
        }

        return Status.OK_STATUS;
    }
}
