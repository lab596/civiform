# Software Stack

CiviForm is built on the [Play Framework](https://www.playframework.com/) in Java, and backed by a [PostgreSQL](https://www.postgresql.org/) database.

## Views

Instead of the default templating language for Play (Twirl), CiviForm uses the [J2Html](https://j2html.com/) Java library to render HTML (server-side).

All view classes should extend [`BaseHtmlView`](https://github.com/seattle-uat/civiform/blob/main/universal-application-tool-0.0.1/app/views/BaseHtmlView.java), which has some helpful common tag helper methods. Its `makeCsrfTokenInputTag` must be added to all CiviForm forms.

[`ViewUtils`](https://github.com/seattle-uat/civiform/blob/main/universal-application-tool-0.0.1/app/views/ViewUtils.java) is a utility class for accessing stateful view dependencies.

The `View` classes are generally organized by which role(s) they are viewable by (e.g., [app/view/admin/](https://github.com/seattle-uat/civiform/tree/main/universal-application-tool-0.0.1/app/views/admin) for pages viewable by Admins, [app/views/applicant/](https://github.com/seattle-uat/civiform/tree/main/universal-application-tool-0.0.1/app/views/applicant) for pages viewable by Applicants). Each of these roles also has its own [`Layout` class](https://github.com/seattle-uat/civiform/blob/main/universal-application-tool-0.0.1/app/views/admin/AdminLayout.java) that extends `BaseHtmlLayout` for rendering page content in the context of that role.

# AWS Infra for Seattle Instance

-  ArchiMate Output [Diagram](https://drive.google.com/file/d/17JGYn9aB12Iig-C6CyQQI0rafFFhJYzz/view?usp=sharing)
-  ArchiMate [File](https://drive.google.com/file/d/17JGYn9aB12Iig-C6CyQQI0rafFFhJYzz/view?usp=sharing)