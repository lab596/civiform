CiviForm contains the enumerator question type that offers CiviForm Admins a specialized way of structuring their data. Repeated questions can be created for enumerator questions, which allows admins to ask the same question for each repeated entity the applicant lists for the enumerator question.

An enumerator screen allows you to create repeated screens with repeated questions in them. In an enumerator screen, the applicant provides a list of repeated entities (for example, household members). Applicants can view each repeated screen for each repeated entity declared in the enumerator screen.

For example, if a program needs to gather name and date of birth information for each of an applicant's children. You can create an enumerator screen with an enumerator question to list the applicant's children, and a repeated screen with the repeated questions for the name and date of birth. You can choose to put both the name and date of birth repeated questions in the same repeated screen, or create two repeated screens with one question in each screen.

Enumerators do not store question data. Instead, each repeated question of the enumerator stores the data. An Applicant's answers to enumerator questions are only used to help Applicants track their answers within the repeated questions.

A screen can only contain an enumerator when it’s the only question on the screen. Repeated screens are created from enumerator screens. An enumerator screen can be a repeated screen of another enumerator. An enumerator screen's repeated screen appears below the enumerator screen and is indented in the tree. Repeated screens display a label showing the screen they were copied from. When adding repeated screens, the enumerator question in the enumerator screen isn’t copied to the repeated screens.

<img src="https://github.com/seattle-uat/documents/blob/main/EnumeratorQuestion.png">

## Create an enumerator question
<ol>
<li>Sign in to CiviForm as a CiviForm Admin.</li>
<li>Click <strong>Questions</strong> on the navigation bar.</li>
<li>Click <strong>Create new question</strong> and select <strong>Enumerator</strong>.</li>

<li>Enter the information for the question. See table below for the enumerator question fields, along with the expected data input:

<table>
  <tr>
   <td>
<strong>Field</strong>
   </td>
   <td><strong>Expected data</strong>
   </td>
  </tr>
  <tr>
   <td>Question enumerator
   </td>
   <td>Can be set to “does not repeat” or to another enumerator when using nested enumerators.
   </td>
  </tr>
  <tr>
   <td>Name
   </td>
   <td>The name displayed in the global question bank.
   </td>
  </tr>
  <tr>
   <td>Description
   </td>
   <td>The description displayed in the global question bank.
   </td>
  </tr>
  <tr>
   <td>Question text
   </td>
   <td><p>The text displayed to the Applicant.</p>
<p>
For nested enumerators, the “$this” token is required.</p>
   </td>
  </tr>
  <tr>
   <td>Question help text
   </td>
   <td><p>The help text displayed to the Applicant.</p>
<p>
If the question help text field is used, the “$this” token is required for nested enumerators.</p>
   </td>
  </tr>
  <tr>
   <td>Question settings
   </td>
   <td><p>The repeated entity type that’s being enumerated.</p>
<p>
<strong>Tip</strong>: This should be a singular noun, not plural. For example, “Household member”, <em>not</em> “Household members”.</p>
   </td>
  </tr>
</table></li>

<li>Click <strong>Create</strong>.<br/>The new question appears in the list of questions.</li></ol>


## Create a repeated question

Any question type can be a repeated question, including the enumerator type.

<ol>
<li>Sign in to CiviForm as a CiviForm Admin.</li>
<li>Click <strong>Questions</strong> on the navigation bar.</li>
<li>Click <strong>Create new question</strong> and select the question type you want to use.</li>
<li>Enter the information for the question. See table below for the repeated question fields, along with the expected data input:

<table>
  <tr>
   <td>
<strong>Field</strong>
   </td>
   <td><strong>Expected data</strong>
   </td>
  </tr>
  <tr>
   <td>Question enumerator
   </td>
   <td>Select the enumerator this question should be a repeated question for.
   </td>
  </tr>
  <tr>
   <td>Question text
   </td>
   <td><p>Enter the “$this” token to refer to the name of the repeated entity the Applicant will provide. For example, “Monthly income for $this”, or “$this’s monthly income”. The token appears as highlighted in the preview pane.</p>
<p>
For nested repeated questions, you can use “$this.parent” to refer to a higher-level repeated entity. For example, if the top-level enumerator is household members and the current enumerator is household member jobs, “Monthly income for $this.parent from $this” would be something like “Monthly income for Theresa from Hooli”.</p>
   </td>
  </tr>
  <tr>
   <td>Question help text
   </td>
   <td>(Optional) If the question help text field is used, the “$this” token is required.
   </td>
  </tr>
</table>
</li>

<li>Click <strong>Create</strong>.<br/>The new repeated question appears in the list of questions.
</ol>

## Add an enumerator question to an enumerator screen

1. Sign in to CiviForm as a CiviForm Admin.
2. Click **Programs** on the navigation bar and select a program.
3. Click **Edit** > **Manage questions**.
4. Select an existing screen with no other questions (since a screen can only contain an enumerator when it’s the only question in the screen) or click **Add screen**.
5. Add an enumerator question to the screen by selecting it from the question bank.<br/>The question appears within the screen and the “Create repeated screen” button is visible.
6. (Optional) To modify a screen’s name and description, click **Edit name and description**.

## Add a repeated question to a repeated screen

1. Sign in to CiviForm as a CiviForm Admin.
2. Click **Programs** on the navigation bar and select a program.
3. Click **Edit** > **Manage questions**.
4. Locate an enumerator screen.
5. Click **Create repeated screen**.<br/>The screen appears at the bottom of the repeated screens list for the enumerator screen.
6. Add a repeated question to the screen by selecting it from the question bank.<br/>The question bank shows only the questions you can add to a screen, so if there are no questions available that means you need to create repeated questions. The question then appears within the screen.
7. (Optional) To modify a screen’s name and description, click **Edit name and description**.

***

| IMPORTANT: You can only remove an enumerator question if there are no repeated screens.<br/>Similarly, you cannot delete an enumerator screen if there are repeated screens. |
| --- |

## Remove an enumerator from a screen

1. Sign in to CiviForm as a CiviForm Admin.
2. Click **Programs** on the navigation bar and select a program.
3. Click **Edit** > **Manage questions**.
4. Find the enumerator question within a screen you want to remove and select it.<br/>The question is removed from the screen and it returns to the question bank. The “Create repeated screen” button is also removed.

## Remove a repeated screen

1. Sign in to CiviForm as a CiviForm Admin.
2. Click **Programs** on the navigation bar and select a program.
3. Click **Edit** > **Manage questions**.
4. Find the repeated screen you want to remove and click **Delete screen**. \
The screen is removed from the program.

_Last updated: June 2021_