# How to configure forms

## Types of forms

There are two types of forms in Play - [**defined forms**](https://www.playframework.com/documentation/2.8.x/JavaForms#Defining-a-form) and [**dynamic forms**](https://www.playframework.com/documentation/2.8.x/JavaForms#Handling-a-form-with-dynamic-fields). Defined forms have pre-defined fields and often map 1:1 to an object. All our defined forms are in the [/app/forms](https://github.com/seattle-uat/civiform/tree/main/universal-application-tool-0.0.1/app/forms) directory. Dynamic forms are those that cannot be defined in advance - for example, we cannot generalize a single form for every block of questions, since each block has a different set of questions. Therefore, we use a dynamic form for our blocks (see [ApplicantProgramBlocksController#update](https://github.com/seattle-uat/civiform/blob/main/universal-application-tool-0.0.1/app/controllers/applicant/ApplicantProgramBlocksController.java)).

## Play documentation
For more information, see [Java Forms with Play](https://www.playframework.com/documentation/2.8.x/JavaForms) or [Java Form Helpers](https://www.playframework.com/documentation/2.8.x/JavaFormHelpers)