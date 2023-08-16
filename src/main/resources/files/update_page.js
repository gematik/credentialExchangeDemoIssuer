function onUpdateStateChange() {
    if (this.readyState === 4 && this.status === 200) {
        if (JSON.parse(this.responseText).update) {
            return document.location.reload();
        }
    }
}

function checkForUpdate() {
    const request = new XMLHttpRequest();
    request.open('GET', '/medicaloffice/update_status', true);
    request.addEventListener('readystatechange', onUpdateStateChange);
    request.send();
}

setInterval(checkForUpdate, 1000);