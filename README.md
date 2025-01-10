**Table of Content**

<!--TOC-->

- [About](#about)
- [Contribution](#contribution)
  - [Resources](#resources)
  - [Setup development environment](#setup-development-environment)
  - [Development tasks](#development-tasks)
    - [Create build package](#create-build-package)
- [Katalon products](#katalon-products)
  - [Katalon TestOps](#katalon-testops)
  - [Katalon Studio](#katalon-studio)
- [Update **Table of Content** section in the README file](#update-table-of-content-section-in-the-readme-file)

<!--TOC-->

---

# About

A Katalon Studio plugin to support the integration with JIRA (self-hosted and cloud-based). This plugin provides the following features:

- Submitting Katalon test run result as a JIRA issue.
- Creating a Katalon test case from a BDD description from JIRA

# Contribution

## Resources

- [Katalon Studio plugin development guide](https://github.com/katalon-studio/katalon-studio-platform/blob/master/docs/tutorials/create-your-first-plugin.md)
- [Install plugin offline in Studio](https://docs.katalon.com/katalon-platform/plugins-and-add-ons/katalon-store/katalon-studio-plugins/installing-plugin-offline-in-katalon-studio#install-plugins-offline)

## Setup development environment

Install the following tools:

- JDK 17
- Maven 3.3+

## Development tasks

### Create build package

```shell script
mvn clean package
```

The command produces a jar file at `target/katalon-studio-jira-plugin-...jar`

# Katalon products

## Katalon TestOps

[Katalon TestOps](https://analytics.katalon.com) is a web-based application that provides dynamic perspectives and an insightful look at your automation testing data. You can leverage your automation testing data by transforming and visualizing your data; analyzing test results; seamlessly integrating with such tools as Katalon Studio and Jira; maximizing the testing capacity with remote execution.

* Read our [documentation](https://docs.katalon.com/katalon-analytics/docs/overview.html).
* Ask a question on [Forum](https://forum.katalon.com/categories/katalon-analytics).
* Request a new feature on [GitHub](CONTRIBUTING.md).
* Vote for [Popular Feature Requests](https://github.com/katalon-analytics/katalon-analytics/issues?q=is%3Aopen+is%3Aissue+label%3Afeature-request+sort%3Areactions-%2B1-desc).
* File a bug in [GitHub Issues](https://github.com/katalon-analytics/katalon-analytics/issues).

## Katalon Studio

[Katalon Studio](https://www.katalon.com) is a free and complete automation testing solution for Web, Mobile, and API testing with modern methodologies (Data-Driven Testing, TDD/BDD, Page Object Model, etc.) as well as advanced integration (JIRA, qTest, Slack, CI, Katalon TestOps, etc.). Learn more about [Katalon Studio features](https://www.katalon.com/features/).

# Update **Table of Content** section in the README file

> Python3 is required

The **Table of Content** section at the top of the README file can be updated with the below code snippet

```shell script
python3 -m venv /tmp/venv/; /tmp/venv/bin/pip install md-toc==8.1.9
/tmp/venv/bin/md_toc -p github ./README.md
```
