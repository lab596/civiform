Questions form the structure of a CiviForm program. When a CiviForm Admin creates a question for one of their forms, the question is saved in the global question bank. When programs reuse the same question, all Applicant data related to the question gets autofilled. This reduces duplicate data entry and ensures accuracy by using previously vetted information.

## Create a question
1. Sign in to CiviForm as a CiviForm Admin.
1. Click **Questions** on the navigation bar.
1. Click **Create new question** and select a question type.<br/>For more details on question types, go to [Question types](https://github.com/seattle-uat/documents/wiki/Manage-questions#question-types).
1. Enter the information for the question.
1. Click **Create**.<br/>The new question appears in the list of questions.

**Tip**: You might want to develop a naming convention for your questions. For example, address-residence, address-work, etc.

## Edit a question
You can edit both unpublished and published questions. To edit published questions, you need a new version. For more details on versioning, go to [Manage versions for programs & questions](https://github.com/seattle-uat/documents/wiki/Manage-versions-for-programs-&-questions).

1. Sign in to CiviForm as a CiviForm Admin.
1. Click **Questions** on the navigation bar and select a question.
1. Click **Edit draft**.
1. Modify the question information fields.
1. Click **Update**.

## Archive a question
If a question is no longer in use by any program, you can archive a question.

1. Sign in to CiviForm as a CiviForm Admin.
1. Click **Questions** on the navigation bar and select a question.
1. Click **Archive**.

## Restore an archived question
When you restore an archived question, you can use it in your programs. You can restore an archived question up until the next version is published. For more details on versioning, go to [Manage versions for programs & questions](https://github.com/seattle-uat/documents/wiki/Manage-versions-for-programs-&-questions).

1. Sign in to CiviForm as a CiviForm Admin.
1. Click **Questions** on the navigation bar and select an archived question.
1. Click **Restore archived**.

## Question types
You can customize your program to include multiple different question types. The table below shows the supported question types, along with the expected data input.


<table>
  <tr>
   <td><strong>Type</strong>
   </td>
   <td><strong>Use case and expected data</strong>
   </td>
  </tr>
  <tr>
   <td>Address
   </td>
   <td>An Applicant’s address. For example, residential, work, mailing, school, etc.
   </td>
  </tr>
  <tr>
   <td>Checkbox
   </td>
   <td><p>Useful when Applicants need to check multiple items to answer the question fully.</p>
<p>
<strong>Tip</strong>: If you want Applicants to select only one option from a group of options, use a Dropdown or Radio Button question instead.</p>
   </td>
  </tr>
  <tr>
   <td>Date
   </td>
   <td>Suitable for capturing dates. For example, date of birth, graduation date, employment start date.
   </td>
  </tr>
  <tr>
   <td>Dropdown
   </td>
   <td>Useful for long lists (>8 items) of static data where a single selection is required. For example, a daycare program restricted to certain daycare sites.
   </td>
  </tr>
  <tr>
   <td>Email
   </td>
   <td>An Applicant’s email address.
   </td>
  </tr>
  <tr>
   <td>Enumerator
   </td>
   <td><p>Allows applicants to create a list of one type of entity. For example, household members, vehicles, jobs, etc.</p>
<p>
Enumerators do not store question data. Instead, the data is stored within the repeated questions within the enumerator.</p>
<p>
Enumerators also allow you to dynamically add multiple questions whenever needed, reducing program clutter. For example, you can create a repeater to ask the same questions for every member of an Applicant’s household.</p>
<p>
Enumerator questions must be the only question in an enumerator screen.</p>
<p>
For more details, go to <a href="https://github.com/seattle-uat/documents/wiki/Using-enumerator-questions-&-screens-in-a-program">Using enumerator questions & screens in a program</a>.</p>
   </td>
  </tr>
  <tr>
   <td>File Upload
   </td>
   <td><p>Allows Applicants to upload files to support their application. For example, PDF files and images (PNG, JPG, GIF).</p>
<p>
File Upload questions must be the only question in a screen.</p>
   </td>
  </tr>
  <tr>
   <td>Name
   </td>
   <td>A full, legal name.
   </td>
  </tr>
  <tr>
   <td>Number
   </td>
   <td><p>Applicants can enter a numeric value. For example, annual household income.</p>
<p>
Numbers must be integers only with no decimals allowed. Users can increase or decrease the number using the arrow buttons within the field.</p>
   </td>
  </tr>
  <tr>
   <td>Radio Button
   </td>
   <td><p>Suitable for a short list (&lt;=7 items) of static items where the Applicant is required to select only one option. For example, simple yes/no questions or employment status.</p>
<p>
<strong>Tip</strong>: If you want Applicants to select multiple options in a question, use a Checkbox question instead.</p>
   </td>
  </tr>
  <tr>
   <td>Text
   </td>
   <td>A free form field that can store letters, numbers, characters, or symbols.
   </td>
  </tr>
</table>

_Last updated: June 2021_