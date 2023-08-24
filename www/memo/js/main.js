var planList;
var publicKey;
var router;

// 戻る動作
window.addEventListener('popstate', function(e) {
	var route = router[location.pathname];
	if (route) {
		route();
	}
});

// 画面表示初期設定
function init() {
	router = {
		"/memo/user/mypage": dispMyPage,
		"/memo/user/list": dispMemo,
		"/memo/user/register": dispMemoRegister,
	};
}
// 画面表示
var display = pathname => {
	UIkit.offcanvas('#offcanvas-slide').hide();
	var route = router[pathname];
	if (route) {
		clearErrorMessage();
		route();
		history.pushState(null, null, pathname);
		return true;
	}
	return false;
}


//ポスト
function post(path, selector, func, errorFunc, confirmMessage, isArray) {
	if (!selector || isString(selector)) {
		postJsonUrl('/memo/api' + path, selector, func, errorFunc, confirmMessage, isArray);
	} else {
		postJsonData('/memo/api' + path, selector, func, errorFunc, confirmMessage, isArray);
	}
}

//ログイン
dispLogin = () => {
	// 認証済み
	if (getToken()) {
		dispTop();
		return;
	}
	// 未認証
	if (location.pathname == "/memo/user/signup") { //サインアップ
		dispAccountRegister();
	} else {
		articleTemplate('loginTemplate');
	}
}

//トップ
function dispTop() {
	init();
	articleTemplate('topTemplate');
	var pathname = location.pathname;
	
	if (!display(pathname)) {
		display("/memo/user/mypage");
	}
}

// ログイン画面
function dispLoginView() {
	history.pushState(null, null, '/memo/user/');
	dispLogin();
}

//ログイン
function login() {
	post( '/account/login',  '#content', data => {
		if (data.token) {
			saveToken(data.token);
			dispTop();
		}
	});
}

function reflectToken(data, url) {
	if (data.token) {
		saveToken(data.token);
	}
	display(url);
}

//ログアウト
function logout() {
	if (confirm('ログアウトしますか？')) {
		logoutProcess();
	}
}
function logoutProcess() {
	removeToken();
	UIkit.nav('#offcanvas-slide').$destroy();
	dispLoginView();
}

//Eメールアドレス登録
dispAccountRegister = () => {
	articleTemplate('accountRegisterTemplate');
}

function dispMyPage() {
	contentTemplate('mypageTemplate');
}

// メモ一覧
function dispMemo() {
	contentTemplate('memoTemplate');
	post('/memo/list', null, data => {
		if(listLength(data.list) == 0) return;
		var tbody = document.createElement("tbody");
		for (var key in data.list) {
			var record = data.list[key];
			var row = createRow('organizationUserTemplate');
			var cols = row.querySelectorAll("td");
			cols[0].append(record.emailAddress);
			cols[1].append(record.name);
			cols[2].append(record.registerDateTime);
			tbody.append(row);
		}
		querySelector("#content .uk-table tbody").replaceWith(tbody);
	});
}

// メンバー変更・削除
function dispMemoRegister() {
	contentTemplate('organizationUserSaveTemplate');
	post('/organization/user/list', null, data => {
		if(listLength(data.list) == 0) return;
		var tbody = document.createElement("tbody");
		for (var key in data.list) {
			var record = data.list[key];
			var row = createRow('organizationUserSaveTemplate');
			var cols = row.querySelectorAll("td");
			var accountId = cols[0].querySelector("input");
			accountId.value = record.accountId;
			cols[0].append(record.emailAddress);
			cols[1].append(record.name);
			cols[2].append(record.registerDateTime);
			tbody.append(row);
		}
		querySelector("#content .uk-table tbody").replaceWith(tbody);
	});
}

// 契約一覧表示
function dispContract() {
	contentTemplate('contractTemplate');
	gmemoontractList();
}

// 契約一覧取得
function gmemoontractList() {
	post('/contract/list', "#content", data => {
		initList(data.contractList);
		let content = idElement("content");
		let tables = content.querySelectorAll("table");
		for (let i = 0; i < tables.length; i++) {
			content.removeChild(tables[i]);
		}
		if(listLength(data.contractList) == 0) {
			return;
		}
		const contractKind = getEnumeration("ContractKind");
		for (let key in data.contractList) {
			var contract = data.contractList[key];
			let table = createClone('contractTemplate.contract'); 
			let tds = table.querySelectorAll("td");
			tds[0].append(contractKind[contract.contractKind]);
			tds[1].append(contract.activationDatetime);
			if (contract.cancelDatetime != null) {
				tds[2].firstChild.replaceWith(text(contract.cancelDatetime));
			} else {
				tds[2].querySelector('#contractId').value = contract.contractId;
			}
			tds[3].append(contract.paymentKind == "CREDIT" ? "クレジットカード" : "銀行振込");
			const contractPlan = table.querySelector("#contractPlan");
			const contractSpan = table.querySelector("#contractSpan");
			if (contract.cancelDatetime != null) {
				tds[4].removeChild(tds[4].firstChild);
			} else {
				appendKeySelectOption(contractPlan, getEnumeration("ContractPlan"), (key,value)=>option(key, value));
				appendKeySelectOption(contractSpan, getEnumeration("ContractSpan"), (key,value)=>option(key, value));
			}
			const today = getToday();
			let tbody = table.querySelector("tbody");
			for (let key in contract.contractPeriodList) {
				let contractPeriod = contract.contractPeriodList[key];
				let period = createClone('contractTemplate.period');
				let periodTds = period.querySelectorAll("td");
				periodTds[0].append(getEnumeration("ContractPlan")[contractPeriod.plan]);
				periodTds[1].append(contractPeriod.year ? "年":"月");
				periodTds[2].append(contractPeriod.fromDate);
				periodTds[3].append(contractPeriod.toDate);
				if (today < contractPeriod.fromDate || today > contractPeriod.toDate) {
					period.querySelector("tr").classList.add("notToday");
				}
				periodTds[4].append(contractPeriod.price.toLocaleString() + (contractPeriod.year ? '円/年': '円/月') + '(税込)');
				if (contractPeriod.invoiceFileName) {
					const button = periodTds[5].querySelector("button");
					button.setAttribute("onClick",'download(\'/contract/invoice/download\', {invoiceId:' + contractPeriod.invoiceId + '}, \'' + contractPeriod.invoiceFileName +  '\', this)');
				} else {
					periodTds[5].removeChild(periodTds[5].firstChild);
				}
				if (contractPeriod.receiptFileName) {
					const button = periodTds[6].querySelector("button");
					button.setAttribute("onClick",'download(\'/contract/receipt/download\', {receiptId:' + contractPeriod.receiptId + '}, \'' + contractPeriod.receiptFileName +  '\', this)');
				} else {
					periodTds[6].removeChild(periodTds[6].firstChild);
				}
				if (key == 0 && contract.cancelDatetime == null) {
					contractPlan.value = contractPeriod.plan;
					contractSpan.value = contractPeriod.span;
				}
				tbody.append(period);

			}
			idElement("content").append(table);
			if (data.accountNumber) {
				let trs = createClone("contractTemplate.account");
				let tds = trs.querySelectorAll("td");
				tds[1].append(data.branchName + '(' + data.branchNumber + ')');
				tds[2].append(data.accountNumber);
				idElement("content").append(trs);
			}
		}
		
		
	});
}
//プラン申し込み画面
function dispPlanList() {
	contentTemplate('planTemplate');
  // 契約プラン取得
	post('/contract/plan/list', null, data => {
		if (!data.planList) return;
		planList = data.planList;
		publicKey = data.publicKey;
		var tbody = document.createElement("tbody");
		for (var i = 0; i < data.planList.length; i++) {
			var plan = data.planList[i];
			var row = createRow('planTemplate'); // TODO contentTemplate でtemplateオブジェクトを返してその中から取得できるようにしたい。
			var tds = row.querySelectorAll("td");
			if (i % 2 == 0) {
				tds[0].setAttribute("rowspan", 2);
				tds[0].append(getEnumeration("ContractPlan")[plan.plan]);
			} else {
				tds[0].parentElement.removeChild(tds[0]);
			}
			tds[1].append(getEnumeration("ContractSpan")[plan.span]);
			tds[2].append(formatter.format(plan.price), '/' , (plan.year ? '年' : '月' ));
			if (data.contractSpan != null) {
				// button 削除
				tds[3].removeChild(tds[3].firstChild);
				tds[4].removeChild(tds[4].firstChild);
				if (data.contractPlan == plan.plan && data.contractSpan == plan.span) {
					tds[3].append("申込済み");
				}
			} else {
				var buttons = row.querySelectorAll("button");
				buttons[0].setAttribute("onClick", "selectPlanCredit(planList[" + i+ "], " + data.hasDefaultCard + ")");
				buttons[1].setAttribute("onClick", "selectPlanBank(planList[" + i + "])");
			}
			tbody.append(row);
			querySelector("#content .uk-table tbody").replaceWith(tbody);
		}
	});
}

