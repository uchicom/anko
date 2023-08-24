// (C) 2022 uchicom
function ut(url, mockjs, tests, preProcess, assertResultKey) {
    const iframe = create("iframe");
    iframe.setAttribute("src", url);
    iframe.onload = async () => {
      const ifWindow = iframe.contentWindow;
  
      const script = ifWindow.create("script");
      script.src = mockjs;
      script.onload= async ()=>{
  
        // テスト開始
        console.log("ut start");
        // テスト実施
        await doTest(ifWindow, tests, assertResultKey == null ? "result" : assertResultKey);
        document.body.removeChild(iframe);
        // レポート
        preProcess();
  
      };
      ifWindow.document.body.append(script);
    };
    document.body.append(iframe);
  }
  // テスト実施
  async function doTest(ifWindow, tests, assertResultKey) {
    for (let i = 0; i < tests.length; i++) {
      const test = tests[i];
      console.log(test.name);
      ifWindow.preset(test.path, assertResultKey);
      test.action(ifWindow);
    }
  }
  