const formatter = new Intl.NumberFormat('ja-JP', {
	style: 'currency',
	currency: 'JPY'
});

function getEnumeration(enumeration) {
	return JSON.parse(localStorage.getItem("enumeration"))[enumeration];
}

function getValidationMap(formId) {
	return JSON.parse(localStorage.getItem("validation"))[formId];
}
function setTransition(key, value) {
	let transition = JSON.parse(sessionStorage.getItem("transition"));
	if (transition == null) transition = {};
	transition[key] = value;
	sessionStorage.setItem("transition", JSON.stringify(transition));
}
function getTransition(key) {
	let transition = JSON.parse(sessionStorage.getItem("transition"));
	if (transition == null) return null;
	return transition[key];
}
function clearTransition(key) {
	let transition = JSON.parse(sessionStorage.getItem("transition"));
	if (transition == null) return;
	delete transition[key];
	sessionStorage.setItem("transition", JSON.stringify(transition));
}
function popTransition(key) {
	let transition = JSON.parse(sessionStorage.getItem("transition"));
	if (transition == null) return;
	const value = transition[key];
	delete transition[key];
	sessionStorage.setItem("transition", JSON.stringify(transition));
	return value;
}
function infoNotification(message) {
	notification(message, 'primary');
}
function successNotification(message) {
	notification(message, 'success');
}
function warningNotification(message) {
	notification(message, 'warning');
}
function clearNotification() {
	var elements = querySelectorAll("dialog");
	for (var index = 0; index < elements.length; index++) {
		elements[index].close();
		elements[index].remove();
	}
}
function errorMessage(message) {
  idElement("content").prepend(createAlertDiv(true, message));
}
function notification(message, className) {
	const dialog = create("dialog");
	dialog.classList.add(className);
	dialog.open = true;
	dialog.style = "border:unset";
	const p = create("p");
	p.style = "margin:10px";
	p.append(message);
	var messageA = create("a");
	messageA.append("×");
	messageA.classList.add("anko-close");
	messageA.onclick = () => dialog.close();
	p.append(messageA);
	dialog.append(p);
	querySelector("body").prepend(dialog);
	setInterval(() => {
		if (dialog.open) {
			dialog.close();
			dialog.remove();
		}
	}, 5000);
}
function replaceClone(id) {
	var element = idElement(id);
	var clone = element.cloneNode(false);
	element.parentNode.replaceChild(clone , element);
	return clone;
}
function removeAll(className) {
	var elems = classNameElements(className);
	for (var index = 0; index < elems.length; index++) {
		elems[index].classList.remove(className);
	}
}
function removeAllMessageDiv() {
	var elems = classNameElements("anko-danger");
	while (elems.length > 0) {
		elems[0].parentNode.removeChild(elems[0]);
	}
}
function removeQuerySelector(selector) {
	var element = querySelector(selector);
	element.parentNode.removeChild(element);
}
function classNameElements(className) {
	return document.getElementsByClassName(className);
}
function createAlertDiv(closeDiv, message) {
	var messageDiv = create("div");
	messageDiv.classList.add("anko-danger");
	var messageP = create("p");
	messageP.style = "margin-left:20px";
	messageP.append(text(message));
	messageDiv.append(messageP);
	if (closeDiv) {
		var messageA = create("a");
		messageA.append("×");
		messageA.classList.add("anko-close");
		messageA.onclick = () => messageDiv.remove();
		messageP.append(messageA);
	}
	return messageDiv;

}
function errorMessages(formId, violations) {
	if (formId) {
		var selector = "#" + formId;
		var selects = document.querySelectorAll(`${selector} input, ${selector} textarea, ${selector} select`);
		for (var i = 0; i < selects.length; selects) {
			var select = selects[i++];
			if (select.disabled) {
				continue;
			}
		}
		
		for (const key in violations) {
			var violation = violations[key];
			var elems =  document.querySelectorAll(`${selector} input[name="${violation.propertyPath}"], ${selector} textarea[name="${violation.propertyPath}"], ${selector} select[name="${violation.propertyPath}"]`);
			var elem = elems[violation.index];
			if (elem) {
				if (elem.parentNode) {
					elem.parentNode.append(createAlertDiv(false, violation.message));
				}
			}
		}
	} else {
		for (var key in violations) {
			var violation = violations[key];
			var elems =  document.querySelectorAll(`${selector} input[name="${violation.propertyPath}"], ${selector} textarea[name="${violation.propertyPath}"], ${selector} select[name="${violation.propertyPath}"]`);
			var elem = elems[violation.index];
			elem.parentNode.append(createAlertDiv(false, violation.message));
		}
	}
}
function authMessage(message) {
	removeToken();
	warningNotification(message);
	dispLogin();
}
function option(value, text) {
	var option = create("option");
	option.setAttribute("value", value);
	option.append(text ? text : value);
	return option;
}
function appendOption(id, list, createOption) {
	var select = idElement(id);
	appendSelectOption(select, list, createOption);
	return select;
}
function appendSelectOption(select, list, createOption) {
	for (var key in list) {
		const option = createOption(list[key]);
		if (!option) continue;
		select.append(option);
	}
}