//プラン選択
function selectPlanCredit(plan, hasDefaultCard) {
	contentTemplate('applyTemplate');
	
	const title = querySelector("#content h3");
	title.append("クレジットカード決済");
	const tds = querySelectorAll("td");
	tds[0].append(plan.name);
	tds[1].append(formatter.format(plan.price), '/', (plan.year ? '年' : '月' ));
	idElement("paymentKind").value = "CREDIT"
	idElement("contractPlan").value = plan.plan;
	idElement("contractSpan").value = plan.span;
	if (hasDefaultCard) {
		idElement("hasDefaultCard").value = true;
		const button = querySelector("button");
		button.setAttribute("onclick", "payment(this)");
	} else {
		const form = idElement("contractRegister");
		removeQuerySelector("button");
		form.append(createClone("applyTemplate.card"));
		appendPayjpCardForm('#payjpCard');
	}

}

var payjp;
var cardElement;
function appendPayjpCardForm(mountId) {
	// 公開鍵を登録し、起点となるオブジェクトを取得します
	if (payjp == null) {
		payjp = Payjp(publicKey);
	}
	// element(入力フォームの単位)を生成します
	cardElement = payjp.elements().create('card');
	// elementをDOM上に配置します
	cardElement.mount(mountId);
}

function createToken(button) {
	disabledForElement(button);
	payjp.createToken(cardElement).then(response => {
		if (response.error) {
			alert(response.error.message);
			enabled(button);
			return;
		}
    idElement('payjpToken').value = response.id;
		payment(button);
  });
}

//プラン選択
function selectPlanBank(plan) {
	contentTemplate('applyTemplate');
  const title = querySelector("#content h3");
	title.append("銀行振込");
	const tds = querySelectorAll("td");
	tds[0].append(plan.name);
	tds[1].append(formatter.format(plan.price), '/', (plan.year ? '年' : '月' ));
	idElement("paymentKind").value = "GMO_AOZORA_NET_BANK";
	idElement("contractPlan").value = plan.plan;
	idElement("contractSpan").value = plan.span;
	const button = querySelector("button");
	button.setAttribute("onclick", "payment(this)");
}


// 支払い
function payment(button) {
// すでにカード情報があるまたは銀行振込の場合
	disabledForElement(button);
  post('/contract/register', '#content', () => display('/memo/user/contract/list'));
}

//クレジットカード変更画面
function dispChangeCard() {
	contentTemplate('changeCardTemplate');
	post('/contract/card', null, data => {
		if (!data.publicKey) {
			return;
		}
		publicKey = data.publicKey;
		var yearMonth = idElement("expYearMonth");
		yearMonth.append(`${data.expMonth} / ${data.expYear}`);
		var cardNumber = idElement("cardNumber");
		cardNumber.firstChild.src = `https://checkout.pay.jp/images/creditcard/${data.brandImage}.png`;
		cardNumber.append(`XXXX-XXXX-XXXX-${data.last4}`);
		appendPayjpCardForm('#payjpCard');
	});

}
// クレジットカード変更処理
function changeCard(button) {
	disabledForElement(button);
	payjp.createToken(cardElement).then(response => {
		if (response.error) {
			alert(response.error.message);
			enabled(button);
			return;
		}
		idElement("payjpToken").value = response.id;
		post('/contract/card/update', '#content', () => display('/memo/user/credit/change'));
  });
}

// memo利用照会サービスアカウント情報登録
function dispMeisaiAccountRegister() {
	contentTemplate('meisaiAccountRegisterTemplate');

  post('/meisai/account/get', null, data => {
		set('#content', data);
		if (data.userId == null) {
			infoNotification("未登録です。");
		} else if (!data.authenticated) {
			idElement("authenticate").disabled = false;
		}
	});
}
function dispVechicleRegister() {
	contentTemplate('vehicleRegisterTemplate');

	post('/master/vehicle/list', null, data => {
		if (!initEditableList(data.list)) return;
		var tbody = document.createElement("tbody");
		for (let key in data.list) {
			var vehicle = data.list[key];
			let row = createRow('vehicleRegisterTemplate'); 
			let tds = row.querySelectorAll("td");
			tds[0].append(vehicle.vehicle_number);
			if (vehicle.inactive_datetime != null) {
				tds[1].append(createClone("check"));
			}
			let inputs = row.querySelectorAll("input");
			inputs[0].value = vehicle.id;
			inputs[1].value = vehicle.vehicle_number;
			inputs[2].checked = vehicle.inactive_datetime != null;
			tbody.append(row);
		}
		querySelector("#content .uk-table-divider tbody").replaceWith(tbody);
	});
}
function dispRouteRegister() {
	contentTemplate('routeRegisterTemplate');
	appendKeyOption("logicalOperation", getEnumeration("LogicalOperation"), (key,value)=>option(key, value));

	post('/master/route/list', null, data => {
		if (!initEditableList(data.list)) return;
		const logicalOperation = getEnumeration("LogicalOperation");
		const tbody = document.createElement("tbody");
		for (let key in data.list) {
			var route = data.list[key];
			let row = createRow('routeRegisterTemplate'); 
			let tds = row.querySelectorAll("td");
			tds[0].append(blank(route.entrance));
			tds[1].append(blank(route.exit));
			tds[2].append(logicalOperation[route.logicalOperation]);
			if (route.inactive_datetime != null) {
				tds[3].append(createClone("check"));
			}
			let inputs = row.querySelectorAll("input, select");
			inputs[0].value = route.id;
			inputs[1].value = route.entrance;
			inputs[2].value = route.exit;
			appendKeySelectOption(inputs[3], getEnumeration("LogicalOperation"), (key,value)=>option(key, value));
			inputs[3].value = route.logicalOperation;
			inputs[4].checked = route.inactive_datetime != null;
			tbody.append(row);
		}
		querySelector("#content .uk-table-divider tbody").replaceWith(tbody);
	});
}

function dispFaxRegister() {
	contentTemplate('faxRegisterTemplate');

	post('/master/fax/list', null, data => {
		if (!initEditableList(data.list)) return;
		var tbody = document.createElement("tbody");
		for (let key in data.list) {
			var fax = data.list[key];
			let row = createRow('faxRegisterTemplate'); 
			let tds = row.querySelectorAll("td");
			tds[0].append(fax.fax_number);
			if (fax.inactive_datetime != null) {
				tds[1].append(createClone("check"));
			}
			let inputs = row.querySelectorAll("input");
			inputs[0].value = fax.id;
			inputs[1].value = fax.fax_number;
			inputs[2].checked = fax.inactive_datetime != null;
			tbody.append(row);
		}
		querySelector("#content .uk-table-divider tbody").replaceWith(tbody);
	});
}

