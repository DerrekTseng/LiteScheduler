const DialogUtils = {
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
			document.body.removeChild(e);
		});
		e.querySelector("[data-title-close]").addEventListener('click', () => {
			callback();
		});
		e.querySelector("[data-footer-close]").addEventListener('click', () => {
			callback();
		});

		document.body.appendChild(e);

		new bootstrap.Modal(e).show();
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
			document.body.removeChild(e);
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

		document.body.appendChild(e);

		new bootstrap.Modal(e).show();
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
			document.body.removeChild(e);
		});

		e.querySelector("[data-title-close]").addEventListener('click', () => {
			callback();
		});

		document.body.appendChild(e);

		let windowModal = new bootstrap.Modal(e);
		windowModal.show();
	},
	closeWindow() {
		let windowModal = top.bootstrap.Modal.getInstance(top.document.getElementById("modal-" + window.frameElement.id));
		windowModal.hide();
	}
}

const ElementUtils = {
	createElement: (html) => {
		let template = document.createElement('template');
		html = html.trim();
		template.innerHTML = html;
		return template.content.firstChild;
	}
}

const PromptUtils = {
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
			<div class="alert ${alertClass} prompt" role="alert">
			  ${msg}
			</div> 
		`;
		let e = ElementUtils.createElement(html);
		document.body.appendChild(e);
		let timeout = setTimeout(() => {
			document.body.removeChild(e);
		}, 3000);
		e.addEventListener('click', () => {
			document.body.removeChild(e);
			clearTimeout(timeout);
		});
	}
}

const TextUtils = {
	randonString: (length = 0) => {
		let result = [];
		let characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
		let charactersLength = characters.length;
		for (let i = 0; i < length; i++) {
			result.push(characters.charAt(Math.floor(Math.random() * charactersLength)));
		}
		return result.join('');
	}
}