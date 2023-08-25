// (C) 2022 uchicom
function postJson(url, assignHeader, payload, preProcess, process, errorProcess, postProcess) {
  mockJson(url, assignHeader, payload, preProcess, process, errorProcess, postProcess);
}
function getJson(url, assignHeader, payload, preProcess, process, errorProcess, postProcess) {
  mockJson(url, assignHeader, payload, preProcess, process, errorProcess, postProcess);
}
function mockJson(url, assignHeader, payload, preProcess, process, errorProcess, postProcess) {
    if (path == null) {
      if (preProcess) preProcess();
      try {
        if (process) process(payload);
      } catch (error) {
        if (errorProcess) errorProcess(error);
      } finally {
        if (postProcess) postProcess();
      }
      return;
    }
  
    var test = path[url];
    if (test) {
      if (preProcess) preProcess();
      try {
      if (process && test.mock) {
        process(test.mock);
      }
    } catch (error) {
        if (errorProcess) errorProcess(error);
    } finally {
        if (postProcess) postProcess();
    }
    if (test.assert) {
        let results = [];
        for (let i = 0; i < test.assert.length; i++) {
          results.push(test.assert[i](payload, window));
        }
        test[assertResultKey] = results;
      }
    }
}
let tests = {};
let assertResultKey;
let path;
function preset(pathdata, assertResultKeydata) {
  path = pathdata;
  assertResultKey = assertResultKeydata == null ? "result" : assertResultKeydata;
}
