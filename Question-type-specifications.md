UAT Question Type Requirements

General comments
================

-   All question types have the following configuration options:

-   Title (text shown to the user)

-   Description or help text

-   This is raw text, but if we detect a URL, we can format it as a hyperlink.

-   Required or optional

-   Each question may have zero, one, or more validation criteria.

-   For simplicity, if there are two or more criteria, it's assumed that they're joined with "AND" (all criteria must be met for the answer to be accepted)

-   The list of supported validation criteria are given under each question type's heading.

-   Each validation criterion may be paired with an error message in case that criterion isn't met.

-   ![](https://lh3.googleusercontent.com/u_KYHHE7E5jVKhLVYs_z6AIG-4JwTmBHUAP8n5p1Yi6wk9MWdf92KoglR0YIPfhplkxu6K9gquuyr21pq_uOFYrUKMejWcu5XFESNot_Mge6dxCOOagG_-nPBFxj71ogwu1DMitahQ)

📜 Text
=======

This question type produces a simple text input widget.

![](https://lh6.googleusercontent.com/DfNdC0E9ePRFKPFuOy-WfQ8_20ZQ8tk68QEoXEqRkXg6Lo-rbI22rjqOMz9F4WD72xHesviZzo0wH1Cia78uoH1jYL1o7jKRTUuy0vLtZmD7YLsE-C9LF86J9IdVAQKgqkvEk1215w)

Configuration options
---------------------

-   Format: short answer (one line) or paragraph (multiple lines)

Supported validation criteria
-----------------------------

-   Min length (in characters)

-   Max length (in characters)

-   Includes some text

-   Doesn't include some text

-   Starts with some text

-   Ends with some text

-   Matches RegEx

💯 Number
=========

This question type produces a numerical input widget with an optional spinner.

![](https://lh3.googleusercontent.com/c4mc-_VfPt5K6jq3dtBrKcPPtoqrHwvIFIJNB5jg-qi-zWlEzHtjDUM2dxkzRRKPQLZxgF2BG6RR4MNJQs7IIp1_09opw42VkJkFjNYj-c3pEga74f0IQ0JHiqhxNgDOsrtFVM3T3g)

Configuration options
---------------------

-   Spinner or no spinner

-   The spinner is the pair of up/down arrows at the edge of the input field.

-   If a spinner is selected, the following options become available:

-   Spinner step size, or how much the inputted number goes up/down when the arrows are pressed (default 1)

-   Whether an integer is required, or if a floating-point number is OK

Supported validation criteria
-----------------------------

-   Minimum value

-   Maximum value

📆 Date
=======

This question type produces a simple date-picker widget. Time-picking is probably out of scope here.

![](https://lh6.googleusercontent.com/kyJ6iwPMlh1DjFwLcOT_H5tcHZe1dDcJ8CltnToqkcKXZtVgCtQgTx25OVZKpNysgQc_D_2mcWTatdZBd2SPTpXxjCQfeMggkK9fOPMRptX69TeTxgPyr9GRuxn29jgWGBAufEnY3w)

Configuration options
---------------------

-   None

Supported validation criteria
-----------------------------

-   Earliest allowed date

-   Latest allowed date

-   Allowed days of the week

-   Allowed months of the year

📻 Radio button set
===================

This question type creates a set of radio buttons, of which only one can be chosen.

![](https://lh5.googleusercontent.com/qh0fbFdhhcUUZ5jhUsmSSmnXfbTnQXrtB9SflTYS3HLbzIBHfyvJP19pCsWTwA8tziC0dsa7-fHiqTxKn978aLXZ3BM0ZOYq9CEZDLmH_fMsPOj-V47TbSYlh0iP3u0MzHXFGdWx0w)

We may want to recommend that users switch to dropdowns if their radio button set [has more than 7 choices](https://blog.prototypr.io/7-rules-of-using-radio-buttons-vs-drop-down-menus-fddf50d312d1).

Configuration options
---------------------

-   List of choices available

-   Whether or not an "other" field is shown

-   "Other" fields are always accompanied with short text input fields

-   Sorting of choices

-   As written

-   Alphabetical A-Z

-   Alphabetical Z-A

-   Random

Supported validation criteria
-----------------------------

-   None

💧 Dropdown
===========

This question type creates a dropdown with multiple choices available. Applicants may pick one or more choices.

![](https://lh3.googleusercontent.com/XOlc1JvdwbOEG_CIAlo7Xg__fkAtWiAgBrpgb7We4cd9yubSaFNIUDsbCQNx__WC_8WQcM-jD4h-Powec68dQXJsbERRWbb87DS5k2sFu79RrDlBSBrtu11cdb2N2VrL2sGUIxzyzw)

If we have time, we can automatically add search bars atop long dropdown lists to help filter.

![](https://lh6.googleusercontent.com/GTS4x3vj_ZYpUAUM9fZ5TZjQvI6cU0Aw2Fgh0Vp3KQzWJZgZk_LIvVxY1x8nnCb92PMk-_vqWs2bse9_CP_OGnYrGv3dFdw-AyoIOQfzmhz7K2MjoCxfNuGHxpkK_OYC8QlXYbEQCw)![](https://lh6.googleusercontent.com/MF5QqOm80e6DZyTpp9avThqktMBs1Z94FAyq05m056V1K8U098YdcBO6aLEvnj3197kXJ4UgP4mg0mKdaFuH4qgm2UYH8lS0N4n614G65yIjBvqlGeGt2SpkFvnRvthNm-TTTYA5IA)

Configuration options
---------------------

-   List of choices available

-   Default choice, if any

-   Sorting of choices

-   Alphabetical A-Z

-   Alphabetical Z-A

-   Random

-   (If time) Whether or not a search bar should be shown along the top

Supported validation criteria
-----------------------------

-   Minimum number of choices that can be picked

-   Maximum number of choices that can be picked

☑️ Checkbox set
===============

This question type creates a list of checkboxes. Applicants may pick one or more choices, depending on how the admin configured the question.

![](https://lh6.googleusercontent.com/9vyVEWw_1eOGRFUCHjmsL59PTDk89FC3Yv2xF8Df65D4Fh-IHV2p7fD0he633OIPqDQ2m0xvxHe2TIOYSq34iZ90dkm8NgQdD0N2w-xbSATFrWOxD8ntjIOi0dE9qWn-Wc52PKcC1g)

The checkbox set should be able to implement complex questions like this:

![](https://lh5.googleusercontent.com/ebLdkJKtWUwKEtHzBnWldpiugfQzFGzgU5OVxf8AzZfxxM7dBi7uFaL7gcnInaa7i5uHUtLtgwlT0OSiIGKiUQNsp5CQO3f6gCIPcpVpuhcDElhPCXct4oWKHnOTW1QL6XipINbC3g)

Configuration options
---------------------

-   Whether or not an "other" field should be shown at the bottom

-   For each field, whether applicants should be allowed to input free text next to the checkbox

-   If so, some help text to accompany the free-text input (e.g. Native American => "print name of enrolled tribe")

-   How the options should be sorted: exactly as inputted, alphabetically, reverse alphabetically, or random

-   The "other" field should always go at the bottom

Supported validation criteria
-----------------------------

-   Minimum number of choices that can be picked

-   Maximum number of choices that can be picked

👍 Yes/No toggle
================

💵 Currency
===========

📬 Email address
================

☎️ Phone number
===============

💳 ID / Account Number
======================

👋 Name
=======

This question type creates a multi-part widget with several text fields, each corresponding to a different part of the name.

![](https://lh6.googleusercontent.com/2sOa2BAiS662vK9q2JjgOAS4bQKGSF6Z8i3y6alV5f3qSVhhW9Wf-IbEz-wD77MMQxFz3cCJM1i331Dl2U2ySYwB4QsjC43OCEmVxCQR78_CwCyEDxy2UAQkne4gKvvhkTIZFXXf4g)

Sub-fields
----------

All the following should be text fields with a long maximum length (think 100 characters) to account for the wide variety of naming conventions across human cultures.

-   Title or honorifics, such as Dr., Rev., Ms., etc. (optional)

-   Leave this as an open text field, since [you can never predict the full list of titles that humankind can come up with](https://www.zuko.io/blog/titles-in-online-forms-how-inclusive-should-you-be)

-   Legal first or given name (required)

-   Middle name(s) (optional)

-   Some forms may only care about the middle initial, but that's easy enough to compute from the middle name, so for consistency we should always ask for the full middle name.

-   Last or family name(s) (required)

-   Suffix(es), such as Jr., Sr., III, etc. (optional)

-   Again, leave it as an open text field, since you can never predict the full list of available suffixes

Configuration options
---------------------

-   None

Supported validation criteria
-----------------------------

-   None

Custom functionality
--------------------

-   None

🏡 Address
==========

This question type creates another multi-part widget with several fields, each corresponding to a different component of a United States address.

![](https://lh5.googleusercontent.com/qxI8tgZdzNY3nO_gn5DCRmP5cDZ_Qaezol1qsTTPLb3Td7cM40QkcATMU1QJ4TWMTOVs2x0_GMm2hHSat4_PQm7eGgotIyZSH9GUBDWHfGSJhtwZ9V8KCCSa8Jiuv1FYqPVlol9XLQ)

Sub-fields
----------

-   Address line 1 (required)

-   This usually corresponds to a street address or P.O. Box number.

-   Implement as a text field.

-   Address line 2 (optional)

-   This usually corresponds to a unit or apartment number, 

-   It's considered good UX practice to [hide this field behind a button or link](https://baymard.com/blog/address-line-2): make the user hit a button to reveal this field. This stops people from instinctively typing their city into this field.

-   To [reduce confusion](https://baymard.com/blog/address-line-2), explicitly mark this as optional.

-   Implement as a text field.

-   City (required)

-   Implement as a text field.

-   State (required)

-   Implement as a dropdown.

-   Include:

-   All 50 states

-   The District of Columbia (DC)

-   The 6 outlying territories: American Samoa (AS), Guam (GU), Northern Mariana Islands (MP), Puerto Rico (PR), US Minor Outlying Islands (UM), and US Virgin Islands (VI)

-   Armed forces address codes: Armed Forces Americas (AA), Pacific (AP), and others (AE)

-   Offer both the full name and the postal abbreviation for easier skimming, such as "CA - California".

-   WA may want to be called "Washington State" to distinguish from Washington, DC.

-   ZIP (required)

-   Implement as a text input field that only supports digits, and with a required length of 5.

-   Avoid implementing as a plain old number field, since some ZIP codes start with a "0" (e.g. 02110 for Boston).

-   No address (optional, if enabled by admin in Configuration Options)

-   Implement as a checkbox.

-   If this is checked, the rest of the fields are grayed out automatically, and if another field is filled, this checkbox is unchecked automatically.

-   This is helpful if we want to let people say they're experiencing homelessness and thus have no home address, without directly asking about homelessness status.

Configuration options
---------------------

-   Default state in dropdown

-   Whether or not the "no address" checkbox is available

-   That checkbox is mostly useful for questions about the applicant's home address, since it lets them specify if they're experiencing homelessness.

Supported validation criteria
-----------------------------

-   Allow or disallow P.O. Boxes when entered into Address 1

Custom functionality
--------------------

-   As the user types their street address into Address Line 1, use the [Google Maps autocompletion API](https://developers.google.com/places/web-service/autocomplete) (or similar) to suggest full addresses in a dropdown. If the user selects one, fill the entire widget with data from the API.

-   Potentially auto-validate addresses using the [USPS's Address Verification API](https://www.usps.com/business/web-tools-apis/) (same one that Amazon uses when you enter a new address).

-   Auto-rejects ZIP codes that don't match the state.

📤 File upload
==============

This generates a widget that lets applicants upload files from their devices or from camera rolls.

![](https://lh6.googleusercontent.com/Q74Zw_uLskic2fZa0Rmkry6ioJX0v_ODBMsTByrhJoi-gfciIw3OapRSizyzn3WJpRxvDObqFTAESIxrDCanevSlnczF6YehUilnHPXAS1l05K_Yfgh87c2zJKSzHWhji2P5Tez3VA)

Upload from cloud storage services will be considered out of scope for v1.

Configuration options
---------------------

-   Supported file types

-   Admins can choose from GIF, JPG, PNG, and/or PDF.

-   Minimum number of files applicants need to upload

-   Maximum number of files applicants need to upload

Supported validation criteria
-----------------------------

-   Maximum filesize of all documents put together

-   Ideally, we'd make this infinitely large so no applicant has to puzzle out how to shrink their files, but there has to be some limit.

-   Maximum filesize of any individual document uploaded

Custom functionality
--------------------

-   Applicants can drag and drop files or upload them using the native file browser or camera

-   Applicants see previews of their already-uploaded files

-   Next to each preview is a delete button that lets applicants remove uploaded files

-   Progress bars show percent completion of uploads to UAT servers

-   Auto-shrink uploaded files if they're larger than the maximum allowed filesize

-   For images, this can be as simple as scaling down the dimensions. 

-   PDFs are harder, but [compression APIs](https://cloudconvert.com/api/v1/compress/pdf-to-pdf) may be available.

-   Letting applicants interactively crop their files is out of scope for v1.

🔢 Multi-text
=============

This question type will be used before each repeated block. It asks applicants to enter nicknames of an arbitrary number of items (children, jobs, cars, etc.), after which they'll have to enter details for each one.

![](https://lh5.googleusercontent.com/W-qVrFTQh8STpgAylIrc3989jidf48d7aZJDnsQ6Jlhsns5YguUXdVDPDYADUe27DeI-A6fL9cW95XhUbEGC5UGOxduYT_mQxBGr_VmI-_q0Pi2wBduy9413Wq3WQT5MTd6g7RxkpQ)

In the future it'd be great to import the text typed into these fields into questions in the repeated blocks -- for instance, applicants type their children's first names into the multi-text question, and those first names auto-fill into the Name questions when it comes time to fill in details about each child. But that adds a lot of complexity to the UAT admin experience (they have to drill deep into the data model to show what field the text inputs correlate with), so it's better to leave off for v1. (Also, there are edge cases where two household members have the same name, so auto-filling information might get messy.)

Sub-fields
----------

-   Nickname fields (required)

-   Implement each as a required text input field.

-   Applicants can add/delete fields. There can never be less than 1 field; if the applicant has 0 items to report, they can just hit "skip" instead.

Configuration options
---------------------

-   Placeholder text in the nickname field

-   In the mockup above, this would be "Full name."

-   Name of the type of item

-   In the mockup above, this would be "Member."

-   This text is shown after the word "Add" on the plus button.

Supported validation criteria
-----------------------------

-   Minimum number of items the applicant can choose to input

-   Defaults to 1.

-   Maximum number of items the applicant can choose to input

-   Defaults to infinite.

-   Maximum length of each nickname field

-   Defaults to infinite.

Custom functionality
--------------------

-   There is a plus button that applicants can use to add items, up to the maximum allowed number.

-   Next to each nickname field is a delete button that removes an item.

-   The delete buttons disappear if the applicant has hit the minimum number of allowed items, and reappear once they're above the minimum.

-   Next to each nickname field is a drag-and-drop handle

-   These let applicants move items up and down in the list.

-   There are [many possible UX affordances for these](https://uxdesign.cc/drag-and-drop-for-design-systems-8d40502eb26d).

Prebuilt questions
==================

Some common questions will be complicated and intricate, and we'll want to ensure that they're standardized across the UAT. For that reason, we'll want to "pre-build" those questions and pre-populate them into the question bank. This way, we'll avoid each form having its own slightly different take on the same question.

Race and ethnicity
------------------

One common but complex question is race and ethnicity. It can be implemented as a pair of [checkbox sets](https://docs.google.com/document/d/1HYVHBno6dk7fmS5w6V6Y3jxERHGfDoQKTtzBZr27Y6o/edit#heading=h.tqbgqf8imqbx), one for race and one for Hispanic/Latinx origin. It would likely be built as two separate items in the question bank, but the UAT admin would almost always use them in tandem.

Here's a good example from the [US Census Bureau](https://www.pewresearch.org/fact-tank/2014/03/14/u-s-census-looking-at-big-changes-in-how-it-asks-about-race-and-ethnicity/):

 ![](https://lh5.googleusercontent.com/i6ILA1aWp2pNNQ34EnH7mpgMmo1nNethn51SDh0cJOWZLVm8irPF1mlXqBU4MejBGkNMWcI4iQUpgnobzIgXZDvLD4kTGOTo79ZwtpQ0nAVc37ILretbBWgdfzuIeF3YiO-VGJ2Nbw)

See [Anna's additional research](https://docs.google.com/document/d/1RaL-lQWY7VCx1HdsEnoGqdLajA-msg_8IVkZqVBUYD8/edit?ts=603d5afc&resourcekey=0-nrmClT8G20a5yn4ySbWfrw#heading=h.lc4smjna8jkm).

Sex and gender identity
-----------------------

These topics would form two prebuilt questions in the question bank. UAT admins may use them separately or in tandem.

More thorough user research is merited, but this [patient registration form](https://www.researchgate.net/figure/Collection-of-Gender-Identity-on-a-Patient-Registration-Form-Adapted-from-the_fig1_275893198) may offer a good starting point:

![](https://lh4.googleusercontent.com/OuwYA8h8x1gn1Op_ONWpAeq2h5rHcEFyvW8Iov3nqUZ0Z980IEtT01JKyVte2Az5VeRpLgwKLIMna_VNzPYsB_Aotq_9ADML5cmsbekOPWEEMu4Wtoj-kG5TwIm_17yXjqjbiMv2ag)

See [Anna's additional research](https://docs.google.com/document/d/1RaL-lQWY7VCx1HdsEnoGqdLajA-msg_8IVkZqVBUYD8/edit?ts=603d5afc&resourcekey=0-nrmClT8G20a5yn4ySbWfrw#heading=h.slrq452jsgec).

Language
--------

This one's just a dropdown, but we should pre-populate the list of languages (including the localized versions) so that admins don't have to build it themselves.

It's relatively straightforward, but [remember to not use flags](http://www.flagsarenotlanguages.com/blog/language-menus-flags-symbolize-nations-not-languages/):

![](https://lh6.googleusercontent.com/4SoiIY9WbrtO1S9sGgq8eRjVmV-lMhbDMrOPmMKaaYVqce3fzcenbUK-L729SHIenIzVxMCDCYWySnU4zVwTXyVfUdUza-ie9udIFn2Tl6W1aW2kWutdLlbggKQ26vwCyWDXoI6sOA)