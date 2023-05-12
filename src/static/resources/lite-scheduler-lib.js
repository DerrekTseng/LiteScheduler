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
			DocumentUtils.removeFromTop(e);
		});
		e.querySelector("[data-title-close]").addEventListener('click', () => {
			callback();
		});
		e.querySelector("[data-footer-close]").addEventListener('click', () => {
			callback();
		});

		DocumentUtils.appendToTop(e);

		new DocumentUtils.bootstrap.Modal(e).show();
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
			DocumentUtils.removeFromTop(e);
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

		DocumentUtils.appendToTop(e);

		new DocumentUtils.bootstrap.Modal(e).show();
	},
	window: (options = {}) => {
		let title = options.title || "";
		let data = options.data || {};
		let url = options.url || "";
		let callback = options.callback || function() { };

		url = url += TextUtils.objectToQuerystring(data);
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
			DocumentUtils.removeFromTop(e);
			dialogWindowCallbacks.delete(id);
		});

		e.querySelector("[data-title-close]").addEventListener('click', () => {
			callback();
		});

		DocumentUtils.appendToTop(e);

		let windowModal = new DocumentUtils.bootstrap.Modal(e);
		windowModal.show();

		dialogWindowCallbacks.set(id, callback);

	},
	closeWindow(e, data) {
		let id = e.frameElement.id;
		let windowModal = DocumentUtils.bootstrap.Modal.getInstance(top.document.getElementById("modal-" + id));
		dialogWindowCallbacks.get(id)(data);
		windowModal.hide();
	}
}

var DocumentUtils = {
	appendToTop: (e) => {
		top.document.body.appendChild(e);
	},
	removeFromTop: (e) => {
		top.document.body.removeChild(e);
	},
	bootstrap: top.bootstrap,
	ready: (callback = () => { }) => {
		window.onload = callback;
	},
	isFunction(functionToCheck) {
		return functionToCheck && {}.toString.call(functionToCheck) === '[object Function]';
	}
}

