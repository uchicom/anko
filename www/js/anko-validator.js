// (C) 2022 uchicom
function getValidationMap(formId) {
	return JSON.parse(localStorage.getItem("validation"))[formId];
}

function replaceValidateTemplate(orgId, templateId) {
  replaceTemplate(orgId, templateId, attachValidattion);
}

const attachValidattion = (form, labelConsumer) => {
	var map = getValidationMap(form.getAttribute("id"));
	if (!map) {
		return;
	}
	var elements = form.querySelectorAll("input,select,textarea");
	for (var ei = 0; ei < elements.length; ei++) {
		var element = elements[ei];
		var validation = map[element.name];
		if (!validation) {
			continue;
		}
		if (validation['required']) {
			element.required = true;
			element.title = validation['required'].message;
			var label = form.querySelector("label[for=\"" + element.getAttribute("id") + "\"],label[for=\"" + element.getAttribute("name") + "\"]");
			if (label && labelConsumer) {
        labelConsumer(label);
			}
		}
		if (validation['pattern']) {
			element.pattern = validation['pattern'].regexp;
			element.title = validation['pattern'].message;
		}
		if (validation['length'] && validation['length'].max) {
			element.maxLength = validation['length'].max;
		}
	}
};
