The UAT (Universal Application Tool) supports multiple **programs** (Utility Discount Program, Seattle Preschool Program, Vehicle License Fee Rebate Program, etc.). Each program has one associated **application form**, or just **form**.


## Form Builder 

The UAT admin can create and edit forms in the **form builder** tool.

The form is composed of one or more **sections**, along with **rules** for transitioning from one section to the next. We'll come back to the rules shortly.

Each section is composed of one or more **questions**. Each question, in turn, is made of:

* A **title** (e.g. "What is your mailing address?")
* An optional **description** or subtitle (e.g. "This can be a PO Box.")
* A boolean flag specifying whether or not this question is **required**
* A **input type** specifying the type of form field(s) that will be used to gather the applicant's answer.


### Input types

The **input type** can be any of the following **primitive input types**:

* Text
* Date
* Integer
* Floating-point Number
* File upload
* Signature
* Checkbox(es)
* Dropdown or Radio buttons

Alternatively, the input type could also be a **widget**, which groups multiple primitive input types into one pre-built collection with custom functionality, such as:

* Name (a collection of Text inputs)
* Address (a collection of Text inputs)
* Race and Ethnicity (a combination of Checkboxes and Text inputs)
* Income (a fancy Number input with a few extra buttons and links attached)

Some example questions could look like:

| Field | Value |
| --- | --- |
| Title | Are you a veteran? |
| Description | *none* |
| Required | False |
| Input type | Checkbox |

| Field | Value |
| --- | --- |
| Title | How many dependents do you have? |
| Description | They may or may not all live in your household. |
| Required | True |
| Input type | Integer |

| Field | Value |
| --- | --- |
| Title | What is your mailing address? |
| Description | This can be a PO Box. |
| Required | True |
| Input type | Address |

| Field | Value |
| --- | --- |
| Title | What is your work address? |
| Description | Only enter if you are employed. |
| Required | False |
| Input type | Address |

### Custom input types

UAT admins can also create their own **custom widget** by specifying the list of **sub-inputs** it contains. They must specify the primitive input type for each sub-input. For instance, the Car widget could contain Make [Text], Model [Text], Year [Integer], and Color [Dropdown]. This widget could then be used for questions like, "What car do you currently drive?" or "What car did you drive 3 years ago?".

### Repeated questions

A set of questions within a section may be marked as **repeated**.