function dispEmailAddressRegister() {
	contentTemplate('emailAddressRegisterTemplate');

	post('/master/email_address/list', null, data => {
		if (!initEditableList(data.list)) return;
		var tbody = document.createElement("tbody");
		for (let key in data.list) {
			var emailAddress = data.list[key];
			let row = createRow('emailAddressRegisterTemplate'); 
			let tds = row.querySelectorAll("td");
			tds[0].append(emailAddress.email_address);
			if (emailAddress.inactive_datetime != null) {
				tds[1].append(createClone("check"));
			}
			let inputs = row.querySelectorAll("input");
			inputs[0].value = emailAddress.id;
			inputs[1].value = emailAddress.email_address;
			inputs[2].checked = emailAddress.inactive_datetime != null;
			tbody.append(row);
		}
		querySelector("#content .uk-table-divider tbody").replaceWith(tbody);
	});
}
function dispMeisaiOutputRegister() {
	contentTemplate('meisaiOutputRegisterTemplate');
	post('/meisai/output/list', null, data => {
		if (!initEditableList(data.list)) return;
		if(data.faxList.length > 0) {
			appendOption("faxId", data.faxList, record => option(record.id, record.fax_number));
		}
		if(data.emailList.length > 0) {
			appendOption("emailId", data.emailList, record => option(record.id, record.email_address));
		}
		const condtionKind = getEnumeration("ConditionKind");
		var tbody = document.createElement("tbody");
		for (let key in data.list) {
			var output = data.list[key];
			let row = createRow('meisaiOutputRegisterTemplate'); 
			let tds = row.querySelectorAll("td");
			const a = create("a");
			if (output.condition_kind == 'DAY_OF_MONTH') {
				a.setAttribute("onClick", `setTransition('outputId','${output.id}'), display(\'/memo/user/meisai/day_condition/register\')`);
			} else {
				a.setAttribute("onClick", `setTransition('outputId','${output.id}'), display(\'/memo/user/meisai/day_of_week_condition/register\')`);
			}
			a.append(output.name);
			tds[0].append(a);
			if (output.fax_id != null) {
				var fax = find(data.faxList, record => record.id == output.fax_id);
				if (fax != null) {
					tds[2].append(fax.fax_number);
				}
			}
			if (output.email_id != null) {
				var email = find(data.emailList, record => record.id == output.email_id);
				if (email != null) {
					tds[3].append(email.email_address);
				}
			}
			tds[4].append(condtionKind[output.condition_kind]);
			if (output.inactive_datetime != null) {
				tds[5].append(createClone("check"));
			}
			let inputs = row.querySelectorAll("input, select");
			inputs[0].value = output.id;
			inputs[1].checked = output.certificate_pdf;
			inputs[2].checked = output.detail_pdf;
			inputs[3].checked = output.detail_csv;
			inputs[4].value = output.name;
			inputs[5].checked = inputs[1].checked;
			inputs[6].checked = inputs[2].checked;
			inputs[7].checked = inputs[3].checked;
			if(data.faxList.length > 0) {
				appendSelectOption(inputs[8], data.faxList, record => option(record.id, record.fax_number));
				inputs[8].value = output.fax_id;
			}
			if(data.emailList.length > 0) {
				appendSelectOption(inputs[9], data.emailList, record => option(record.id, record.email_address));
				inputs[9].value = output.email_id;
			}
			inputs[10].value = output.condition_kind;
			inputs[11].checked = output.inactive_datetime != null;
			tbody.append(row);
		}
		querySelectorAll("#content .uk-table tbody")[1].replaceWith(tbody);
	});
}
function dispMeisaiDayConditionRegister() {

	contentTemplate('meisaiDayConditionRegisterTemplate');
	changeConditionKind("DAY_OF_MONTH");
	post('/meisai/output/day_of_month_list', null, data => {
		if(listLength(data.list) > 0) {
			var outputSelect = appendOption("outputId", data.list, record => option(record.id, record.name));
			const outputId = popTransition('outputId');
			if (outputId != null) {
				outputSelect.value = outputId;
			}
			changeDayOutput(outputSelect);
		}
	});
}
function changeOutput(outputSelect, selectFunction) {
	var option = outputSelect.options[outputSelect.selectedIndex];
	selectFunction(option.value, option.text);
}
function changeDayOutput(outputSelect) {
	changeOutput(outputSelect, (value, text) => searchDayOutput(value, text));
}
function searchDayOutput(outputId, outputName) {
	post('/meisai/day_condition/list', {outputId: outputId}, data => {
		replaceChild("outputName", `出力名:${outputName}`);
		defaultEdit();
		initEditableList(data.list);
		var tbody = document.createElement("tbody");
		for (let key in data.list) {
			var condition = data.list[key];
			let row = createRow('meisaiDayConditionRegisterTemplate'); 
			let tds = row.querySelectorAll("td");
			tds[0].append(dayLabel(condition.from_day));
			tds[1].append(dayLabel(condition.to_day));
			tds[2].append(dayLabel(condition.extraction_day));
			var description = descriptionLabel(condition.from_day, condition.to_day, condition.extraction_day, dayLabel);
			tds[3].append(description);
			if (condition.inactive_datetime != null) {
				tds[4].append(createClone("check"));
			}
			const button = tds[5].querySelector("button");
			button.onclick = "this.disabled=true;ouputTest()";
			button.setAttribute("onClick",`this.disabled=true;post('/meisai/day_condition/output', {dayConditionId:${condition.id},})`);
			tds[9].append(description);
			let inputs = row.querySelectorAll("input, select");
			inputs[0].value = condition.output_id;
			inputs[1].value = condition.id;
			inputs[2].append(createClone("DAY_OF_MONTH"));
			inputs[2].value = condition.from_day;
			inputs[3].append(createClone("DAY_OF_MONTH"));
			inputs[3].value = condition.to_day;
			inputs[4].append(createClone("DAY_OF_MONTH"));
			inputs[4].value = condition.extraction_day;
			inputs[5].checked = condition.inactive_datetime != null;
			tbody.append(row);
		}
		querySelectorAll("#content .uk-table tbody")[1].replaceWith(tbody);
	});
}
function changeConditionKind(kind) {
	replaceClone("extractionDay").append(createClone(kind));
	replaceClone("fromDay").append(createClone(kind));
	replaceClone("toDay").append(createClone(kind));
}
function changeDayOfWeekConditionKind(kind) {
	replaceClone("extractionDayOfWeek").append(createClone(kind));
	replaceClone("fromDayOfWeek").append(createClone(kind));
	replaceClone("toDayOfWeek").append(createClone(kind));
}
function dispMeisaiDayOfWeekConditionRegister() {

	contentTemplate('meisaiDayOfWeekConditionRegisterTemplate');
	changeDayOfWeekConditionKind("DAY_OF_WEEK");
	post('/meisai/output/day_of_week_list', null, data => {
		if(listLength(data.list) > 0) {
			var outputSelect = appendOption("outputId", data.list, record => option(record.id, record.name));
			changeDayOfWeekOutput(outputSelect);
		}
	});var planList;
  var publicKey;
  var router;
  
  // 戻る動作
  window.addEventListener('popstate', function(e) {
    var route = router[location.pathname];
    if (route) {
      route();
    }
  });
  
  // 画面表示初期設定
  function init() {
    router = {
      "/memo/user/mypage": dispMyPage,
      "/memo/user/profile/change": dispChangeProfile,
      "/memo/user/password/change": dispChangePassword,
      "/memo/user/email_address/change": dispChangeEmailAddress,
      "/memo/user/organization/change": dispChangeOrganization,
      "/memo/user/organization/address/verify": dispVerifyOrganizationAddress,
      "/memo/user/organization/user": dispOrganizationUser,
      "/memo/user/organization/user/save": dispOrganizationUserSave,
      "/memo/user/organization/user/invite": dispOrganizationUserInvite,
      "/memo/user/contract/list": dispContract,
      "/memo/user/contract/apply": dispPlanList,
      "/memo/user/credit/change": dispChangeCard,
      "/memo/user/master/vehicle/register": dispVechicleRegister,
      "/memo/user/master/route/register": dispRouteRegister,
      "/memo/user/master/fax/register": dispFaxRegister,
      "/memo/user/master/email_address/register": dispEmailAddressRegister,
      "/memo/user/meisai/account/register": dispMeisaiAccountRegister,
      "/memo/user/meisai/output/register": dispMeisaiOutputRegister,
      "/memo/user/meisai/day_condition/register": dispMeisaiDayConditionRegister,
      "/memo/user/meisai/day_of_week_condition/register": dispMeisaiDayOfWeekConditionRegister,
      "/memo/user/meisai/vehicle/register": dispMeisaiVehicleRegister,
      "/memo/user/meisai/route/register": dispMeisaiRouteRegister,
      "/memo/user/meisai/send_fax/list": dispSendFaxList,
      "/memo/user/inquiry": dispInquiry
    };
  }
  // 画面表示
  var display = pathname => {
    UIkit.offcanvas('#offcanvas-slide').hide();
    var route = router[pathname];
    if (route) {
      clearErrorMessage();
      route();
      history.pushState(null, null, pathname);
      return true;
    }
    return false;
  }
  
  
  //ポスト
  function post(path, selector, func, errorFunc, confirmMessage, isArray) {
    if (!selector || isString(selector)) {
      postJsonUrl('/memo/api' + path, selector, func, errorFunc, confirmMessage, isArray);
    } else {
      postJsonData('/memo/api' + path, selector, func, errorFunc, confirmMessage, isArray);
    }
  }
  function postArray(path, selector, func, errorFunc, confirmMessage) {
    post(path, selector, func, errorFunc, confirmMessage, true);
  }
  function download(path, data, fileName, input) {
    downloadUrl('/memo/api' + path, data, fileName, input);
  }
  
  //ログイン
  dispLogin = () => {
    // 認証済み
    if (getToken()) {
      dispTop();
      return;
    }
    // 未認証
    if (location.pathname == "/memo/user/signup") { //サインアップ
      dispRegisterEmailAddress();
    } else if (location.pathname == "/memo/user/singup/activate") { //サインアップ認証
      dispActivate();
    } else if (location.pathname == "/memo/user/email_address/change/activate") { //メールアドレス変更認証
      dispActivateChange();
    } else if (location.pathname == "/memo/user/organization/menber/activate") { //組織メンバー認証
      dispActivateMenber();
    } else if (location.pathname == "/memo/user/password/reset") { //パスワード再設定
      dispResetPassword();
    } else if (location.pathname == "/memo/user/password/reset/activate") { //パスワード再設定認証
      dispActivateResetPassword();
    } else {
      articleTemplate('loginTemplate');
    }
  }
  
  //トップ
  function dispTop() {
    init();
    articleTemplate('topTemplate');
    var pathname = location.pathname;
    
    if (!display(pathname)) {
      display("/memo/user/mypage");
    }
  }
  
  // ログイン画面
  function dispLoginView() {
    history.pushState(null, null, '/memo/user/');
    dispLogin();
  }
  
  //ログイン
  function login() {
    post( '/session/login',  '#content', data => {
      if (data.token) {
        saveToken(data.token);
        dispTop();
      }
    });
  }
  
  function reflectToken(data, url) {
    if (data.token) {
      saveToken(data.token);
    }
    display(url);
  }
  
  //ログアウト
  function logout() {
    if (confirm('ログアウトしますか？')) {
      logoutProcess();
    }
  }
  function logoutProcess() {
    removeToken();
    UIkit.nav('#offcanvas-slide').$destroy();
    dispLoginView();
  }
  
  //Eメールアドレス登録
  dispRegisterEmailAddress = () => {
    articleTemplate('registerEmailAddressTemplate');
  }
  
  //会員登録申請
  dispActivate = () => {
    articleTemplate('activateTemplate');
    var search = location.search;
    if (search.startsWith("?token=")) {
      idElement("token").value = search.substring(7);
      history.pushState(null, null, location.pathname);
    }
  }
  function dispMyPage() {
    contentTemplate('notificationTemplate');
  
    post('/account/notification/list', null, data => {
      if(listLength(data.list) == 0) return;
      var tbody = document.createElement("tbody");
      for (var key in data.list) {
        var record = data.list[key];
        var row = createRow('notificationTemplate');
        var cols = row.querySelectorAll("th,td");
        cols[0].append(record.fromDate);
        cols[1].append(record.content);
        tbody.append(row);
      }
      querySelector("#content tbody").replaceWith(tbody);
    });
  }
  
  
  //登録情報変更
  function dispChangeProfile() {
  contentTemplate('changeProfileTemplate');
  post('/account/profile/get', null, data => set('#content', data));
  //renderGoogleLoginButton(onSignIn);
  }
  //パスワード変更
  function dispChangePassword() {
    contentTemplate('changePasswordTemplate');
  }
  //メールアドレス変更
  function dispChangeEmailAddress() {
    contentTemplate('changeEmailAddressTemplate');
    post('/account/email_address/get', null, data => set('#content', data));
  }
  
  //メールアドレス変更有効化
  dispActivateChange = () => {
    if (getToken()) {
      contentTemplate('approvedActivateChangeTemplate');
    } else {
      articleTemplate('activateChangeTemplate');
    }
    var search = location.search;
    if (search.startsWith("?token=")) {
      idElement("token").value = search.substring(7);
      history.pushState(null, null, location.pathname);
    }
  }
  
  // 組織情報登録・変更・削除
  function dispChangeOrganization() {
    contentTemplate('organizationChangeTemplate');
    post('/organization/get', null, data => {set('#content', data);});
  }
  
  // 所在地確認番号登録
  function dispVerifyOrganizationAddress() {
    contentTemplate('verifyAddressTemplate');
  }
  
  function getPrefixAddress() {
    var postCode = idElement("postCode").value;
    if (postCode == null) {
      alert("郵便番号を入力してください.");
      return;
    }
    post('/general/prefix_address', {postCode: idElement("postCode").value}, data => {
      if(data.value != null){
        idElement('address').value = data.value;
      } else {
        alert("所在地を検索できませんでした.");
      }
      });
  }
  
  // メンバー一覧
  function dispOrganizationUser() {
    contentTemplate('organizationUserTemplate');
    post('/organization/user/list', null, data => {
      if(listLength(data.list) == 0) return;
      var tbody = document.createElement("tbody");
      for (var key in data.list) {
        var record = data.list[key];
        var row = createRow('organizationUserTemplate');
        var cols = row.querySelectorAll("td");
        cols[0].append(record.emailAddress);
        cols[1].append(record.name);
        cols[2].append(record.registerDateTime);
        tbody.append(row);
      }
      querySelector("#content .uk-table tbody").replaceWith(tbody);
    });
  }
  
  // メンバー変更・削除
  function dispOrganizationUserSave() {
    contentTemplate('organizationUserSaveTemplate');
    post('/organization/user/list', null, data => {
      if(listLength(data.list) == 0) return;
      var tbody = document.createElement("tbody");
      for (var key in data.list) {
        var record = data.list[key];
        var row = createRow('organizationUserSaveTemplate');
        var cols = row.querySelectorAll("td");
        var accountId = cols[0].querySelector("input");
        accountId.value = record.accountId;
        cols[0].append(record.emailAddress);
        cols[1].append(record.name);
        cols[2].append(record.registerDateTime);
        tbody.append(row);
      }
      querySelector("#content .uk-table tbody").replaceWith(tbody);
    });
  }
  
  // メンバー情報登録・変更・削除
  function dispOrganizationUserInvite() {
    contentTemplate('organizationUserInviteTemplate');
    searchInvitedUser();
  }
  function searchInvitedUser() {
    post('/organization/user/list/invited', null, data => {
      if(listLength(data.list) == 0) return;
      var tbody = document.createElement("tbody");
      for (var key in data.list) {
        var record = data.list[key];
        var row = createRow('organizationUserInviteTemplate');
        var cols = row.querySelectorAll("td");
        cols[0].append(record.emailAddress);
        cols[1].append(record.lastInviteDateTime);
        const button = row.querySelector("button");
        button.setAttribute("onclick", `confirmPost('最招待しますか？', '/organization/user/invite', {emailAddress: '${record.emailAddress}', checkEmailAddress:'${record.emailAddress}'}, () => searchInvitedUser()); this.disabled = true;`);
        tbody.append(row);
      }
      querySelector("#content .uk-table tbody").replaceWith(tbody);
    });
  }
  
  // 契約一覧表示
  function dispContract() {
    contentTemplate('contractTemplate');
    gmemoontractList();
  }
  
  // 契約一覧取得
  function gmemoontractList() {
    post('/contract/list', "#content", data => {
      initList(data.contractList);
      let content = idElement("content");
      let tables = content.querySelectorAll("table");
      for (let i = 0; i < tables.length; i++) {
        content.removeChild(tables[i]);
      }
      if(listLength(data.contractList) == 0) {
        return;
      }
      const contractKind = getEnumeration("ContractKind");
      for (let key in data.contractList) {
        var contract = data.contractList[key];
        let table = createClone('contractTemplate.contract'); 
        let tds = table.querySelectorAll("td");
        tds[0].append(contractKind[contract.contractKind]);
        tds[1].append(contract.activationDatetime);
        if (contract.cancelDatetime != null) {
          tds[2].firstChild.replaceWith(text(contract.cancelDatetime));
        } else {
          tds[2].querySelector('#contractId').value = contract.contractId;
        }
        tds[3].append(contract.paymentKind == "CREDIT" ? "クレジットカード" : "銀行振込");
        const contractPlan = table.querySelector("#contractPlan");
        const contractSpan = table.querySelector("#contractSpan");
        if (contract.cancelDatetime != null) {
          tds[4].removeChild(tds[4].firstChild);
        } else {
          appendKeySelectOption(contractPlan, getEnumeration("ContractPlan"), (key,value)=>option(key, value));
          appendKeySelectOption(contractSpan, getEnumeration("ContractSpan"), (key,value)=>option(key, value));
        }
        const today = getToday();
        let tbody = table.querySelector("tbody");
        for (let key in contract.contractPeriodList) {
          let contractPeriod = contract.contractPeriodList[key];
          let period = createClone('contractTemplate.period');
          let periodTds = period.querySelectorAll("td");
          periodTds[0].append(getEnumeration("ContractPlan")[contractPeriod.plan]);
          periodTds[1].append(contractPeriod.year ? "年":"月");
          periodTds[2].append(contractPeriod.fromDate);
          periodTds[3].append(contractPeriod.toDate);
          if (today < contractPeriod.fromDate || today > contractPeriod.toDate) {
            period.querySelector("tr").classList.add("notToday");
          }
          periodTds[4].append(contractPeriod.price.toLocaleString() + (contractPeriod.year ? '円/年': '円/月') + '(税込)');
          if (contractPeriod.invoiceFileName) {
            const button = periodTds[5].querySelector("button");
            button.setAttribute("onClick",'download(\'/contract/invoice/download\', {invoiceId:' + contractPeriod.invoiceId + '}, \'' + contractPeriod.invoiceFileName +  '\', this)');
          } else {
            periodTds[5].removeChild(periodTds[5].firstChild);
          }
          if (contractPeriod.receiptFileName) {
            const button = periodTds[6].querySelector("button");
            button.setAttribute("onClick",'download(\'/contract/receipt/download\', {receiptId:' + contractPeriod.receiptId + '}, \'' + contractPeriod.receiptFileName +  '\', this)');
          } else {
            periodTds[6].removeChild(periodTds[6].firstChild);
          }
          if (key == 0 && contract.cancelDatetime == null) {
            contractPlan.value = contractPeriod.plan;
            contractSpan.value = contractPeriod.span;
          }
          tbody.append(period);
  
        }
        idElement("content").append(table);
        if (data.accountNumber) {
          let trs = createClone("contractTemplate.account");
          let tds = trs.querySelectorAll("td");
          tds[1].append(data.branchName + '(' + data.branchNumber + ')');
          tds[2].append(data.accountNumber);
          idElement("content").append(trs);
        }
      }
      
      
    });
  }
  //プラン申し込み画面
  function dispPlanList() {
    contentTemplate('planTemplate');
    // 契約プラン取得
    post('/contract/plan/list', null, data => {
      if (!data.planList) return;
      planList = data.planList;
      publicKey = data.publicKey;
      var tbody = document.createElement("tbody");
      for (var i = 0; i < data.planList.length; i++) {
        var plan = data.planList[i];
        var row = createRow('planTemplate'); // TODO contentTemplate でtemplateオブジェクトを返してその中から取得できるようにしたい。
        var tds = row.querySelectorAll("td");
        if (i % 2 == 0) {
          tds[0].setAttribute("rowspan", 2);
          tds[0].append(getEnumeration("ContractPlan")[plan.plan]);
        } else {
          tds[0].parentElement.removeChild(tds[0]);
        }
        tds[1].append(getEnumeration("ContractSpan")[plan.span]);
        tds[2].append(formatter.format(plan.price), '/' , (plan.year ? '年' : '月' ));
        if (data.contractSpan != null) {
          // button 削除
          tds[3].removeChild(tds[3].firstChild);
          tds[4].removeChild(tds[4].firstChild);
          if (data.contractPlan == plan.plan && data.contractSpan == plan.span) {
            tds[3].append("申込済み");
          }
        } else {
          var buttons = row.querySelectorAll("button");
          buttons[0].setAttribute("onClick", "selectPlanCredit(planList[" + i+ "], " + data.hasDefaultCard + ")");
          buttons[1].setAttribute("onClick", "selectPlanBank(planList[" + i + "])");
        }
        tbody.append(row);
        querySelector("#content .uk-table tbody").replaceWith(tbody);
      }
    });
  }
  
  //プラン選択
  function selectPlanCredit(plan, hasDefaultCard) {
    contentTemplate('applyTemplate');
    
    const title = querySelector("#content h3");
    title.append("クレジットカード決済");
    const tds = querySelectorAll("td");
    tds[0].append(plan.name);
    tds[1].append(formatter.format(plan.price), '/', (plan.year ? '年' : '月' ));
    idElement("paymentKind").value = "CREDIT"
    idElement("contractPlan").value = plan.plan;
    idElement("contractSpan").value = plan.span;
    if (hasDefaultCard) {
      idElement("hasDefaultCard").value = true;
      const button = querySelector("button");
      button.setAttribute("onclick", "payment(this)");
    } else {
      const form = idElement("contractRegister");
      removeQuerySelector("button");
      form.append(createClone("applyTemplate.card"));
      appendPayjpCardForm('#payjpCard');
    }
  
  }
  
  var payjp;
  var cardElement;
  function appendPayjpCardForm(mountId) {
    // 公開鍵を登録し、起点となるオブジェクトを取得します
    if (payjp == null) {
      payjp = Payjp(publicKey);
    }
    // element(入力フォームの単位)を生成します
    cardElement = payjp.elements().create('card');
    // elementをDOM上に配置します
    cardElement.mount(mountId);
  }
  
  function createToken(button) {
    disabledForElement(button);
    payjp.createToken(cardElement).then(response => {
      if (response.error) {
        alert(response.error.message);
        enabled(button);
        return;
      }
      idElement('payjpToken').value = response.id;
      payment(button);
    });
  }
  
  //プラン選択
  function selectPlanBank(plan) {
    contentTemplate('applyTemplate');
    const title = querySelector("#content h3");
    title.append("銀行振込");
    const tds = querySelectorAll("td");
    tds[0].append(plan.name);
    tds[1].append(formatter.format(plan.price), '/', (plan.year ? '年' : '月' ));
    idElement("paymentKind").value = "GMO_AOZORA_NET_BANK";
    idElement("contractPlan").value = plan.plan;
    idElement("contractSpan").value = plan.span;
    const button = querySelector("button");
    button.setAttribute("onclick", "payment(this)");
  }
  
  
  // 支払い
  function payment(button) {
  // すでにカード情報があるまたは銀行振込の場合
    disabledForElement(button);
    post('/contract/register', '#content', () => display('/memo/user/contract/list'));
  }
  
  //クレジットカード変更画面
  function dispChangeCard() {
    contentTemplate('changeCardTemplate');
    post('/contract/card', null, data => {
      if (!data.publicKey) {
        return;
      }
      publicKey = data.publicKey;
      var yearMonth = idElement("expYearMonth");
      yearMonth.append(`${data.expMonth} / ${data.expYear}`);
      var cardNumber = idElement("cardNumber");
      cardNumber.firstChild.src = `https://checkout.pay.jp/images/creditcard/${data.brandImage}.png`;
      cardNumber.append(`XXXX-XXXX-XXXX-${data.last4}`);
      appendPayjpCardForm('#payjpCard');
    });
  
  }
  // クレジットカード変更処理
  function changeCard(button) {
    disabledForElement(button);
    payjp.createToken(cardElement).then(response => {
      if (response.error) {
        alert(response.error.message);
        enabled(button);
        return;
      }
      idElement("payjpToken").value = response.id;
      post('/contract/card/update', '#content', () => display('/memo/user/credit/change'));
    });
  }
  
  // memo利用照会サービスアカウント情報登録
  function dispMeisaiAccountRegister() {
    contentTemplate('meisaiAccountRegisterTemplate');
  
    post('/meisai/account/get', null, data => {
      set('#content', data);
      if (data.userId == null) {
        infoNotification("未登録です。");
      } else if (!data.authenticated) {
        idElement("authenticate").disabled = false;
      }
    });
  }
  function dispVechicleRegister() {
    contentTemplate('vehicleRegisterTemplate');
  
    post('/master/vehicle/list', null, data => {
      if (!initEditableList(data.list)) return;
      var tbody = document.createElement("tbody");
      for (let key in data.list) {
        var vehicle = data.list[key];
        let row = createRow('vehicleRegisterTemplate'); 
        let tds = row.querySelectorAll("td");
        tds[0].append(vehicle.vehicle_number);
        if (vehicle.inactive_datetime != null) {
          tds[1].append(createClone("check"));
        }
        let inputs = row.querySelectorAll("input");
        inputs[0].value = vehicle.id;
        inputs[1].value = vehicle.vehicle_number;
        inputs[2].checked = vehicle.inactive_datetime != null;
        tbody.append(row);
      }
      querySelector("#content .uk-table-divider tbody").replaceWith(tbody);
    });
  }
  function dispRouteRegister() {
    contentTemplate('routeRegisterTemplate');
    appendKeyOption("logicalOperation", getEnumeration("LogicalOperation"), (key,value)=>option(key, value));
  
    post('/master/route/list', null, data => {
      if (!initEditableList(data.list)) return;
      const logicalOperation = getEnumeration("LogicalOperation");
      const tbody = document.createElement("tbody");
      for (let key in data.list) {
        var route = data.list[key];
        let row = createRow('routeRegisterTemplate'); 
        let tds = row.querySelectorAll("td");
        tds[0].append(blank(route.entrance));
        tds[1].append(blank(route.exit));
        tds[2].append(logicalOperation[route.logicalOperation]);
        if (route.inactive_datetime != null) {
          tds[3].append(createClone("check"));
        }
        let inputs = row.querySelectorAll("input, select");
        inputs[0].value = route.id;
        inputs[1].value = route.entrance;
        inputs[2].value = route.exit;
        appendKeySelectOption(inputs[3], getEnumeration("LogicalOperation"), (key,value)=>option(key, value));
        inputs[3].value = route.logicalOperation;
        inputs[4].checked = route.inactive_datetime != null;
        tbody.append(row);
      }
      querySelector("#content .uk-table-divider tbody").replaceWith(tbody);
    });
  }
  
  function dispFaxRegister() {
    contentTemplate('faxRegisterTemplate');
  
    post('/master/fax/list', null, data => {
      if (!initEditableList(data.list)) return;
      var tbody = document.createElement("tbody");
      for (let key in data.list) {
        var fax = data.list[key];
        let row = createRow('faxRegisterTemplate'); 
        let tds = row.querySelectorAll("td");
        tds[0].append(fax.fax_number);
        if (fax.inactive_datetime != null) {
          tds[1].append(createClone("check"));
        }
        let inputs = row.querySelectorAll("input");
        inputs[0].value = fax.id;
        inputs[1].value = fax.fax_number;
        inputs[2].checked = fax.inactive_datetime != null;
        tbody.append(row);
      }
      querySelector("#content .uk-table-divider tbody").replaceWith(tbody);
    });
  }
  
  function dispEmailAddressRegister() {
    contentTemplate('emailAddressRegisterTemplate');
  
    post('/master/email_address/list', null, data => {
      if (!initEditableList(data.list)) return;
      var tbody = document.createElement("tbody");
      for (let key in data.list) {
        var emailAddress = data.list[key];
        let row = createRow('emailAddressRegisterTemplate'); 
        let tds = row.querySelectorAll("td");
        tds[0].append(emailAddress.email_address);
        if (emailAddress.inactive_datetime != null) {
          tds[1].append(createClone("check"));
        }
        let inputs = row.querySelectorAll("input");
        inputs[0].value = emailAddress.id;
        inputs[1].value = emailAddress.email_address;
        inputs[2].checked = emailAddress.inactive_datetime != null;
        tbody.append(row);
      }
      querySelector("#content .uk-table-divider tbody").replaceWith(tbody);
    });
  }
  function dispMeisaiOutputRegister() {
    contentTemplate('meisaiOutputRegisterTemplate');
    post('/meisai/output/list', null, data => {
      if (!initEditableList(data.list)) return;
      if(data.faxList.length > 0) {
        appendOption("faxId", data.faxList, record => option(record.id, record.fax_number));
      }
      if(data.emailList.length > 0) {
        appendOption("emailId", data.emailList, record => option(record.id, record.email_address));
      }
      const condtionKind = getEnumeration("ConditionKind");
      var tbody = document.createElement("tbody");
      for (let key in data.list) {
        var output = data.list[key];
        let row = createRow('meisaiOutputRegisterTemplate'); 
        let tds = row.querySelectorAll("td");
        const a = create("a");
        if (output.condition_kind == 'DAY_OF_MONTH') {
          a.setAttribute("onClick", `setTransition('outputId','${output.id}'), display(\'/memo/user/meisai/day_condition/register\')`);
        } else {
          a.setAttribute("onClick", `setTransition('outputId','${output.id}'), display(\'/memo/user/meisai/day_of_week_condition/register\')`);
        }
        a.append(output.name);
        tds[0].append(a);
        if (output.fax_id != null) {
          var fax = find(data.faxList, record => record.id == output.fax_id);
          if (fax != null) {
            tds[2].append(fax.fax_number);
          }
        }
        if (output.email_id != null) {
          var email = find(data.emailList, record => record.id == output.email_id);
          if (email != null) {
            tds[3].append(email.email_address);
          }
        }
        tds[4].append(condtionKind[output.condition_kind]);
        if (output.inactive_datetime != null) {
          tds[5].append(createClone("check"));
        }
        let inputs = row.querySelectorAll("input, select");
        inputs[0].value = output.id;
        inputs[1].checked = output.certificate_pdf;
        inputs[2].checked = output.detail_pdf;
        inputs[3].checked = output.detail_csv;
        inputs[4].value = output.name;
        inputs[5].checked = inputs[1].checked;
        inputs[6].checked = inputs[2].checked;
        inputs[7].checked = inputs[3].checked;
        if(data.faxList.length > 0) {
          appendSelectOption(inputs[8], data.faxList, record => option(record.id, record.fax_number));
          inputs[8].value = output.fax_id;
        }
        if(data.emailList.length > 0) {
          appendSelectOption(inputs[9], data.emailList, record => option(record.id, record.email_address));
          inputs[9].value = output.email_id;
        }
        inputs[10].value = output.condition_kind;
        inputs[11].checked = output.inactive_datetime != null;
        tbody.append(row);
      }
      querySelectorAll("#content .uk-table tbody")[1].replaceWith(tbody);
    });
  }
  function dispMeisaiDayConditionRegister() {
  
    contentTemplate('meisaiDayConditionRegisterTemplate');
    changeConditionKind("DAY_OF_MONTH");
    post('/meisai/output/day_of_month_list', null, data => {
      if(listLength(data.list) > 0) {
        var outputSelect = appendOption("outputId", data.list, record => option(record.id, record.name));
        const outputId = popTransition('outputId');
        if (outputId != null) {
          outputSelect.value = outputId;
        }
        changeDayOutput(outputSelect);
      }
    });
  }
  function changeOutput(outputSelect, selectFunction) {
    var option = outputSelect.options[outputSelect.selectedIndex];
    selectFunction(option.value, option.text);
  }
  function changeDayOutput(outputSelect) {
    changeOutput(outputSelect, (value, text) => searchDayOutput(value, text));
  }
  function searchDayOutput(outputId, outputName) {
    post('/meisai/day_condition/list', {outputId: outputId}, data => {
      replaceChild("outputName", `出力名:${outputName}`);
      defaultEdit();
      initEditableList(data.list);
      var tbody = document.createElement("tbody");
      for (let key in data.list) {
        var condition = data.list[key];
        let row = createRow('meisaiDayConditionRegisterTemplate'); 
        let tds = row.querySelectorAll("td");
        tds[0].append(dayLabel(condition.from_day));
        tds[1].append(dayLabel(condition.to_day));
        tds[2].append(dayLabel(condition.extraction_day));
        var description = descriptionLabel(condition.from_day, condition.to_day, condition.extraction_day, dayLabel);
        tds[3].append(description);
        if (condition.inactive_datetime != null) {
          tds[4].append(createClone("check"));
        }
        const button = tds[5].querySelector("button");
        button.onclick = "this.disabled=true;ouputTest()";
        button.setAttribute("onClick",`this.disabled=true;post('/meisai/day_condition/output', {dayConditionId:${condition.id},})`);
        tds[9].append(description);
        let inputs = row.querySelectorAll("input, select");
        inputs[0].value = condition.output_id;
        inputs[1].value = condition.id;
        inputs[2].append(createClone("DAY_OF_MONTH"));
        inputs[2].value = condition.from_day;
        inputs[3].append(createClone("DAY_OF_MONTH"));
        inputs[3].value = condition.to_day;
        inputs[4].append(createClone("DAY_OF_MONTH"));
        inputs[4].value = condition.extraction_day;
        inputs[5].checked = condition.inactive_datetime != null;
        tbody.append(row);
      }
      querySelectorAll("#content .uk-table tbody")[1].replaceWith(tbody);
    });
  }
  function changeConditionKind(kind) {
    replaceClone("extractionDay").append(createClone(kind));
    replaceClone("fromDay").append(createClone(kind));
    replaceClone("toDay").append(createClone(kind));
  }
  function changeDayOfWeekConditionKind(kind) {
    replaceClone("extractionDayOfWeek").append(createClone(kind));
    replaceClone("fromDayOfWeek").append(createClone(kind));
    replaceClone("toDayOfWeek").append(createClone(kind));
  }
  function dispMeisaiDayOfWeekConditionRegister() {
  
    contentTemplate('meisaiDayOfWeekConditionRegisterTemplate');
    changeDayOfWeekConditionKind("DAY_OF_WEEK");
    post('/meisai/output/day_of_week_list', null, data => {
      if(listLength(data.list) > 0) {
        var outputSelect = appendOption("outputId", data.list, record => option(record.id, record.name));
        changeDayOfWeekOutput(outputSelect);
      }
    });
  }
  function changeDayOfWeekOutput(outputSelect) {
    changeOutput(outputSelect, (value, text) => searchDayOfWeekOutput(value, text));
  }
  function searchDayOfWeekOutput(outputId, outputName) {
    post('/meisai/day_of_week_condition/list', {outputId: outputId}, data => {
      replaceChild("outputName", `出力名:${outputName}`);
      defaultEdit();
      initEditableList(data.list);
      var tbody = document.createElement("tbody");
      for (let key in data.list) {
        var condition = data.list[key];
        let row = createRow('meisaiDayOfWeekConditionRegisterTemplate'); 
        let tds = row.querySelectorAll("td");
        tds[0].append(dayOfWeekLabel(condition.from_day_of_week));
        tds[1].append(dayOfWeekLabel(condition.to_day_of_week));
        tds[2].append(dayOfWeekLabel(condition.extraction_day_of_week));
        var description = descriptionLabel(condition.from_day_of_week, condition.to_day_of_week, condition.extraction_day_of_week, dayOfWeekLabel);
        tds[3].append(description);
        if (condition.inactive_datetime != null) {
          tds[4].append(createClone("check"));
        }
        const button = tds[5].querySelector("button");
        button.onclick = "this.disabled=true;ouputTest()";
        button.setAttribute("onClick",`this.disabled=true;post('/meisai/day_of_week_condition/output', {dayOfWeekConditionId:${condition.id}})`);
        tds[9].append(description);
        let inputs = row.querySelectorAll("input, select");
        inputs[0].value = condition.output_id;
        inputs[1].value = condition.id;
        inputs[2].append(createClone("DAY_OF_WEEK"));
        inputs[2].value = condition.from_day_of_week;
        inputs[3].append(createClone("DAY_OF_WEEK"));
        inputs[3].value = condition.to_day_of_week;
        inputs[4].append(createClone("DAY_OF_WEEK"));
        inputs[4].value = condition.extraction_day_of_week;
        inputs[5].checked = condition.inactive_datetime != null;
        tbody.append(row);
      }
      querySelectorAll("#content .uk-table tbody")[1].replaceWith(tbody);
    });
  }
  var vehicleList;
  function dispMeisaiVehicleRegister() {
  
    contentTemplate('meisaiVehicleRegisterTemplate');
    post('/meisai/output_vehicle/list', null, data => {
      if(listLength(data.list) > 0) {
        vehicleList = data.vehicleList;
        appendOption("vehicleId", vehicleList, record => option(record.id, record.vehicle_number));
        var outputSelect = appendOption("outputId", data.list, record => option(record.id, record.name));
        changeVehicleOutput(outputSelect.value);
      }
    });
  }
  function changeVehicleOutput(outputId) {
    post('/meisai/vehicle/list', {outputId: outputId}, data => {
      defaultEdit();
      initEditableList(data.list);
      var tbody = document.createElement("tbody");
      for (let key in data.list) {
        var outputVehicle = data.list[key];
        let row = createRow('meisaiVehicleRegisterTemplate');
        let tds = row.querySelectorAll("td");
        tds[0].append(find(vehicleList, record => record.id == outputVehicle.vehicle_id).vehicle_number);
        if (outputVehicle.inactive_datetime != null) {
          tds[1].append(createClone("check"));
        }
        let inputs = row.querySelectorAll("input, select");
        inputs[0].value = outputVehicle.id;
        inputs[1].value = outputVehicle.output_id;
        appendSelectOption(inputs[2], vehicleList, record => option(record.id, record.vehicle_number));
        inputs[2].value = outputVehicle.vehicle_id;
        inputs[3].checked = outputVehicle.inactive_datetime != null;
        tbody.append(row);
      }
      querySelectorAll("#content .uk-table tbody")[1].replaceWith(tbody);
    });
  }
  
  var routeList;
  function dispMeisaiRouteRegister() {
    contentTemplate('meisaiRouteRegisterTemplate');
    post('/meisai/output_route/list', null, data => {
      if(listLength(data.list) > 0) {
        const logicalOperation = getEnumeration("LogicalOperation");
        routeList = data.routeList;
        appendOption("routeId", routeList, record => option(record.id, routeLabel(logicalOperation, record)));
        var outputSelect = appendOption("outputId", data.list, record => option(record.id, record.name));
        changeRouteOutput(outputSelect.value);
      }
    });
  }
  function changeRouteOutput(outputId) {
    post('/meisai/route/list', {outputId: outputId}, data => {
      defaultEdit();
      initEditableList(data.list);
      const logicalOperation = getEnumeration("LogicalOperation");
      var tbody = document.createElement("tbody");
      for (let key in data.list) {
        var outputRoute = data.list[key];
        let row = createRow('meisaiRouteRegisterTemplate');
        let tds = row.querySelectorAll("td");
        tds[0].append(routeLabel(logicalOperation, find(routeList, record => record.id == outputRoute.route_id)));
        if (outputRoute.inactive_datetime != null) {
          tds[1].append(createClone("check"));
        }
        let inputs = row.querySelectorAll("input, select");
        inputs[0].value = outputRoute.id;
        inputs[1].value = outputRoute.output_id;
        appendSelectOption(inputs[2], routeList, record => option(record.id, routeLabel(logicalOperation,record)));
        inputs[2].value = outputRoute.route_id;
        inputs[3].checked = outputRoute.inactive_datetime != null;
        tbody.append(row);
      }
      querySelectorAll("#content .uk-table tbody")[1].replaceWith(tbody);
    });
  }
  function dispSendFaxList() {
    contentTemplate('meisaiFaxSendListTemplate');
    post('/meisai/send_fax/year_month_list', null, data => {
      if(data.list && data.list.length > 0) {
        const select = appendOption("yearMonth", data.list, record => option(record.yearMonth));
        select.value = formatYearMonth(new Date());
        searchSendFaxList(select);
      }
    });
  }
  function searchSendFaxList(select) {
    post('/meisai/send_fax/list', {yearMonth: select.value}, data => {
      initList(data.list);
      var tbody = document.createElement("tbody");
      for (let key in data.list) {
        var sendFax = data.list[key];
        let row = createRow('meisaiFaxSendListTemplate');
        let tds = row.querySelectorAll("td");
        tds[0].append(sendFax.startDatetime);
        tds[1].append(sendFax.outputName);
        tds[2].append(blank(sendFax.sentDatetime));
        tds[3].append(blank(sendFax.sentPage));
        tbody.append(row);
      }
      querySelectorAll("#content .uk-table tbody")[1].replaceWith(tbody);
    });
  }
  function routeLabel(logicalOperation, route) {
    var label = "";
    if (route.entrance != null) {
      label += "(自)" + route.entrance + " ";
    }
    if (route.exit != null) {
      label += "(至)" + route.exit + " ";
    }
    label += logicalOperation[route.logicalOperation];
  
    return label;
  }
  function dayLabel(day) {
    if (day > 0) {
      return day + "日";
    }
    switch (day) {
      case 0:
        return "月末日";
      case -1:
        return "月末日前日";
      case -2:
        return "月末日前々日";
    }
  }
  function descriptionLabel(from, to, extraction, labelFunc) {
    if (from == to) {
      return labelFunc(to) + "のデータを"
       + labelFunc(extraction) + "に出力";
    }
    return labelFunc(from) + "から"
    + labelFunc(to) + "のデータを"
     + labelFunc(extraction) + "に出力します";
  }
  const dayOfWeekJson = {
    "MONDAY":"月曜日",
    "TUESDAY":"火曜日",
    "WEDNESDAY":"水曜日",
    "THURSDAY":"木曜日",
    "FRIDAY":"金曜日",
    "SATURDAY":"土曜日",
    "SUNDAY":"日曜日",
  };
  function dayOfWeekLabel(dayOfWeek) {
    return dayOfWeekJson[dayOfWeek];
  }
  function dispInquiry() {
    contentTemplate('inquiryTemplate');
  }
  // function changeHidden(edits) {
  // 	for (let key = 0; key < edits.length; key++) {
  // 		if (edits[key].getAttribute("hidden") != null) {
  // 			edits[key].removeAttribute("hidden");
  // 		} else {
  // 			edits[key].setAttribute("hidden", null);
  // 		}
  // 	}
  // }
  function defaultEdit() {
    const edit = idElement('edit');
    if (edit.checked) {
      edit.checked = false;
      UIkit.toggle(edit).toggle();
    }
  }
  function dispResetPassword() {
    articleTemplate('resetPasswordTemplate');
  }
  function dispActivateResetPassword() {
    articleTemplate('activateResetPasswordTemplate');
    var search = location.search;
    if (search.startsWith("?token=")) {
      idElement("token").value = search.substring(7);
      history.pushState(null, null, location.pathname);
    }
  }
}
function changeDayOfWeekOutput(outputSelect) {
	changeOutput(outputSelect, (value, text) => searchDayOfWeekOutput(value, text));
}
function searchDayOfWeekOutput(outputId, outputName) {
	post('/meisai/day_of_week_condition/list', {outputId: outputId}, data => {
		replaceChild("outputName", `出力名:${outputName}`);
		defaultEdit();
		initEditableList(data.list);
		var tbody = document.createElement("tbody");
		for (let key in data.list) {
			var condition = data.list[key];
			let row = createRow('meisaiDayOfWeekConditionRegisterTemplate'); 
			let tds = row.querySelectorAll("td");
			tds[0].append(dayOfWeekLabel(condition.from_day_of_week));
			tds[1].append(dayOfWeekLabel(condition.to_day_of_week));
			tds[2].append(dayOfWeekLabel(condition.extraction_day_of_week));
			var description = descriptionLabel(condition.from_day_of_week, condition.to_day_of_week, condition.extraction_day_of_week, dayOfWeekLabel);
			tds[3].append(description);
			if (condition.inactive_datetime != null) {
				tds[4].append(createClone("check"));
			}
			const button = tds[5].querySelector("button");
			button.onclick = "this.disabled=true;ouputTest()";
			button.setAttribute("onClick",`this.disabled=true;post('/meisai/day_of_week_condition/output', {dayOfWeekConditionId:${condition.id}})`);
			tds[9].append(description);
			let inputs = row.querySelectorAll("input, select");
			inputs[0].value = condition.output_id;
			inputs[1].value = condition.id;
			inputs[2].append(createClone("DAY_OF_WEEK"));
			inputs[2].value = condition.from_day_of_week;
			inputs[3].append(createClone("DAY_OF_WEEK"));
			inputs[3].value = condition.to_day_of_week;
			inputs[4].append(createClone("DAY_OF_WEEK"));
			inputs[4].value = condition.extraction_day_of_week;
			inputs[5].checked = condition.inactive_datetime != null;
			tbody.append(row);
		}
		querySelectorAll("#content .uk-table tbody")[1].replaceWith(tbody);
	});
}
var vehicleList;
function dispMeisaiVehicleRegister() {

	contentTemplate('meisaiVehicleRegisterTemplate');
	post('/meisai/output_vehicle/list', null, data => {
		if(listLength(data.list) > 0) {
			vehicleList = data.vehicleList;
			appendOption("vehicleId", vehicleList, record => option(record.id, record.vehicle_number));
			var outputSelect = appendOption("outputId", data.list, record => option(record.id, record.name));
			changeVehicleOutput(outputSelect.value);
		}
	});
}
function changeVehicleOutput(outputId) {
	post('/meisai/vehicle/list', {outputId: outputId}, data => {
		defaultEdit();
		initEditableList(data.list);
		var tbody = document.createElement("tbody");
		for (let key in data.list) {
			var outputVehicle = data.list[key];
			let row = createRow('meisaiVehicleRegisterTemplate');
			let tds = row.querySelectorAll("td");
			tds[0].append(find(vehicleList, record => record.id == outputVehicle.vehicle_id).vehicle_number);
			if (outputVehicle.inactive_datetime != null) {
				tds[1].append(createClone("check"));
			}
			let inputs = row.querySelectorAll("input, select");
			inputs[0].value = outputVehicle.id;
			inputs[1].value = outputVehicle.output_id;
			appendSelectOption(inputs[2], vehicleList, record => option(record.id, record.vehicle_number));
			inputs[2].value = outputVehicle.vehicle_id;
			inputs[3].checked = outputVehicle.inactive_datetime != null;
			tbody.append(row);
		}
		querySelectorAll("#content .uk-table tbody")[1].replaceWith(tbody);
	});
}