function appendKeyOption(id, list, createOption) {
	var select = idElement(id);
	appendKeySelectOption(select, list, createOption);
	return select;
}
function appendKeySelectOption(select, list, createOption) {
	for (var key in list) {
		const option = createOption(key, list[key]);
		if (!option) continue;
		select.append(option);
	}
}
function enabled(element) {
	element.removeAttribute("disabled");
}
function disabledForElement(element) {
	element.setAttribute("disabled", true);
}
function enabledById(id) {
	enabled(idElement(id))
}
function disabledById(id) {
	disabledForElement(idElement(id));
}
function enabledByQuerySelector(selector) {
	enabled(querySelector(selector))
}
function disabledByQuerySelector(selector) {
	disabledForElement(querySelector(selector))
}
function enabledByQuerySelectorAll(selector) {
	const elements = querySelectorAll(selector);
	for (let i = 0; i < elements.length; i++) {
		enabled(elements[i]);
	}
}
function disabledByQuerySelectorAll(selector) {
	const elements = querySelectorAll(selector);
	for (let i = 0; i < elements.length; i++) {
		disabledForElement(elements[i]);
	}
}
function text(string) {
	return document.createTextNode(string);
}
function selected(option) {
	option.setAttribute("selected", true);
}
// ポストJSON
function postJsonUrl(url, selector, func, errorFunc, confirmMessage, isArray, arrayKey) {
	var records = [];
	var data = {};
	records.push(data);
	if (arrayKey != null) {
		data[arrayKey] = [];
	}
	if (selector) {
		var validate = querySelector(selector);
		if (validate && validate.tagName == 'FORM' && !validate.checkValidity()) {
			if (errorFunc) {
				errorFunc();
			}
			console.warn("入力チェックエラー");
			return;
		}
		var selects = querySelectorAll(selector + " input," + selector + " textarea," + selector + " select");
		for (var i = 0; i < selects.length; i++) {
			var select = selects[i];
			if (select.disabled) {
				continue;
			}
			if (arrayKey == null) {
				if (data[select.name] != null) {
					data = {};
					records.push(data);
				}
				if (select.type == 'checkbox') {
					data[select.name] = select.checked;
				} else if(select.value != "") {
					data[select.name] = select.value;
				}
			} else {
				if (select.name == arrayKey) {
					if (select.type == 'checkbox') {
						data[arrayKey].push(select.prop('checked'));
					} else if(select.value != "") {
						data[arrayKey].push(select.value);
					}
				} else {
					if (select.type == 'checkbox') {
						data[select.name] = select.checked;
					} else if(select.value != "") {
						data[select.name] = select.value;
					}
				}
			}
		}
	}
	if (isArray) {
		postJsonDataUrl(url, records, func, errorFunc, confirmMessage);
	} else {
		postJsonDataUrl(url, data, func, errorFunc, confirmMessage);
	}
}
const loadingKey = [];
function loading(on, key) {
	var load = idElement("loading");
	if (on) {
		loadingKey.push(key);
		if(!load){
			var div = create("div");
			div.id = "loading";
			div.classList.add("waitScreen");
			var div2 = create("div");
			div2.classList.add("waitIcon");
			div.append(div2);
			document.body.append(div);
		}
	} else {
		var index = loadingKey.indexOf(key);
		loadingKey.splice(index, 1);
		if (loadingKey.length == 0 && load) {
			load.parentNode.removeChild(load);
		}
	}
}
function postJsonData(url, data, func, errorFunc, confirmMessage) {
	postJsonDataUrl(url, data, func, errorFunc, confirmMessage);
}
// ポストJSON
function postJsonDataUrl(url, data, func, errorFunc, confirmMessage) {
  var key = {};
	postJson(url,
     {'token': getToken()}, 
     data,
     () => {
      if (confirmMessage) {
        let res = confirm(confirmMessage);
				if (!res) {
					return false;
				}
      }
			removeAllMessageDiv();
      removeAll("anko-danger");
			loading(true, key);
			return true;
     },
    data=>{
      //メッセージ
      if (data.message) {
        successNotification(data.message);
      }
      //認証メッセージ
      if(data.authMessage) {
        authMessage(data.authMessage);
      }
      //エラーメッセージ
      else if (data.violationList) {
        errorMessages(data.form, data.violationList);
        if (errorFunc) {
          errorFunc(data);
        }
      } else if (data.errorMessage) {
        errorMessage(data.errorMessage);
        if (errorFunc) {
          errorFunc(data);
        }
      } else {
        if (func) {
          func(data);
        }
      }
    },
    err => {  
		console.log(err);
		if (err.response) {
			errorMessage(`システムエラー(ステータス:${err.response.status})が発生しました。時間を置いて再度実施してください。それでも解決しない場合は管理者に連絡してください。`);
		} else {
			errorMessage(`システムエラーが発生しました。時間を置いて再度実施してください。それでも解決しない場合は管理者に連絡してください。`);
		}
		if (errorFunc) {
        errorFunc(data);
			}
    },
    () => loading(false, key));

}
// ダウンロード
function downloadUrl(url, data, fileName, input) {

	disabledForElement(input);
	fetch(url, {
		method: "POST",
		headers: {
			'Content-Type': 'application/json; charset=utf-8',
			'token': getToken(),
		},
		body: JSON.stringify(data)
	}).then(response=> {
		if (!response.ok) {
      throw new ResponseError(response);
		}
		return response.blob();
	}).then(blob => {
		let a = document.createElement("a");
		document.body.appendChild(a);
		a.href = window.URL.createObjectURL(blob);
		a.download = fileName;
		a.click();
		document.body.removeChild(a);
		enabled(input);
	}).catch(err => {
		console.log(err);
		if (err.response) {
			errorMessage(`システムエラー(ステータス:${err.response.status})が発生しました。時間を置いて再度実施してください。解決しない場合は管理者に連絡してください。`);
		} else {
			errorMessage(`システムエラーが発生しました。時間を置いて再度実施してください。解決しない場合は管理者に連絡してください。`);
		}
	});
}


