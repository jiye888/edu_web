
        function getRandomImgId() {
            var imgId = '';
            const characters = 'abcdefghijklmnopqrstuvwxyz0123456789';
            for (var i=0; i<12; i++) {
                imgId += characters.charAt(Math.floor(Math.random()*characters.length));
            }
            return imgId;
        }

        function matchContent(id) {
            const regex = new RegExp(id);
            const contentText = document.getElementById('content').textContent;
            const match = contentText.match(regex);
            return match
        }

        function appendHiddenDiv(tag, id) {
            const hiddenDiv = document.createElement('div');
            hiddenDiv.setAttribute("style", "display:none;");
            hiddenDiv.textContent = id;
            tag.appendChild(hiddenDiv);

            return hiddenDiv;
        }

        function setTexts(match, content) {
            const index = match.index;
            const preText = index < 10 ? content.slice(0, index) : content.slice(index - 10, index);
            const afterIndex = index + match[0].length;
            const nextText = (afterIndex + 10) < content.length ? content.slice(afterIndex, afterIndex + 10) : content.slice(afterIndex, content.length);

            return [preText, nextText];
        }

        function findNewImgPosition() {
            const images = document.querySelectorAll('img[name="new"]');
            let imgIndex = [];
            if (images.length !== 0) {
                images.forEach(img => {
                    const dataName = img.getAttribute('data-name');
                    var id = getRandomImgId();
                    var match = matchContent(id);

                    var hiddenDiv = appendHiddenDiv(img, id);

                    var revisedText = document.getElementById('content').textContent;
                    var revisedMatch = revisedText.match(id);

                    if (typeof existImages !== 'undefined' && existImages != null) {
                        existImages.forEach(exist => {
                            if (id === exist.index) {
                                id = getRandomImgId();
                                match = matchContent(id);
                                appendHiddenDiv(img, id);
                            }
                        });
                    }

                    while (match != null && revisedMatch.length == null) {
                        if (match == null && revisedMatch.length === 1) {
                            break;
                        }
                        id = getRandomImgId();
                        match = matchContent(id);
                        hiddenDiv.innerText = id;
                        revisedText = document.getElementById('content').textContent;
                        revisedMatch = revisedText.match(id);
                    }
                    var texts = setTexts(revisedMatch, revisedText);

                    imgIndex.push([dataName, id, texts[0], texts[1]]);
                });
            }
            return imgIndex;
        }

        function findExistImgPosition() {
            const images = document.querySelectorAll('img[name="exist"]');
            let existIndex = [];
            if (images.length !== 0) {
                images.forEach(img => {
                    const dataName = img.getAttribute('data-name');
                    var id = img.getAttribute('data-id');

                    appendHiddenDiv(img, id);

                    var contentText = document.getElementById("content").textContent;

                    var match = matchContent(id);
                    if (match != null && match.length === 1) {
                        var texts = setTexts(match, contentText);
                    } else {
                        var newId = getRandomImgId();
                        var newMatch = matchContent(newId);
                        while (newMatch != null && newMatch.length === 1) {
                            newId = getRandomImgId();
                            newMatch = matchContent(newId);
                        }
                        img.setAttribute('data-id', newId);
                        contentText = document.getElementById("content").textContent;
                        var texts = setTexts(newMatch, contentText);
                    }

                    existIndex.push([dataName, id, texts[0], texts[1]]);
                })
            }
            return existIndex;
        }

        function setImgElement(image, dataURL) {
            const content = document.getElementById('content');
            const imgDiv = document.createElement('div');
            content.appendChild(imgDiv);
            const img = document.createElement('img');
            img.setAttribute("src", dataURL);
            img.setAttribute("draggable", "true");
            img.setAttribute("name", "new");
            img.setAttribute("style", "max-height:600px; max-width:600px;");
            img.setAttribute("data-name", image.name);
            imgDiv.appendChild(img);
            return img;
        }

        function clearText(pTags) {
            for (var tag in pTags) {
                var pElement = document.getElementById(tag);
                if (pElement != null) {
                    pElement.textContent="";
                }
            }
        }

        function checkBindingResult(errorMessage) {
            if (errorMessage.BindingResultError) {
                for (var err in errorMessage) {
                    if (document.getElementById(err)) {
                        var errElement = document.getElementById(err);
                        errElement.textContent = errorMessage[err];
                        errElement.classList.add("alert", "alert-warning");
                    }
                }
            }
        }

        function deleteImage(event) {
            if (event.key === 'Backspace' || event.keyCode === 8 || event.key === 'Delete' || event.keyCode === 46) {
                if (window.getSelection().getRangeAt(0).endContainer != null) {
                    const firstElement = window.getSelection().getRangeAt(0).endContainer.firstElementChild;
                    const firstElementTag = firstElement == null ? "" : firstElement.tagName;
                    const lastElement = window.getSelection().getRangeAt(0).endContainer.lastElementChild;
                    const lastElementTag = lastElement == null ? "" : lastElement.tagName;

                    if (firstElementTag === "IMG" || lastElementTag === "IMG") {
                        if (confirm("사진을 삭제하시겠습니까?")) {
                            for (var i=0; i<imgList.length; i++) {

                                if (imgList[i].name === event.target.getAttribute('data-name')) {
                                    imgList.splice(i, 1);
                                }
                            }
                            const imageLength = document.querySelectorAll('img').length - 1;
                            imageLabel.innerText = "이미지 추가 ("+imageLength+"/10)";
                        } else {
                            event.preventDefault();
                        }
                    }
                }
            }
        }