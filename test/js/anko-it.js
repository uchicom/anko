// (C) 2022 uchicom
function it(url, tests, preProcess, assertResultKey) {
    const iframe = create("iframe");
    iframe.setAttribute("src", url);
    iframe.onload = async () => {
      const ifWindow = iframe.contentWindow;
      // テスト開始
      console.log("it start");
      // テスト実施
      await doTest(ifWindow, tests, assertResultKey == null ? "result" : assertResultKey);
      document.body.removeChild(iframe);
      // レポート
      preProcess();
    };
    document.body.append(iframe);
  }
  
  // テスト実施
  async function doTest(ifWindow, tests,assertResultKey) {
    for (let i = 0; i < tests.length; i++) {
      const test = tests[i];
      console.log(test.name);
      test.action(ifWindow);
      await wait(test.wait);
      test[assertResultKey] = test.assert(ifWindow);
    }
  }
   
  function wait(msec) {
    return new Promise(resolve => setTimeout(resolve, msec));
  }
  