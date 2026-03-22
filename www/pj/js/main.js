var router;

// 戻る動作
window.addEventListener('popstate', function() {
	const keyId = getKeyId(location.pathname);
	const route = router[keyId.key];
	if (route) {
		route(keyId.id);
	}
});

// 画面表示初期設定
function init() {
	router = {
		"/pj/user/mypage": dispMyPage,
		"/pj/user/project/list": dispproject,
		"/pj/user/project/register": dispprojectRegister,
		"/pj/user/project/update": dispprojectUpdate,
		"/pj/user/project/{id}": dispprojectView,
		"/pj/user/customer/list": dispCustomer,
		"/pj/user/customer/register": dispCustomerRegister,
		"/pj/user/customer/update": dispCustomerUpdate,
		"/pj/user/task/list": dispTask,
		"/pj/user/task/register": dispTaskRegister,
		"/pj/user/task/update": dispTaskUpdate,
	};
}

// 画面表示
const display = pathname => {
	const keyId = getKeyId(pathname);
	const route = router[keyId.key];
	if (route) {
		route(keyId.id);
		history.pushState(null, null, pathname);
		return true;
	}
	return false;
}

function getKeyId(pathname) {
	const matches = pathname.match(/^(.+)\/([0-9]+)$/);
	let key = pathname;
	let id = null;
	if (matches) {
		key = matches[1] + "/{id}";
		id = matches[2];
	}
	return {key:key, id:id};
}


// ポスト
function post(path, selector, func, errorFunc, confirmMessage, isArray) {
	if (!selector || isString(selector)) {
		postJsonUrl('/pj/api' + path, selector, func, errorFunc, confirmMessage, isArray);
	} else {
		postJsonData('/pj/api' + path, selector, func, errorFunc, confirmMessage, isArray);
	}
}

// ログイン
dispLogin = () => {
	// 認証済み
	post( '/account/check/login',  null, data => {
		if (data.result == "OK") {
			dispTop();
			return;
		}
		// 未認証
		if (location.pathname == "/pj/user/signup") { //サインアップ
			dispAccountRegister();
		} else {
			articleTemplate('loginTemplate');
		}
	});
}

// トップ画面
function dispTop() {
	init();
	articleTemplate('topTemplate');
	const pathname = location.pathname;
	
	if (!display(pathname)) {
		display("/pj/user/mypage");
	}
}

// ログイン画面
function dispLoginView() {
	history.pushState(null, null, '/pj/user/');
	dispLogin();
}

