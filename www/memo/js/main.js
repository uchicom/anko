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
		"/memo/user/update": dispMemoUpdate,
	};
}

// 画面表示
const display = pathname => {
	const canvas = UIkit.offcanvas('#offcanvas-slide');
  if (canvas) {
    canvas.hide();
  }
	const route = router[pathname];
	if (route) {
		clearErrorMessage();
		route();
		history.pushState(null, null, pathname);
		return true;
	}
	return false;
}


// ポスト
function post(path, selector, func, errorFunc, confirmMessage, isArray) {
	if (!selector || isString(selector)) {
		postJsonUrl('/memo/api' + path, selector, func, errorFunc, confirmMessage, isArray);
	} else {
		postJsonData('/memo/api' + path, selector, func, errorFunc, confirmMessage, isArray);
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
		if (location.pathname == "/memo/user/signup") { //サインアップ
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
		display("/memo/user/mypage");
	}
}

// ログイン画面
function dispLoginView() {
	history.pushState(null, null, '/memo/user/');
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
			UIkit.nav('#offcanvas-slide').$destroy();
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
function dispMemo() {
	contentTemplate('memoTemplate');
	post('/memo/list', null, data => {
		initList(data.list);
		const tbody = document.createElement("tbody");
		for (let key in data.list) {
			const record = data.list[key];
			const row = createRow('memoTemplate');
			const tds = row.querySelectorAll("td");
			tds[0].append(record.id);
			tds[1].append(record.title);
			row.querySelector("pre").append(record.body);
			tbody.append(row);
		}
		querySelector("#content .uk-table tbody").replaceWith(tbody);
	});
}

// メモ登録画面
function dispMemoRegister() {
	contentTemplate('memoRegisterTemplate');
}

// メモ編集表示
function dispMemoUpdate() {
	contentTemplate('memoUpdateTemplate');
	post('/memo/list', null, data => {
		initList(data.list);
		const tbody = document.createElement("tbody");
		for (let key in data.list) {
			const record = data.list[key];
			const row = createRow('memoUpdateTemplate');
			row.querySelector("td").append(record.id);
			const inputs = row.querySelectorAll("input,textarea");
			inputs[0].value = record.id;
			inputs[1].value = record.title;
			inputs[2].value = record.body;
			tbody.append(row);
		}
		const form = querySelector("#content form");
		form.querySelector(".uk-table tbody").replaceWith(tbody);
		attachValidattion(form, labelConsumer);
	});
}
