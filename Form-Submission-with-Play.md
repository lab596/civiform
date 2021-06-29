# How to configure forms

## Types of forms

There are two types of forms in Play - [**defined forms**](https://www.playframework.com/documentation/2.8.x/JavaForms#Defining-a-form) and [**dynamic forms**](https://www.playframework.com/documentation/2.8.x/JavaForms#Handling-a-form-with-dynamic-fields). Defined forms have pre-defined fields and often map 1:1 to an object. All our defined forms are in the [/app/forms](https://github.com/seattle-uat/civiform/tree/main/universal-application-tool-0.0.1/app/forms) directory. Dynamic forms are those that cannot be defined in advance - for example, we cannot generalize a single form for every block of questions, since each block has a different set of questions. Therefore, we use a dynamic form for our blocks (see [ApplicantProgramBlocksController#update](https://github.com/seattle-uat/civiform/blob/main/universal-application-tool-0.0.1/app/controllers/applicant/ApplicantProgramBlocksController.java)).

## Process for adding a defined form

1. Define an HTML form - the field `name` attributes must match the Play form method names exactly
2. Add a [POJO](https://en.wikipedia.org/wiki/Plain_old_Java_object) under [/app/forms](https://github.com/seattle-uat/civiform/tree/main/universal-application-tool-0.0.1/app/forms) with getters and setters for each field in the form. **Note**: the getter and setter names must exactly match the HTML input name
3. Bind the form in the controller like so:

```java
Form<MyPojoForm> formWrapper = formFactory.form(MyPojoForm.class).bindFromRequest(request);

if (predicateFormWrapper.hasErrors()) {
  // Add any error handling here
}

MyPojoForm form = formWrapper.get();
```

## Repeated fields

If you have a form field that is repeated (for example, a checkbox), the `name` attribute must end in square brackets. Example:

```java
<input id="checkbox-applicant.kitchen-1" type="checkbox" name="applicant.kitchen.selections[]" value="1">
```

In the POJO form, use a mutable `List` for setters and getters ([example POJO with lists](https://github.com/seattle-uat/civiform/blob/main/universal-application-tool-0.0.1/app/forms/MultiOptionQuestionForm.java)).

## Play documentation
For more information, see [Java Forms with Play](https://www.playframework.com/documentation/2.8.x/JavaForms) or [Java Form Helpers](https://www.playframework.com/documentation/2.8.x/JavaFormHelpers)