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
		let callback = options.callback || (() => { });

		let html = `
			<div class="modal fade" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
			  <div class="modal-dialog modal-dialog-centered">
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
		let cancelText = options.cancelText || "Cancel";
		let confirmText = options.confirmText || "Confirm";
		let onCancel = options.onCancel || (() => { });
		let onConfirm = options.onConfirm || (() => { });

		let html = `
			<div class="modal fade" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
			  <div class="modal-dialog modal-dialog-centered">
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
		let callback = options.callback || (() => { });

		url = url += TextUtils.objectToQuerystring(data);
		let id = TextUtils.randonString(16);

		let html = `
			<div id="modal-${id}" class="modal fade" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
			  <div class="modal-dialog modal-lg modal-dialog-centered modal-window">
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
	isFunction: (functionToCheck) => {
		return functionToCheck && {}.toString.call(functionToCheck) === '[object Function]';
	},
	isNode: (o) => {
		return (
			typeof Node === "object" ? o instanceof Node :
				o && typeof o === "object" && typeof o.nodeType === "number" && typeof o.nodeName === "string"
		);
	},
	isElement: (o) => {
		return (
			typeof HTMLElement === "object" ? o instanceof HTMLElement : //DOM2
				o && typeof o === "object" && o !== null && o.nodeType === 1 && typeof o.nodeName === "string"
		);
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
	renderTable: (options = {}) => {
		let $table = options.table || ElementUtils.createElement("<table></table>");
		let captions = options.captions || [];
		let data = options.data || [];
		let thead = options.thead || [];
		let tbody = options.tbody || [];
		let treach = options.treach || (() => { });

		let captionsBuffer = []
		captions.forEach(item => {
			captionsBuffer.push(`<caption style="caption-side: ${item.side}">${item.text}</caption>`);
		});
		$table.innerHTML = captionsBuffer.join("");
		if(thead.isSorter){
			$table.appendChild(thead.element);
		}else{
			let theadBuffer = [];
			theadBuffer.push("<thead>");
			theadBuffer.push("<tr>");
			thead.forEach(item => {
				let _clazz = item.clazz ? `class="${item.clazz}"` : "";
				let _style = item.style ? `style="${item.style}"` : "";
				let _attrs = item.attrs ? item.attrs : "";
				theadBuffer.push(`<th ${_attrs} ${_clazz} ${_style}>${item.html}</th>`);
			});
			theadBuffer.push("</tr>");
			theadBuffer.push("</thead>");
			$table.insertAdjacentHTML('beforeend', theadBuffer.join(""));
		}
		
		let $tbody = ElementUtils.createElement("<tbody></tbody>");
		if(!data || data.length === 0){
			let $tr = ElementUtils.createElement(`
				<tr>
					<td colspan="${tbody.length}">No Data</td>
				</tr>
			`);
			$tbody.appendChild($tr);
		}else{
			data.forEach(item => {
				let $tr = ElementUtils.createElement("<tr></tr>");
				tbody.forEach((_item) => {
					let _clazz = _item.clazz ? `class="${_item.clazz}"` : "";
					let _style = _item.style ? `style="${_item.style}"` : "";
					let _attrs = _item.attrs ? _item.attrs : "";
					let $td = ElementUtils.createElement(TextUtils.tranPattern(`<td ${_attrs} ${_clazz} ${_style}></td>`, item));
					if (_item.html) {
						$td.innerHTML = TextUtils.tranPattern(_item.html, item);
					} else if (DocumentUtils.isFunction(_item.parse)) {
						let parsed = _item.parse(item);
						if (DocumentUtils.isElement(parsed) || DocumentUtils.isNode(parsed)) {
							$td.appendChild(parsed);
						} else {
							$td.innerHTML = parsed;
						}
					}
					$tr.appendChild($td);
				});
				treach(item, $tr);
				$tbody.appendChild($tr);
			});
		}
		$table.appendChild($tbody);
	},
	renderPager: (options = {}) => {
		let $div = options.div || ElementUtils.createElement("<div style='display: flex; justify-content: space-between;'></div>");
		let onEvent = options.onEvent || (() => { });
		let pageNum = options.pageNum || 0;
		let pageSize = options.pageSize || 10;
		let totalPages = options.totalPages || 0;
		let totalSize = options.totalSize || 0;

		$div.innerHTML = '';

		let $pagination = ElementUtils.createElement('<ul class="pagination"></ul>');

		let $firstBtn = ElementUtils.createElement(
			`
				<li class="page-item ${pageNum === 0 ? 'disabled' : ''}">
			      <a class="page-link none-select">
			        <span aria-hidden="true">&laquo</span>
			      </a>
			    </li>
			`
		);

		let $previousBtn = ElementUtils.createElement(
			`
				<li class="page-item ${pageNum === 0 ? 'disabled' : ''}">
			      <a class="page-link none-select">
			        <span aria-hidden="true">&lsaquo;</span>
			      </a>
			    </li>
			`
		);
		if (pageNum != 0) {
			$firstBtn.addEventListener('click', () => {
				onEvent(0, pageSize)
			});
			$previousBtn.addEventListener('click', () => {
				onEvent(pageNum - 1, pageSize)
			});
		}
		let $nextBtn = ElementUtils.createElement(
			`
				<li class="page-item ${pageNum + 1 >= totalPages ? 'disabled' : ''}">
			      <a class="page-link none-select">
			        <span aria-hidden="true">&rsaquo;</span>
			      </a>
			    </li>
			`
		);
		let $lastBtn = ElementUtils.createElement(
			`
				<li class="page-item ${pageNum + 1 >= totalPages ? 'disabled' : ''}">
			      <a class="page-link none-select">
			        <span aria-hidden="true">&raquo</span>
			      </a>
			    </li>
			`
		);
		if (pageNum + 1 < totalPages) {
			$nextBtn.addEventListener('click', () => {
				onEvent(pageNum + 1, pageSize)
			});
			$lastBtn.addEventListener('click', () => {
				onEvent(totalPages - 1, pageSize)
			});
		}

		let $pageBtns = [];

		const getPages = (totalPages, currentPage) => {
			if (totalPages <= 5) return Array.from(Array(totalPages).keys()).map(r => { return r + 1 });
			let diff = 0;
			const result = [currentPage - 2, currentPage - 1, currentPage, currentPage + 1, currentPage + 2];
			if (result[0] < 3) {
				diff = 1 - result[0];
			}
			if (result.slice(-1) > totalPages - 2) {
				diff = totalPages - result.slice(-1);
			}
			return result.map(r => { return r + diff });
		}

		getPages(totalPages, pageNum + 1).forEach(num => {
			let $pageBtn = ElementUtils.createElement(
				`
				<li class="page-item ${num === pageNum + 1 ? 'disabled' : ''}">
			      <a class="page-link none-select">
			        <span aria-hidden="true">${num}</span>
			      </a>
			    </li>
			`
			);
			if (num !== pageNum + 1) {
				$pageBtn.addEventListener('click', () => {
					onEvent(num - 1, pageSize)
				});
			}
			$pageBtns.push($pageBtn);
		});

		$pagination.appendChild($firstBtn);
		$pagination.appendChild($previousBtn);
		$pageBtns.forEach($pageBtn => {
			$pagination.appendChild($pageBtn);
		});
		$pagination.appendChild($nextBtn);
		$pagination.appendChild($lastBtn);

		let startIndex = totalSize === 0 ? 0 : (pageNum * pageSize) + 1;
		let endIndex = (pageNum + 1) * pageSize > totalSize ? totalSize : (pageNum + 1) * pageSize;

		let $info = ElementUtils.createElement(
			`
			<span>
				第${pageNum + 1}頁 第${startIndex}-${endIndex}筆 共${totalSize}筆 
			<span>
			`
		);
    
		let $pageSizer = ElementUtils.createElement(
			`
				<span>
					每頁
					<input type="number" min="1" max="1000" style="height: 38px; width: 82px; text-align: center;" value="${pageSize}"/>
					筆
				</span>
			`
		);
		
		$pageSizer.getElementsByTagName("input")[0].addEventListener("change", (e) => {
			let val = e.target.value;
			if(val < 1 || val > 1000){
				e.target.value = pageSize;
				val = pageSize;
			}else{
				onEvent(0, val)
			}
		})

		$div.appendChild($pageSizer);
		$div.appendChild($info);
		$div.appendChild($pagination);

	},
	createSortableTableHeader: (options = {}) => {
		let thead = options.thead ? options.thead : [];
		let onEvent = options.onEvent ? options.onEvent : (() => { });
		let sortKey = options.sortKey ? options.sortKey : null;
		let sortDir = options.sortDir ? options.sortDir : null;
		
		let $thead = ElementUtils.createElement(`
			<thead>
				<tr></tr>			
			</thead>
		`);
		
		let $tr = $thead.getElementsByTagName("tr")[0];
		
		thead.forEach(item => {
			let _clazz = item.clazz ? `class="${item.clazz}"` : "";
			let _style = item.style ? `style="${item.style}"` : "";
			let _attrs = item.attrs ? item.attrs : "";
			let _sortKey = item.sortKey ? item.sortKey : null;
			
			let $th;
			
			if(_sortKey){
				_clazz = item.clazz ? `class="${item.clazz} none-select clickable"` : "none-select clickable";
				if(_sortKey === sortKey && sortDir){
					$th = ElementUtils.createElement(`
						<th ${_attrs} ${_clazz} ${_style}>
							${item.html}
							${sortDir === "desc" ? '<i class="bi bi-arrow-down-short"></i>' : '<i class="bi bi-arrow-up-short"></i>'}
						</th>
					`);
				}else{
					$th = ElementUtils.createElement(`
						<th ${_attrs} ${_clazz} ${_style}>
							${item.html}
						</th>
					`);
				}
				$th.addEventListener("click", () => {
					if(_sortKey === sortKey){
						if(sortDir === "desc"){
							onEvent("", "");
						}else if(sortDir === "asc"){
							onEvent(_sortKey, "desc");
						}else{
							onEvent(_sortKey, "asc");
						}
					}else{
						onEvent(_sortKey, "asc");
					}
				})
			}else{
				$th = ElementUtils.createElement(`
					<th ${_attrs} ${_clazz} ${_style}>
						${item.html}
					</th>
				`);
			}
			$tr.appendChild($th);
		});
		
		
		return {
			isSorter: true,
			element: $thead
		}
	},
	iconButtonHtml: (options = {}) => {
		let attrs = options.attrs ? options.attrs : "";
		let color = options.color ? `btn-${options.color}` : "";
		let size = options.size ? `btn-${options.size}` : "";
		let icon = options.icon ? `bi bi-${options.icon}` : "";
		let click = options.click ? `onclick="${options.click}"` : "";
		let clazz = options.clazz ? options.clazz : "";

		return `
			<button ${attrs} type="button" class="btn ${color} ${size} ${clazz}" ${click}>
				<i class="${icon}"></i>
			</button>
		`;
	},
	cronExpressionComponent: (options = {}) => {
		let div = options.div || ElementUtils.createElement("<div></div>");
		let value = options.value || "0 0 0 ? * * * ";
		let id = options.id || "";
		let name = options.name || "";
		let attrs = options.attrs || "";

		let _id = id ? `id="${id}"` : "";
		let _name = name ? `name="${name}"` : "";
		let _attrs = attrs ? attrs : "";

		let $input = ElementUtils.createElement(`
			<input cron-exp-input ${_attrs} type="text" class="form-control text-center" ${_id} ${_name} readonly>
		`);

		let $button = ElementUtils.createElement(`
			<button cron-exp-btn class="btn btn-outline-secondary" type="button">
			  	<i class="bi bi-calendar3"></i>
			  </button>
		`);

		$input.value = value;

		$button.addEventListener("click", () => {
			DialogUtils.window({
				title: "Cron expression generator",
				url: "scheduler/cronExpGenerator",
				data: { cronExp: $input.value },
				callback: (retVal) => {
					if (retVal?.cronExp) {
						$input.value = retVal.cronExp;
					}
				}
			});
		});

		if (div.querySelectorAll("[cron-exp-input]").length) {
			div.querySelector("[cron-exp-input]").replaceWith($input);
		} else {
			div.appendChild($input);
		}

		if (div.querySelectorAll("[cron-exp-btn]").length) {
			div.querySelector("[cron-exp-btn]").replaceWith($button);
		} else {
			div.appendChild($button);
		}

	}
}

var PromptUtils = top.PromptUtils || {
	info: (msg) => {
		PromptUtils.prompt("alert-info", msg, 2000);
	},
	success: (msg) => {
		PromptUtils.prompt("alert-success", msg, 1000);
	},
	warning: (msg) => {
		PromptUtils.prompt("alert-warning", msg, 3000);
	},
	error: (msg) => {
		PromptUtils.prompt("alert-danger", msg, 5000);
	},
	prompt: (alertClass = "", msg = "", interval = 3000) => {

		let html = ` 
			<div class="alert ${alertClass} prompt" role="alert" style="z-index: 2080">
			  ${msg}
			</div> 
		`;

		let e = ElementUtils.createElement(html);

		DocumentUtils.appendToTop(e);

		let timeout = TimeoutUtils.setTimeout(function() {
			DocumentUtils.removeFromTop(e);
		}, interval);

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
	tranPattern: (text, item) => {
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
	objectToQuerystring: (obj = {}) => {
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
	},
	fillHead: (text = " ", appender = " ", length = 0) => {
		let _text = "" + text;
		let _appender = "" + appender;
		while (_text.length < length) {
			_text = _appender + _text;
		}
		return _text;
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
					if (responseData?.succeeded == true) {
						success(responseData);
					} else if (responseData?.succeeded == false) {
						error(responseData);
					}

				});
			} else {
				response.json().then((responseData) => {
					PromptUtils.error(responseData.message);
					error(responseData, response);
				});
			}
		}).catch((e) => {
			PromptUtils.error(`
				<span>Message：</span><br>
				<span class="ms-3">${e.message}</span><br>
				<span>Stack：</span><br>
				<p class="ms-3">${e.stack.replace("\n", "<br>")}</p>
				
			`);
			error(e);
		});
	}
}
