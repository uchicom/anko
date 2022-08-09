// (C) 2022 uchicom
function postJson(url, assignHeader, payload, preProcess, process, errorProcess, postProcess) {
    if (preProcess) preProcess();
    fetch(url, {
        method: "POST",
        headers:  Object.assign({'Content-Type': 'application/json; charset=utf-8'},
            assignHeader),
        body: JSON.stringify(payload)
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
