# Adding a new supported language

The Play framework supports internationalization with only a few steps required. If you would like CiviForm to support a new language, please do the following:

1. Look up the language code for the new language. We use Java's [Locale](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Locale.html) class, which uses the IANA Language Subtag Registry. You can search the [list of valid language subtags](https://www.iana.org/assignments/language-subtag-registry/language-subtag-registry) to find the correct one (look for "Type: language").
1. Add the language tag to the end of the list in [application.conf](https://github.com/seattle-uat/civiform/blob/main/universal-application-tool-0.0.1/conf/application.conf) (under `play.i18n`). Make sure to add to the end of the list, since the first language code is considered the default for the application.
1. Add a new messages file under [/conf](https://github.com/seattle-uat/civiform/tree/main/universal-application-tool-0.0.1/conf) (see [messages.en-US](https://github.com/seattle-uat/civiform/blob/main/universal-application-tool-0.0.1/conf/messages.en-US) for an example). Note that the file extension must match the language tag exactly.
1. Update the [ApplicantInformationControllerTest](https://github.com/seattle-uat/civiform/blob/main/universal-application-tool-0.0.1/test/controllers/applicant/ApplicantInformationControllerTest.java) `edit_usesHumanReadableLanguagesInsteadOfIsoTags` to include a check for the new language.
1. Add translations to the new messages file for the language, and run the application to verify the new translations appear.

Need help? See [Play's i18n documentation](https://www.playframework.com/documentation/2.8.x/JavaI18N) for more guidance.