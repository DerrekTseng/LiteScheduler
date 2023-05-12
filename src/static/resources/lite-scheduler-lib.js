var dialogWindowCallbacks = top.dialogWindowCallbacks || new Map();

var TimeoutUtils = top.TimeoutUtils || {
	setTimeout: (callback, interval) => {
		return setTimeout(callback, interval);
	},
	clearTimeout: (id) => {
		clearTimeout(id);
	}
}

var DialogUtils = top.DialogUtils || {
	alert: (options = {}) => {
		let title = options.title || "";
		let text = options.text || "";
		let okText = options.okText || "OK";
		let callback = options.callback || function() { };

		let html = `
			<div class="modal fade" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
			  <div class="modal-dialog">
			    <div class="modal-content">
			      <div class="modal-header">
			        <h5 class="modal-title">${title}</h5>
			        <button data-title-close type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			      </div>
			      <div class="modal-body">
			        ${text}
			      </div>
			      <div class="modal-footer modal-footer-center">
			        <button data-footer-close type="button" class="btn btn-warning" data-bs-dismiss="modal">${okText}</button>
			      </div>
			    </div>
			  </div>
			</div>
		`;
		let e = ElementUtils.createElement(html);

		e.addEventListener('hidden.bs.modal', () => {
			ElementUtils.removeFromTop(e);
		});
		e.querySelector("[data-title-close]").addEventListener('click', () => {
			callback();
		});
		e.querySelector("[data-footer-close]").addEventListener('click', () => {
			callback();
		});

		ElementUtils.appendToTop(e);

		new ElementUtils.bootstrap.Modal(e).show();
	},
	confirm: (options = {}) => {
		let title = options.title || "";
		let text = options.text || "";
		let cancelText = options.okText || "Cancel";
		let confirmText = options.okText || "Confirm";
		let onCancel = options.onCancel || function() { };
		let onConfirm = options.onConfirm || function() { };

		let html = `
			<div class="modal fade" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
			  <div class="modal-dialog">
			    <div class="modal-content">
			      <div class="modal-header">
			        <h5 class="modal-title">${title}</h5>
			        <button data-title-close type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			      </div>
			      <div class="modal-body">
			        ${text}
			      </div>
			      <div class="modal-footer modal-footer-between">
			        <button data-footer-cancel type="button" class="btn btn-secondary" data-bs-dismiss="modal">${cancelText}</button>
        			<button data-footer-confirm type="button" class="btn btn-success" data-bs-dismiss="modal">${confirmText}</button>
			      </div>
			    </div>
			  </div>
			</div>
		`;

		let e = ElementUtils.createElement(html);

		e.addEventListener('hidden.bs.modal', function() {
			ElementUtils.removeFromTop(e);
		});
		e.querySelector("[data-title-close]").addEventListener('click', () => {
			onCancel();
		});
		e.querySelector("[data-footer-cancel]").addEventListener('click', () => {
			onCancel();
		});
		e.querySelector("[data-footer-confirm]").addEventListener('click', () => {
			onConfirm();
		});

		ElementUtils.appendToTop(e);

		new ElementUtils.bootstrap.Modal(e).show();
	},
	window: (options = {}) => {
		let title = options.title || "";
		let url = options.url || "";
		let callback = options.callback || function() { };

		let id = TextUtils.randonString(16);

		let html = `
			<div id="modal-${id}" class="modal fade" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
			  <div class="modal-dialog modal-lg modal-window">
			    <div class="modal-content">
			      <div class="modal-header">
			        <h5 class="modal-title">${title}</h5>
			        <button data-title-close type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			      </div>
			      <div class="modal-body modal-body-window">
			      	<iframe id="${id}" class="modal-window-iframe" src="${url}"></iframe>
			      </div>
			    </div>
			  </div>
			</div>
		`;

		let e = ElementUtils.createElement(html);

		e.addEventListener('hidden.bs.modal', function() {
			ElementUtils.removeFromTop(e);
			dialogWindowCallbacks.delete(id);
		});

		e.querySelector("[data-title-close]").addEventListener('click', () => {
			callback();
		});

		ElementUtils.appendToTop(e);

		let windowModal = new ElementUtils.bootstrap.Modal(e);
		windowModal.show();

		dialogWindowCallbacks.set(id, callback);

	},
	closeWindow(e, data) {
		let id = e.frameElement.id;
		let windowModal = ElementUtils.bootstrap.Modal.getInstance(top.document.getElementById("modal-" + id));
		dialogWindowCallbacks.get(id)(data);
		windowModal.hide();
	}
}

var ElementUtils = top.ElementUtils || {
	createElement: (html) => {
		let template = document.createElement('template');
		html = html.trim();
		template.innerHTML = html;
		return template.content.firstChild;
	},
	appendToTop: (e) => {
		top.document.body.appendChild(e);
	},
	removeFromTop: (e) => {
		top.document.body.removeChild(e);
	},
	bootstrap: top.bootstrap,
	ready: (callback = () => { }) => {
		window.onload = callback;
	}
}

var PromptUtils = top.PromptUtils || {
	info: (msg) => {
		PromptUtils.prompt("alert-info", msg);
	},
	success: (msg) => {
		PromptUtils.prompt("alert-success", msg);
	},
	warning: (msg) => {
		PromptUtils.prompt("alert-warning", msg);
	},
	error: (msg) => {
		PromptUtils.prompt("alert-danger", msg);
	},
	prompt: (alertClass = "", msg = "") => {

		let html = ` 
			<div class="alert ${alertClass} prompt" role="alert" style="z-index: 2080">
			  ${msg}
			</div> 
		`;

		let e = ElementUtils.createElement(html);

		ElementUtils.appendToTop(e);

		let timeout = TimeoutUtils.setTimeout(function() {
			ElementUtils.removeFromTop(e);
		}, 3000);

		e.addEventListener('click', () => {
			ElementUtils.removeFromTop(e);
			TimeoutUtils.clearTimeout(timeout);
		});
	}
}

var TextUtils = top.TextUtils || {
	randonString: (length = 0) => {
		let result = [];
		let characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
		let charactersLength = characters.length;
		for (let i = 0; i < length; i++) {
			result.push(characters.charAt(Math.floor(Math.random() * charactersLength)));
		}
		return result.join('');
	},
	/**  將字串裡面的 @{key} 轉換成 json 的 value */
	tranPattern(text, item) {
		Object.keys(item).forEach(function(k) {
			let key = "@{" + k + "}";
			while (text.includes(key)) {
				text = text.replace(key, item[k]);
			}
		});
		return text;
	}
}

var HttpUtils = top.HttpUtils || {
	doPost: (options = {}) => {
		let url = options.url || "";
		let data = options.data || {};
		let success = options.success || function() { };
		let error = options.error || function() { };

		fetch(url, {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
				"Accept": "application/json"
			},
			body: JSON.stringify(data)
		}).then((response) => {
			if (response.ok) {
				response.json().then((responseData) => {
					success(responseData, response);
				});
			} else {
				response.json().then((responseData) => {
					PromptUtils.error(responseData.message);
					error(responseData, response);
				});
			}
		}).catch((response) => {
			error(response);
		});
	}
}

var ValidateUtils = {

}