var routeList;
function dispMeisaiRouteRegister() {
	contentTemplate('meisaiRouteRegisterTemplate');
	post('/meisai/output_route/list', null, data => {
		if(listLength(data.list) > 0) {
			const logicalOperation = getEnumeration("LogicalOperation");
			routeList = data.routeList;
			appendOption("routeId", routeList, record => option(record.id, routeLabel(logicalOperation, record)));
			var outputSelect = appendOption("outputId", data.list, record => option(record.id, record.name));
			changeRouteOutput(outputSelect.value);
		}
	});
}
function changeRouteOutput(outputId) {
	post('/meisai/route/list', {outputId: outputId}, data => {
		defaultEdit();
		initEditableList(data.list);
		const logicalOperation = getEnumeration("LogicalOperation");
		var tbody = document.createElement("tbody");
		for (let key in data.list) {
			var outputRoute = data.list[key];
			let row = createRow('meisaiRouteRegisterTemplate');
			let tds = row.querySelectorAll("td");
			tds[0].append(routeLabel(logicalOperation, find(routeList, record => record.id == outputRoute.route_id)));
			if (outputRoute.inactive_datetime != null) {
				tds[1].append(createClone("check"));
			}
			let inputs = row.querySelectorAll("input, select");
			inputs[0].value = outputRoute.id;
			inputs[1].value = outputRoute.output_id;
			appendSelectOption(inputs[2], routeList, record => option(record.id, routeLabel(logicalOperation,record)));
			inputs[2].value = outputRoute.route_id;
			inputs[3].checked = outputRoute.inactive_datetime != null;
			tbody.append(row);
		}
		querySelectorAll("#content .uk-table tbody")[1].replaceWith(tbody);
	});
}
function dispSendFaxList() {
	contentTemplate('meisaiFaxSendListTemplate');
	post('/meisai/send_fax/year_month_list', null, data => {
		if(data.list && data.list.length > 0) {
			const select = appendOption("yearMonth", data.list, record => option(record.yearMonth));
			select.value = formatYearMonth(new Date());
			searchSendFaxList(select);
		}
	});
}
function searchSendFaxList(select) {
	post('/meisai/send_fax/list', {yearMonth: select.value}, data => {
		initList(data.list);
		var tbody = document.createElement("tbody");
		for (let key in data.list) {
			var sendFax = data.list[key];
			let row = createRow('meisaiFaxSendListTemplate');
			let tds = row.querySelectorAll("td");
			tds[0].append(sendFax.startDatetime);
			tds[1].append(sendFax.outputName);
			tds[2].append(blank(sendFax.sentDatetime));
			tds[3].append(blank(sendFax.sentPage));
			tbody.append(row);
		}
		querySelectorAll("#content .uk-table tbody")[1].replaceWith(tbody);
	});
}
function routeLabel(logicalOperation, route) {
	var label = "";
	if (route.entrance != null) {
		label += "(自)" + route.entrance + " ";
	}
	if (route.exit != null) {
		label += "(至)" + route.exit + " ";
	}
	label += logicalOperation[route.logicalOperation];

  return label;
}
function dayLabel(day) {
	if (day > 0) {
		return day + "日";
	}
	switch (day) {
		case 0:
			return "月末日";
		case -1:
			return "月末日前日";
		case -2:
			return "月末日前々日";
	}
}
function descriptionLabel(from, to, extraction, labelFunc) {
	if (from == to) {
		return labelFunc(to) + "のデータを"
		 + labelFunc(extraction) + "に出力";
	}
	return labelFunc(from) + "から"
	+ labelFunc(to) + "のデータを"
	 + labelFunc(extraction) + "に出力します";
}
const dayOfWeekJson = {
	"MONDAY":"月曜日",
	"TUESDAY":"火曜日",
	"WEDNESDAY":"水曜日",
	"THURSDAY":"木曜日",
	"FRIDAY":"金曜日",
	"SATURDAY":"土曜日",
	"SUNDAY":"日曜日",
};
function dayOfWeekLabel(dayOfWeek) {
	return dayOfWeekJson[dayOfWeek];
}
function dispInquiry() {
	contentTemplate('inquiryTemplate');
}
// function changeHidden(edits) {
// 	for (let key = 0; key < edits.length; key++) {
// 		if (edits[key].getAttribute("hidden") != null) {
// 			edits[key].removeAttribute("hidden");
// 		} else {
// 			edits[key].setAttribute("hidden", null);
// 		}
// 	}
// }
function defaultEdit() {
	const edit = idElement('edit');
	if (edit.checked) {
		edit.checked = false;
		UIkit.toggle(edit).toggle();
	}
}
function dispResetPassword() {
	articleTemplate('resetPasswordTemplate');
}
function dispActivateResetPassword() {
	articleTemplate('activateResetPasswordTemplate');
	var search = location.search;
	if (search.startsWith("?token=")) {
		idElement("token").value = search.substring(7);
		history.pushState(null, null, location.pathname);
	}
}