// 確認メッセージを表示してからpostする
function confirmPost(message, action, key, func, funcPrevious, isArray) {
	// 事前処理があれば実施する
	if (funcPrevious) {
		funcPrevious();
	}
	post(action, key, func, null, message, isArray);
}
const labelConsumer = label => {
  if (label && !label.querySelector("span[class=\"required\"]")) {
    label.appendChild(createRequirTag());
  }
};
function createRequirTag() {
	var span = document.createElement("span");
	span.setAttribute("class", "required");
	span.appendChild(document.createTextNode("必須"));
	return span;
}
function articleTemplate(templateId) {
	replaceTemplate('article', templateId, attachValidattion, labelConsumer);
}
function contentTemplate(templateId, formFunction) {
	replaceTemplate('content', templateId, formFunction ? formFunction : attachValidattion, labelConsumer);
}
function getToken() {
	return sessionStorage.getItem('token');
}
function saveToken(token) {
	sessionStorage.setItem('token', token);
}
function removeToken() {
	sessionStorage.removeItem('token')
}
function blank(data) {
	return data == null ? '' : data;
}
function blankParen(data) {
	return data ? '(' + data + ')' : '';
}
function isString(obj) {
    return typeof (obj) == "string" || obj instanceof String;
}
function find(list, filter) {
	for (key in list) {
		if (filter(list[key])) {
			return list[key];
		}
	}
	return null;
}

