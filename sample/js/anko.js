// (C) 2022 uchicom
function idElement(id) {
    return document.getElementById(id);
}
function create(tag) {
	return document.createElement(tag);
}
function text(string) {
	return document.createTextNode(string);
}
function removeAllChild(id) {
	var select = idElement(id);
	while (select.firstChild) {
		select.removeChild(select.firstChild);
	}
	return select;
}
function replaceChild(id, child) {
	removeAllChild(id).append(child);
}
function querySelector(selector) {
	return document.querySelector(selector);
}
function querySelectorAll(selector) {
	return document.querySelectorAll(selector);
}
function createClone(templateId, element)  {
	const template = element ? element.getElementById(templateId) : document.getElementById(templateId);
	return document.importNode(template.content, true);
}
function createRow(templateId, element) {
	return createClone(templateId + ".row", element);
}
function createInput(templateId, element) {
	return createClone(templateId + ".input", element);
}
function replaceTemplate(orgId, templateId, formConsumer) {
	const clone = createClone(templateId);
	const form = clone.querySelector("form");
	if (form && formConsumer) formConsumer(form);
	idElement(orgId).replaceWith(clone);
}
function set(selector, response) {
	var selects = querySelectorAll(selector + " input," + selector + " textarea," + selector + " select");
	for (var i = 0; i < selects.length; i++) {
		var select = selects[i];
		if (select.type == "radio" || select.type == "checkbox") {
			select.checked = response[select.id] == null ? false : response[select.id];
		} else {
			select.value = response[select.id] == null ? "" : response[select.id];
		}
	}
}
function postJson(url, assignHeader, payload, preProcess, process, errorProcess, postProcess) {
	requestJson("POST", url, assignHeader, payload, preProcess, process, errorProcess, postProcess);
}
function getJson(url, assignHeader, payload, preProcess, process, errorProcess, postProcess) {
	let queryUrl = payload ? url += "?" + new URLSearchParams(payload).toString() : url;
	requestJson("GET", queryUrl, assignHeader, null, preProcess, process, errorProcess, postProcess);
}
function requestJson(method, url, assignHeader, payload, preProcess, process, errorProcess, postProcess) {
    if (preProcess) preProcess();
    fetch(url, {
        method: method,
        headers:  Object.assign({'Content-Type': 'application/json; charset=utf-8'},
            assignHeader),
        body: payload ? JSON.stringify(payload) : null
    }).then(response=>{
        if (!response.ok) {
            throw new Error(response);
        }
        return response.json();
    })
    .then(process)
    .catch(errorProcess)
    .finally(postProcess);
}