var ElementUtils = top.ElementUtils || {
	createElement: (html) => {
		let template = document.createElement('template');
		html = html.trim();
		template.innerHTML = html;
		return template.content.firstChild;
	},
	selectOptions: (select, options = [], selectedValue) => {
		let buffer = []
		options.forEach(option => {
			let selected = option.value == selectedValue ? "selected" : "";
			buffer.push(`<option ${selected} value="${option.value}">${option.text}</option>`);
		});
		select.innerHTML = buffer.join("");
	},
	cronTimeSelections: (selections = {}) => {
		let monthOptions = [];
		monthOptions.push({ text: "未指定", value: "-1" });
		for (let i = 1; i < 13; i++) {
			monthOptions.push({ text: i, value: i });
		}

		let dayOptions = [];
		dayOptions.push({ text: "未指定", value: "-1" });
		for (let i = 1; i < 32; i++) {
			dayOptions.push({ text: i, value: i });
		}

		let hourOptions = [];
		hourOptions.push({ text: "未指定", value: "-1" });
		for (let i = 0; i < 24; i++) {
			hourOptions.push({ text: i, value: i });
		}

		let minuteOptions = [];
		minuteOptions.push({ text: "未指定", value: "-1" });
		for (let i = 0; i < 60; i++) {
			minuteOptions.push({ text: i, value: i });
		}

		let secondOptions = [];
		secondOptions.push({ text: "未指定", value: "-1" });
		for (let i = 0; i < 60; i++) {
			secondOptions.push({ text: i, value: i });
		}

		ElementUtils.selectOptions(selections.month.dom, monthOptions, selections.month.value);
		ElementUtils.selectOptions(selections.day.dom, monthOptions, selections.day.value);
		ElementUtils.selectOptions(selections.hour.dom, monthOptions, selections.hour.value);
		ElementUtils.selectOptions(selections.minute.dom, monthOptions, selections.minute.value);
		ElementUtils.selectOptions(selections.second.dom, monthOptions, selections.second.value);
	},
	renderTable: (options = {}) => {
		let $table = options.table || ElementUtils.createElement("<table></table>");
		let captions = options.captions || [];
		let data = options.data || [];
		let thead = options.thead || [];
		let tbody = options.tbody || [];
		let treach = options.treach || function() { };

		let captionsBuffer = []
		captions.forEach(item => {
			captionsBuffer.push(`<caption style="caption-side: ${item.side}">${item.text}</caption>`);
		});
		$table.innerHTML = captionsBuffer.join("");

		let theadBuffer = [];
		theadBuffer.push("<thead>");
		theadBuffer.push("<tr>");
		thead.forEach(item => {
			let _clazz = item.clazz ? `class="${item.clazz}"` : "";
			let _style = item.style ? `style="${item.style}"` : "";
			theadBuffer.push(`<th ${_clazz} ${_style}>${item.html}</th>`);
		});
		theadBuffer.push("</tr>");
		theadBuffer.push("</thead>");
		$table.insertAdjacentHTML('beforeend', theadBuffer.join(""));

		let $tbody = ElementUtils.createElement("<tbody></tbody>");
		data.forEach(item => {
			let $tr = ElementUtils.createElement("<tr></tr>");
			let tdBuffer = [];
			tbody.forEach((_item) => {
				let _clazz = _item.clazz ? `class="${_item.clazz}"` : "";
				let _style = _item.style ? `style="${_item.style}"` : "";

				let _html;

				if (_item.html) {
					_html = _item.html;
				} else if (DocumentUtils.isFunction(_item.parse)) {
					_html = _item.parse(item);
				} else {
					_html = "";
				}

				let html = `<td ${_clazz} ${_style}>${_html}</td>`;
				tdBuffer.push(TextUtils.tranPattern(html, item));
			});
			$tr.innerHTML = tdBuffer.join("");
			treach(item, $tr);
			$tbody.appendChild($tr);
		});
		$table.appendChild($tbody);
	},
	iconButtonHtml: (attrs, type, icon) => {
		return `<button ${attrs} type="button" class="btn btn-${type}"><i class="bi bi-${icon}"></i></button>`;
	},
	textButtonHtml: (attrs, type, text) => {
		return `<button ${attrs} type="button" class="btn btn-${type}">${text}</button>`;
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

		DocumentUtils.appendToTop(e);

		let timeout = TimeoutUtils.setTimeout(function() {
			DocumentUtils.removeFromTop(e);
		}, 3000);

		e.addEventListener('click', () => {
			DocumentUtils.removeFromTop(e);
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
	tranPattern(text, item) {
		let key;
		Object.keys(item).forEach(function(k) {
			key = "@{" + k + "}";
			while (text.includes(key)) {
				if (item[k] || item[k] == 0) {
					text = text.replace(key, item[k]);
				} else {
					text = text.replace(key, "");
				}
			}
			key = "@{" + k + ":date}";
			while (text.includes(key)) {
				if (item[k]) {
					let date = new Date(item[k]);
					let formattedDate = `${date.getFullYear()}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}:${date.getSeconds().toString().padStart(2, '0')}`;
					text = text.replace(key, formattedDate);
				} else {
					text = text.replace(key, "");
				}
			}
		});
		return text;
	},
	objectToQuerystring(obj = {}) {
		return Object.keys(obj).filter((key) => obj[key] != undefined && obj[key] != '').reduce((str, key, i) => {
			let delimiter, val;
			delimiter = (i === 0) ? '?' : '&';
			if (Array.isArray(obj[key])) {
				key = encodeURIComponent(key);
				let arrayVar = obj[key].reduce((str, item) => {
					if (typeof item == "object") {
						val = encodeURIComponent(JSON.stringify(item));
					} else {
						val = encodeURIComponent(item);
					}
					return [str, key, '=', val, '&'].join('');
				}, '');
				return [str, delimiter, arrayVar.trimRightString('&')].join('');
			} else {
				key = encodeURIComponent(key);
				if (typeof obj[key] == "object") {
					val = encodeURIComponent(JSON.stringify(obj[key]));
				} else {
					val = encodeURIComponent(obj[key]);
				}
				return [str, delimiter, key, '=', val].join('');
			}
		}, '');
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