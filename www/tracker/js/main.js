var router;

// 戻る動作
window.addEventListener('popstate', function(e) {
	const keyId = getKeyId(location.pathname);
	const route = router[keyId.key];
	if (route) {
		route(keyId.id);
	}
});

// 画面表示初期設定
function init() {
	router = {
		"/tracker/user/mypage": dispMyPage,
		"/tracker/user/issue/list": dispIssue,
		"/tracker/user/issue/register": dispIssueRegister,
		"/tracker/user/issue/update": dispIssueUpdate,
		"/tracker/user/issue/{id}": dispIssueView,
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
		postJsonUrl('/tracker/api' + path, selector, func, errorFunc, confirmMessage, isArray);
	} else {
		postJsonData('/tracker/api' + path, selector, func, errorFunc, confirmMessage, isArray);
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
		if (location.pathname == "/tracker/user/signup") { //サインアップ
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
		display("/tracker/user/mypage");
	}
}

// ログイン画面
function dispLoginView() {
	history.pushState(null, null, '/tracker/user/');
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
function dispIssue() {
	contentTemplate('issueTemplate');
	post('/issue/list', null, data => {
		initList(data.list);
		const tbody = document.createElement("tbody");
		for (let key in data.list) {
			const record = data.list[key];
			const row = createRow('issueTemplate');
			const tds = row.querySelectorAll("td");
			const as = row.querySelectorAll("a");
			as[0].setAttribute("href", `/tracker/user/issue/${record.id}`); // リンクなのでページ遷移
			as[0].append(record.id);
			as[1].setAttribute("onclick", `display('/tracker/user/issue/${record.id}')`); // クリックイベントによる画面切り替えなのでページ遷移しない
			as[1].append(record.subject);
			row.querySelector("pre").append(record.detail);
			tbody.append(row);
		}
		querySelector("#content tbody").replaceWith(tbody);
	});
}

// メモ登録画面
function dispIssueRegister() {
	contentTemplate('issueRegisterTemplate');
}

// メモ編集表示
function dispIssueUpdate() {
	contentTemplate('issueUpdateTemplate', form => {
		post('/issue/list', null, data => {
			initList(data.list);
			const tbody = document.createElement("tbody");
			for (let key in data.list) {
				const record = data.list[key];
				const row = createRow('issueUpdateTemplate');
				row.querySelector("td").append(record.id);
				const inputs = row.querySelectorAll("input,textarea");
				inputs[0].value = record.id;
				inputs[1].value = record.subject;
				inputs[2].value = record.detail;
				tbody.append(row);
			}
			form.querySelector("tbody").replaceWith(tbody);
			querySelector("button").disabled = data.list.length == 0;
			attachValidattion(form, labelConsumer);
		});
	});
}
function dispIssueView(issueId) {
	contentTemplate('issueViewTemplate');
	
	post('/issue/get', {issueId:issueId}, data => {
		const tds = document.querySelectorAll("#content table td");
		tds[0].append(data.id);
		tds[1].append(data.subject);
		tds[2].append(data.detail);
	});
}