function formatYearMonth(date) {
	return date.getFullYear() + '-' 
	+ ('0' + (date.getMonth() + 1)).slice(-2);
}
function formatDate(date) {
	if (date.getTime() == 0) {
		return "";
 	}
	return formatYearMonth(date) + '-'
	+ ('0' + date.getDate()).slice(-2);
}
function formatTime(date) {
	if (date.getTime() == 0) {
		return "";
 	}
	return [
        date.getHours() < 10 ? '0' + date.getHours():date.getHours(),
        date.getMinutes() < 10 ? '0' + date.getMinutes():date.getMinutes(),
        date.getSeconds() < 10 ? '0' + date.getSeconds():date.getSeconds()
        ].join( ':' );
}
function formatDateTime(date) {
	return [
        formatDate(date),
        formatTime(date)
        ].join( ' ' );
}

function addDays(day) {
	return new Date(new Date().getTime() + day * 24 * 3600 *1000);
}

function textNewLine(string, limit) {
	let split = string.split(/\s/g);
	let span = create("span");
	for (var i = 0; i < split.length; i++) {
		span.append(text(split[i]));
		if (i == split.length - 1) continue;
		if (i + 1 < limit) {
			span.append(create("br"));
		} else {
			span.append(text(" "));
		}
	}
	return span;
}
function formatDateHMT(date) {
	return [
        formatDate(date),
        formatHM(date)
        ].join( 'T' );
}

function formatHM(date) {
	if (date.getTime() == 0) {
		return "";
 	}
	return [
        date.getHours() < 10 ? '0' + date.getHours():date.getHours(),
        date.getMinutes() < 10 ? '0' + date.getMinutes():date.getMinutes()
        ].join( ':' );
}
/** 質問リストレコード削除 */
function removeRow(button) {
	var row = button.parentNode.parentNode;
	row.parentNode.removeChild(row);
}
function listLength(list) {
	return list ? list.length : 0;
}
function initList(list) {
	let length = listLength(list);
	replaceChild("number", `検索結果:${length}件`);
	return length;
}
function initEditableList(list) {
	let length = initList(list);
	if(length == 0) {
		idElement("edit").disabled = true;
		return false;
	}
	idElement("edit").disabled = false;
	return true;
}
function getOptionText(select, value) {
	for (let i = 0; i < select.options.length; i++) {
		const option = select.options[i];;
		if (option.value == value) {
			return option.text;
		}
	}
	return null;
}

// 今日の日付取得
function getToday() {
	var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
  
    var toTwoDigits = function (num, digit) {
      num += '';
      if (num.length < digit) {
        num = '0' + num;
      }
      return num;
    };
    
    var yyyy = toTwoDigits(year, 4);
    var mm = toTwoDigits(month, 2);
    var dd = toTwoDigits(day, 2);
    var ymd = yyyy + "-" + mm + "-" + dd;
    return ymd;
}