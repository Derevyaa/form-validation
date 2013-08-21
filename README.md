form-validation
=========================

Overview
--------
This is a validation library for form data defined by [jquery.formbuilder](https://github.com/shunjikonishi/jquery-formbuilder)

Optionally, it can make Salesforce object from JSON by Salesforce Metadata API.

Dependencies
------------
If you want to build this library, you must build and install following maven projects.

- [flectCommon](https://github.com/shunjikonishi/flectCommon)
- [flectSoap](https://github.com/shunjikonishi/flectSoap)
- [flectSalesforce](https://github.com/shunjikonishi/flectSalesforce)

If you don't use Salesfoce, flectSoap.jar and flectSalesforce.jar are not necessary in runtime environment.

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