// ログイン
function login() {
	post( '/account/login',  'form#login', data => {
		if (data.result == 'OK') {
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

// ログアウト
function logout() {
	if (confirm('ログアウトしますか？')) {
		logoutProcess();
	}
}
function logoutProcess() {
	post( '/account/logout',  null, data => {
		if (data.result == "OK") {
			dispLoginView();
		}
	});
}

// アカウント登録
dispAccountRegister = () => {
	articleTemplate('accountRegisterTemplate');
}

function dispMyPage() {
	contentTemplate('mypageTemplate');
}

// メモ一覧表示
function dispproject() {
	contentTemplate('projectTemplate');
	post('/project/list', null, data => {
		initList(data.list);
		const tbody = document.createElement("tbody");
		for (let key in data.list) {
			const record = data.list[key];
			const row = createRow('projectTemplate');
			const as = row.querySelectorAll("a");
			as[0].setAttribute("href", `/pj/user/project/${record.id}`); // リンクなのでページ遷移
			as[0].append(record.id);
			as[1].setAttribute("onclick", `display('/pj/user/project/${record.id}')`); // クリックイベントによる画面切り替えなのでページ遷移しない
			as[1].append(record.subject);
			row.querySelector("pre").append(record.description);
			tbody.append(row);
		}
		querySelector("#content tbody").replaceWith(tbody);
	});
}

// メモ登録画面
function dispprojectRegister() {
	contentTemplate('projectRegisterTemplate');
	post('/customer/list', null, data => {
		appendOption("customerId", data.list, record => option(record.id, record.company_name))
	});
}

// メモ編集表示
function dispprojectUpdate() {
	contentTemplate('projectUpdateTemplate', form => {
		post('/project/list', null, data => {
			initList(data.list);
			const tbody = document.createElement("tbody");
			for (let key in data.list) {
				const record = data.list[key];
				const row = createRow('projectUpdateTemplate');
				row.querySelector("span").append(record.id);
				const inputs = row.querySelectorAll("input,textarea");
				inputs[0].value = record.id;
				inputs[1].value = blank(record.subject);
				inputs[2].value = blank(record.start_schedule_date);
				inputs[3].value = blank(record.end_schedule_date);
				inputs[4].value = blank(record.description);
				tbody.append(row);
			}
			form.querySelector("tbody").replaceWith(tbody);
			querySelector("button").disabled = data.list.length == 0;
			attachValidattion(form, labelConsumer);
		});
	});
}
// 顧客一覧表示
function dispCustomer() {
	contentTemplate('customerTemplate');
	post('/customer/list', null, data => {
		initList(data.list);
		const tbody = document.createElement("tbody");
		for (let key in data.list) {
			const record = data.list[key];
			const row = createRow('customerTemplate');
			const tds = row.querySelectorAll("td");
			tds[0].append(record.id);
			tds[1].append(blank(record.company_name));
			tds[2].append(blank(record.pic_name));
			tds[3].append(blank(record.email_address));
			tds[4].append(blank(record.telephon_number));
			tbody.append(row);
		}
		querySelector("#content tbody").replaceWith(tbody);
	});
}

// 顧客登録画面
function dispCustomerRegister() {
	contentTemplate('customerRegisterTemplate');
}

// 顧客編集表示
function dispCustomerUpdate() {
	contentTemplate('customerUpdateTemplate', form => {
		post('/customer/list', null, data => {
			initList(data.list);
			const tbody = document.createElement("tbody");
			for (let key in data.list) {
				const record = data.list[key];
				const row = createRow('customerUpdateTemplate');
				const inputs = row.querySelectorAll("input");
				inputs[0].value = record.id;
				row.querySelector("span").append(record.id);
				inputs[1].value = blank(record.company_name);
				inputs[2].value = blank(record.pic_name);
				inputs[3].value = blank(record.email_address);
				inputs[4].value = blank(record.telephon_number);
				inputs[5].value = blank(record.fax_number);
				inputs[6].value = blank(record.address);
				inputs[7].value = blank(record.building);
				tbody.append(row);
			}
			form.querySelector("tbody").replaceWith(tbody);
			querySelector("button").disabled = data.list.length == 0;
			attachValidattion(form, labelConsumer);
		});
	});
}

// datetime-local input 用フォーマット変換 ("yyyy-MM-dd HH:mm:ss" → "yyyy-MM-ddTHH:mm")
function toDatetimeLocal(str) {
	if (!str) return '';
	return str.substring(0, 10) + 'T' + str.substring(11, 16);
}

// タスク一覧表示
function dispTask() {
	contentTemplate('taskTemplate');
	post('/project/list', null, data => {
		appendOption("taskProjectId", data.list, record => option(record.id, record.subject));
		if (data.list && data.list.length > 0) {
			loadTaskList(data.list[0].id);
		}
	});
}

// タスク一覧読み込み
function loadTaskList(projectId) {
	post('/task/list', {projectId: projectId}, data => {
		initList(data.list);
		const tbody = document.createElement("tbody");
		for (let key in data.list) {
			const record = data.list[key];
			const row = createRow('taskTemplate');
			const tds = row.querySelectorAll("td");
			tds[0].append(record.id);
			tds[1].append(blank(record.subject));
			tds[2].append(blank(record.priority));
			tds[3].append(blank(record.progress));
			tds[4].append(blank(record.start_datetime));
			tds[5].append(blank(record.complete_datetime));
			tbody.append(row);
		}
		querySelector("#content tbody").replaceWith(tbody);
	});
}

// タスク登録画面
function dispTaskRegister() {
	contentTemplate('taskRegisterTemplate');
	post('/project/list', null, data => {
		appendOption("projectId", data.list, record => option(record.id, record.subject));
	});
}

// タスク編集表示
function dispTaskUpdate() {
	contentTemplate('taskUpdateTemplate', form => {
		post('/project/list', null, data => {
			appendOption("taskUpdateProjectId", data.list, record => option(record.id, record.subject));
			attachValidattion(form, labelConsumer);
			if (data.list && data.list.length > 0) {
				loadTaskUpdateList(data.list[0].id);
			}
		});
	});
}

// タスク編集一覧読み込み
function loadTaskUpdateList(projectId) {
	post('/task/list', {projectId: projectId}, data => {
		initList(data.list);
		const tbody = document.createElement("tbody");
		for (let key in data.list) {
			const record = data.list[key];
			const row = createRow('taskUpdateTemplate');
			const inputs = row.querySelectorAll("input,textarea");
			inputs[0].value = record.id;
			inputs[1].value = record.project_id;
			row.querySelector("span").append(record.id);
			inputs[2].value = blank(record.subject);
			inputs[3].value = blank(record.priority);
			inputs[4].value = blank(record.cost);
			inputs[5].value = blank(record.progress);
			inputs[6].value = toDatetimeLocal(record.start_datetime);
			inputs[7].value = toDatetimeLocal(record.complete_datetime);
			row.querySelector("textarea").value = blank(record.description);
			tbody.append(row);
		}
		querySelector("#content tbody").replaceWith(tbody);
		querySelector("button").disabled = data.list.length == 0;
	});
}

function dispprojectView(projectId) {
	contentTemplate('projectViewTemplate');
	
	post('/project/get', {projectId:projectId}, data => {
		const tds = document.querySelectorAll("#content table td");
		tds[0].append(data.id);
		tds[1].append(data.subject);
		tds[2].append(data.description);
	});
}
