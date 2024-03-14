function _loadExternalJS(FILE_URL = "", async = true, type = "text/javascript"){
	// src: https://www.educative.io/answers/how-to-dynamically-load-a-js-file-in-javascript
	return new Promise((resolve, reject) => {
		try {
			var scriptEle = document.createElement("script");
			scriptEle.type = type;
			scriptEle.async = async;
			scriptEle.src =FILE_URL;

			scriptEle.addEventListener("load", (ev) => {
				resolve({ status: true });
			});

			scriptEle.addEventListener("error", (ev) => {
				reject({
					status: false,
					message: `Failed to load the script ${FILE_URL}`
				});
			});

			document.body.appendChild(scriptEle);
		} catch (error) {
			reject(error);
		}
	});
};

function _initScanLinkSharerInputs() {
  window._linkSharer = {};
	window._linkSharer.inputEls = Array.from(
      document.querySelectorAll('[data-link-sharer]')
    );
}

function _convertInputsToJSON(inputs){
  var json = {};
  inputs.map(function(el){
    if(el){
      json[el.id] = el.value;
    }
  })
  return json;
}

function _generateSharableUrlOutput(elSelector){
  /**
   * for this to work, add attribute `data-link-sharer="any"` to any inputs you targeted
   */
  var inputsJson = _convertInputsToJSON(window._linkSharer.inputEls);
  var jsonCompressor = JsonUrl('lzw');
  jsonCompressor.compress(inputsJson)
    .then(function(encodedCompressedJson){
      var sharableUrl = window.location.origin + window.location.pathname + '?s=' + encodedCompressedJson;
      console.log("sharableUrl: ",sharableUrl,sharableUrl.length);
      if(sharableUrl.length > 2000){
        alert('Note: Sharable URL max length 2000 char exceeded, the URL may not always work with some browser');
        // return false;
      }
      document.querySelector(elSelector).value = sharableUrl;
    });
}

function _insertSharableFormElementToHTML(){
  var mainEl = document.querySelector('main') ?? document.querySelector('body');
  mainEl.insertAdjacentHTML(
    "beforeend",
    `
  <details>
    <summary>Sharable URL</summary>
    <br>
    <p><small>Useful to generate a sharable URL (when it opened, will auto-fill the form with the exact same value as you have above), click below, then copy-paste the long URL to share to people.</small></p>
    <article>
      <label><a onclick="_generateSharableUrlOutput('#sharableUrl');" href="#/">Generate Sharable URL:</a></label>
      <input type="text" id="sharableUrl" placeholder="Click above to generate...">
    </article>
  </details>
    `
  );
}

function _restoreFromSQueryParamToInputValues(){
  var urlParams = new URLSearchParams(window.location.search);
  var encodedCompressedInputsJson = urlParams.get('s');
  if(encodedCompressedInputsJson){
    var jsonCompressor = JsonUrl('lzw');
    jsonCompressor.decompress(encodedCompressedInputsJson)
      .then(function(inputsJson){
        for (const key in inputsJson) {
          var el = document.querySelector('#'+key);
          if(el){
            el.value = inputsJson[key];
            el.dispatchEvent(new Event('keyup'));
          }
        }
      });
  }
}

function _mainCommonScript(){
  _initScanLinkSharerInputs();
	_loadExternalJS('https://cdn.jsdelivr.net/npm/json-url@3.1.0/dist/browser/json-url-single.min.js')
	.then(function(){
      _insertSharableFormElementToHTML();
      _restoreFromSQueryParamToInputValues();
		});
}
_mainCommonScript();
