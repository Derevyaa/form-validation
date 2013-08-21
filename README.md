form-validation
=========================

Overview
--------
This is a validation library for form data defined by [jquery.formbuilder](https://github.com/shunjikonishi/jquery-formbuilder)

Dependencies
------------
If you want to build this library, you must build and install following maven projects.

- [flectCommon](https://github.com/shunjikonishi/flectCommon)

Usage
-----
    String formDefinedJson;
    Map<String, String[]> formData;
    
    FormDefinition form = FormDefinition.fromJson(formDefinedJson);
    ValidationResult result = form.validate(formData);//Or you can use FormDefition#validate(String) method
                                                      // to validate form data serialized by JSON.
    if (result.hasErrors()) {
        List<String> errorMessages = result.getAllErrors();
        //ToDo handling error
    }

License
-------
MIT
