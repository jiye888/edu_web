    function matchContent(id) {
        const regex = new RegExp(id, "g");
        const contentText = document.getElementById('content').textContent;
        const match = contentText.match(regex);

        return match;
    }

    function getFullNodes(nodeList) {
        var i = 0;
        var length = nodeList.length;
        while (i < nodeList.length) {
            if (nodeList[i].nodeName === 'DIV') {
                var children = Array.from(nodeList[i].childNodes);
                nodeList.splice(i, 1, ...children);
                getFullNodes(nodeList);
            } else if (nodeList[i].nodeName === '#text' && nodeList[i].length === 4 && nodeList[i].data.trim() === '') {
                nodeList.splice(i, 1);
            } else {
                i++;
            }
        }
        return nodeList;
    }

    function getNodeList() {
        const content = document.getElementById('content');
        var nodeList = Array.from(content.childNodes);
        nodeList = getFullNodes(nodeList);
        return nodeList;
    }

    function getImgSeq(position, nodeList) {
        var imgSeq = -1;
        for (var i = position-1; i>=0; i--) {
            if (nodeList[i].nodeName === 'IMG') {
                if (imgSeq < 0) {
                    imgSeq = 0;
                }
                imgSeq ++;
            } else {
                break;
            }
        }
        if (imgSeq < 0 && position < nodeList.length-1) {
            if (nodeList[position + 1].nodeName === 'IMG') {
                imgSeq = 0;
            }
        }
        return imgSeq;
    }

    function findImgPosition(tagName) {
        const images = document.querySelectorAll('img[name="'+tagName+'"]');
        const content = document.getElementById('content');
        var nodeList = getNodeList();
        let imgIndex = [];
        if (images.length !== 0) {
            images.forEach(img => {
                const dataName = img.getAttribute('data-name');
                var preTexts = "";
                var postTexts = "";
                var imgSeq;
                var position;
                var q = -1;
                while (q < nodeList.length) {
                    q++;
                    if (nodeList[q] === img) {
                        position = q;
                        q = nodeList.length;
                    }
                }
                imgSeq = getImgSeq(position, nodeList);
                imgSeq > 0 ? q = position - imgSeq - 1 : q = position - 1;
                if (position !== 0) {
                    while (q >= 0) {
                        if (nodeList[q].nodeName !== "IMG") {
                            preTexts = escapeTagSymbol(nodeList[q].textContent) + preTexts;
                        } else if (nodeList[q].nodeName === "BR") {
                            preTexts = escapeTagSymbol('\n') + preTexts;
                        } else {
                            q = -1;
                        }
                        if (preTexts.length > 10) {
                            q = -1;
                        }
                        q--;
                    }
                }
                q = position + 1;
                if (position !== nodeList.length -1) {
                    var endProcess = 0;
                    while (q < nodeList.length -1) {
                        if (endProcess < 1) {
                            if (nodeList[q].nodeName !== "IMG") {
                                postTexts += escapeTagSymbol(nodeList[q].textContent);
                                endProcess = 1;
                            } else if (nodeList[q].nodeName === 'BR') {
                                postTexts += escapeTagSymbol('\n');
                            }
                        } else {
                            if (nodeList[q].nodeName !== "IMG") {
                                postTexts += escapeTagSymbol(nodeList[q].textContent);
                            } else if (nodeList[q].nodeName === 'BR') {
                                postTexts += escapeTagSymbol('\n');
                            } else {
                                q = nodeList.length;
                            }
                        }
                        if (postTexts.length > 10) {
                            q = nodeList.length;
                        }
                        q++;
                    }
                }

                const preText = preTexts.length < 10 ? preTexts.slice(0, preTexts.length) : preTexts.slice(preTexts.length -10, preTexts.length);
                const postText = postTexts.length < 10 ? postTexts.slice(0, postTexts.length) : postTexts.slice(0, 10);

                var reg = escapeRegExp(preText) + "[\\s\\n]*" + escapeRegExp(postText);
                reg = reg + "|" + escapeN(reg);
                const regex = new RegExp(reg, 'g');
                const prePost = preTexts + postText;
                const match = prePost.match(regex);
                const index = match.length;

                imgIndex.push({name: dataName, preText: preText, postText: postText, textIndex: index, arrayIndex: imgSeq});
            });
        }
        return imgIndex;
    }

    function findImgPosition2(tagName) {
        const images = document.querySelectorAll('img[name="'+tagName+'"]');
        const content = document.getElementById('content');
        var brTags = document.querySelectorAll('br');
        brTags.forEach(brTag => {
            const textNode = document.createTextNode('\n');
            brTag.replaceWith(textNode);
        });
        let imgIndex = [];
        if (images.length !== 0) {
            images.forEach(img => {
                const dataName = img.getAttribute('data-name');
                var preTexts = "";
                var postTexts = "";
                var imgSeq = -1;
                var imgSeqEnd = 0;
                var outerDiv = -1;

                var imgPosition = Array.from(content.childNodes).indexOf(img);
                if (imgPosition < 0) {
                    imgPosition = Array.from(content.childNodes).indexOf(img.parentNode);
                    if (imgPosition > 0) {
                        outerDiv = Array.from(content.childNodes[imgPosition]).indexOf(img);
                    }
                }
                if (imgPosition > 0) {
                    var q = imgPosition-1;
                    var ifCaseIMG = q > 0 && content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === 'IMG';
                    var ifCaseSameDIV = false;
                    var ifCaseDiffDIV = false;
                    if (outerDiv > -1) {
                        if (outerDiv !== 0) {
                            var priorNode = content.childNodes[q + 1].childNodes[outerDiv - 1];
                            if (priorNode.nodeType === 1 && priorNode.nodeName === 'IMG') {
                                ifCaseSameDIV = true;
                            }
                        } else {
                            var priorNodeLength = content.childNodes[q].childNodes.length;
                            var priorNode = content.childNodes[q].childNodes[priorNodeLength - 1];
                            if (priorNode.nodeType === 1 && priorNode.nodeName === 'IMG') {
                                ifCaseDiffDIV = true;
                            }
                        }
                    } else {
                        if (content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === "DIV") {
                            var priorNode = content.childNodes[q].childNodes[content.childNodes[q].childNodes.length - 1];
                            if (priorNode.nodeType === 1 && priorNode.nodeName === 'IMG') {
                                ifCaseDiffDIV = true;
                            }
                        }
                    }
                    if (q > 0 && (ifCaseIMG || ifCaseSameDIV || ifCaseDiffDIV)) {
                        imgSeq = 0;
                        var endProcess = 1;
                        var endSeq = "";
                        while (endProcess > 0 && q >= 0) {
                            if (endSeq.length === 0) {
                                if (content.childNodes[q].nodeType === 3) {
                                    preTexts = escapeTagSymbol(content.childNodes[q].textContent) + preTexts;
                                    endSeq = "true";
                                } else if (content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === 'IMG') {
                                    imgSeq ++;
                                } else if (content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === 'DIV') {
                                    var divArray = Array.from(content.childNodes[q].childNodes);
                                    if (divArray.length > 0) {
                                        for (var i=divArray.length-1; i>=0; i--) {
                                            if (divArray[i].nodeName === "IMG") {
                                                imgSeq ++;
                                            } else {
                                                endSeq = i;
                                            }
                                        }
                                    }
                                } else {
                                    preTexts = escapeTagSymbol(content.childNodes[q].innerText) + preTexts;
                                    endSeq = "true";
                                }
                            } else {
                                if (endSeq !== "true") {
                                    for (var i=parseInt(endSeq); i>=0; i--) {
                                        if (!(content.childNodes[q].childNodes[i].nodeType === 1 && content.childNodes[q].childNodes[i].nodeName === 'IMG')) {
                                            preTexts = escapeTagSymbol(content.childNodes[q].childNodes[i].textContent) + preTexts;
                                        } else {
                                            endProcess = 0;
                                        }
                                    }
                                }
                                if (content.childNodes[q].nodeType === 3) {
                                    preTexts = escapeTagSymbol(content.childNodes[q].textContent) + preTexts;
                                } else if (content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === 'IMG') {
                                    endProcess = 0;
                                } else if (content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === 'DIV') {
                                    var divArray = Array.from(content.childNodes[q].childNodes);
                                    if (divArray.length > 0) {
                                        for (var i=divArray.length-1; i>=0; i--) {
                                            if (divArray[i].nodeName === "IMG") {
                                                endProcess = 0;
                                            } else {
                                                preTexts = escapeTagSymbol(content.childNodes[q].childNodes[i].textContent) + preTexts;
                                            }
                                        }
                                    }
                                } else {
                                    preTexts = escapeTagSymbol(content.childNodes[q].innerText) + preTexts;
                                }
                            }
                            q--;
                            if (preTexts.length >= 10) {
                                q = -1;
                            }
                        }

                    } else {
                        while (q >= 0) {
                            if (content.childNodes[q].nodeType === 3) {
                                preTexts = escapeTagSymbol(content.childNodes[q].textContent) + preTexts;
                            } else if (content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === 'IMG') {
                                q = -1;
                            } else if (content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === 'DIV') {
                                var divArray = Array.from(content.childNodes[q].childNodes);
                                for (var i=divArray.length-1; i>=0; i--) {
                                    if (divArray[i].nodeName !== "IMG") {
                                        preTexts = escapeTagSymbol(content.childNodes[q].childNodes[i].textContent) + preTexts;
                                    } else {
                                        q = -1;
                                    }
                                }
                            } else {
                                preTexts = escapeTagSymbol(content.childNodes[q].innerText) + preTexts;
                            }
                            q--;
                            if (preTexts.length >= 10) {
                                q = -1;
                            }
                        }

                    }

                    q = imgPosition+1;

                    if (q < content.childNodes.length && content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === 'IMG') {
                        if (imgSeq < 0) {
                            imgSeq = 0;
                        }
                        var endProcess = 1;
                        var endSeq = "";
                        while (endProcess > 0 && q < content.childNodes.length) {
                            if (endSeq.length === 0) {
                                if (content.childNodes[q].nodeType === 3) {
                                    postTexts += escapeTagSymbol(content.childNodes[q].textContent);
                                    endSeq = "true";
                                } else if (content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === 'IMG') {
                                } else if (content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === 'DIV') {
                                    var divArray = Array.from(content.childNodes[q].childNodes);
                                    if (divArray.length > 0) {
                                        for (var i=0; i<divArray.length; i++) {
                                            if (divArray[i].nodeName !== "IMG") {
                                                endSeq = i;
                                            }
                                        }
                                    }
                                } else {
                                    postTexts += escapeTagSymbol(content.childNodes[q].textContent);
                                    endSeq = "true";
                                }
                            } else {
                                if (endSeq !== "true") {
                                    for (var i=parseInt(endSeq); i<content.childNodes[q].childNodes.length; i++) {
                                        if (!(content.childNodes[q].childNodes[i].nodeType === 1 && content.childNodes[q].childNodes[i].nodeName === 'IMG')) {
                                            postTexts += escapeTagSymbol(content.childNodes[q].childNodes[i].textContent);
                                        } else {
                                            endProcess = 0;
                                        }
                                    }
                                }
                                if (content.childNodes[q].nodeType === 3) {
                                    postTexts += escapeTagSymbol(content.childNodes[q].textContent);
                                } else if (content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === 'IMG') {
                                    endProcess = 0;
                                } else if (content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === 'DIV') {
                                    var divArray = Array.from(content.childNodes[q].childNodes);
                                    if (divArray.length > 0) {
                                        for (var i=0; i<divArray.length; i++) {
                                            if (divArray[i].nodeName === "IMG") {
                                                endProcess = 0;
                                            } else {
                                                postTexts += escapeTagSymbol(content.childNodes[q].childNodes[i].textContent);
                                            }
                                        }
                                    }
                                } else {
                                    postTexts += escapeTagSymbol(content.childNodes[q].textContent);
                                }
                            }
                            q++;
                            if (postTexts.length >= 10) {
                                endProcess = 0;
                            }
                        }

                    } else {
                        while (q < content.childNodes.length) {
                            if (content.childNodes[q].nodeType === 3) {
                                postTexts += escapeTagSymbol(content.childNodes[q].textContent);
                            } else if (content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === 'IMG') {
                                imgSeqEnd = 1;
                                q = content.childNodes.length;
                            } else if (content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === 'DIV') {
                                var divArray = Array.from(content.childNodes[q].childNodes);
                                for (var i=0; i<divArray.length; i++) {
                                    if (divArray[i].nodeName !== "IMG") {
                                        postTexts += escapeTagSymbol(content.childNodes[q].childNodes[i].textContent);
                                    } else {
                                         q = content.childNodes.length;
                                    }
                                }
                            } else {
                                postTexts += escapeTagSymbol(content.childNodes[q].innerText);
                            }
                            q++;
                            if (postTexts.length >= 10) {
                                q = content.childNodes.length;
                            }
                        }

                    }
                    preTexts += "\n";

                    const preText = preTexts.length < 10 ? preTexts.slice(0, preTexts.length) : preTexts.slice(preTexts.length -10, preTexts.length);
                    const postText = postTexts.length < 10 ? postTexts.slice(0, postTexts.length) : postTexts.slice(0, 10);

                    var reg = escapeRegExp(preText) + "[\\s\\n]*" + escapeRegExp(postText);
                    reg = reg + "|" + escapeN(reg);
                    const regex = new RegExp(reg, 'g');
                    const prePost = preTexts + postText;
                    const match = prePost.match(regex);
                    const index = match.length;

                    imgIndex.push({name: dataName, preText: preText, postText: postText, textIndex: index, arrayIndex: imgSeq});
                    if (imgSeqEnd === 1) {

                        imgSeq = -1;
                    }
                }
            });
        }
        return imgIndex;
    }

    function setImgTag(imgObjects) {
        const seqObjects = imgObjects.filter(img => img.arrayIndex != null);
        const seqImg = [];

        while (seqObjects.length > 0) {
            const seqObj = seqObjects.shift();
            const seqArr = [];
            const seqObjFilter = seqObjects.filter(element => element.preText === seqObj.preText && element.postText === seqObj.postText);
            seqObjFilter.forEach(el => {
                seqArr.push(el);
                seqObjects.splice(seqObjects.indexOf(el), 1);
            });
            seqArr.push(seqObj);
            seqImg.push(seqArr);
        }
        let wholeContent;
        seqImg.forEach(img => {
            if (img[0].arrayIndex >= 0) {
                img.sort((a, b) => {
                    return parseInt(a.arrayIndex, 10) - parseInt(b.arrayIndex, 10);
                });
            }
            var imgTag = img[0].preText;
            img.forEach(tag => {
                var insertImg = '<img name="image_is_included_here"/>';
                imgTag += insertImg;
            });
            imgTag += img[0].postText;
            var contentText = document.getElementById('content').innerHTML;
            contentText = contentText.replace(/&nbsp;|&#160;/g, ' ');
            let count = 1;
            var reg = escapeRegExp(img[0].preText.trim()) + "[\\s\\n]*" + escapeRegExp(img[0].postText.trim());
            reg = reg + "|" + escapeN(reg);
            const regex = new RegExp(reg, 'g');
            var newReg = new RegExp(escapeRegExp(img[0].preText.trim()), 'g');

            var imageContent = contentText.replace(regex, function(match) {
                if (img[0].textIndex === count) {
                    return imgTag;
                }
                count++;
                return match;
            });

            if (imageContent != false) {
                const content = document.getElementById('content');
                content.innerHTML = imageContent;
            }

        });

        const flatSeqImg = seqImg.flat();
        flatSeqImg.forEach(img => {
            const imgIncludes = document.querySelectorAll('img[name="image_is_included_here"]');
            imgIncludes[0].setAttribute("src", "data: "+img.mimeType+';base64, '+img.base64);
            imgIncludes[0].setAttribute("data-name", img.originalName);
            imgIncludes[0].setAttribute("name", "exist");
        });

    }

    function escapeRegExp(string) {
        return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    }

    function escapeN(string) {
        return string.replace(/\n/g, "");
    }

    function setDataURL(image, imgList) {
        const reader = new FileReader();
        const images = document.querySelectorAll('img');

        reader.readAsDataURL(image);

        reader.onload = () => {
            const dataURL = reader.result;
            var checkList = {};
            setImgElement(image, dataURL);
            imgList.push(image);
            const imgLabel = document.getElementById('imageLabel');
            const imgTags = document.querySelectorAll('img');
            imgLabel.innerText = "이미지 추가 ("+imgTags.length+"/10)";
        };

    }

    function setImgElement(image, dataURL) {
        const content = document.getElementById('content');
        const divTag = document.createElement('div');
        const img = document.createElement('img');
        img.setAttribute("src", dataURL);
        img.setAttribute("draggable", "true");
        img.setAttribute("name", "new");
        img.setAttribute("data-name", image.name);
        divTag.appendChild(img);
        content.appendChild(divTag);
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
                    if (confirm("이미지를 삭제하시겠습니까?")) {
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

    function showImages(images) {
        if (images === null) {
            return;
        }
        const size = images.length;
        var contentText = document.getElementById('content').textContent;

        if (size != 0) {
            images.forEach(image => {
                const insertImg = '<img src="data:'+image.mimeType+';base64 ,'+image.base64+'" data-name='+image.originalName+' name=\"exist\">';
                const preText = image.preText;
                const postText = image.postText;

                const regex = new RegExp(image.index);
                const content = document.getElementById('content')
                const match = content.textContent.match(regex);
                if (match !== null && match.length === 1) {
                    var tempPre = preText !== undefined ? content.textContent.slice(match.index < 10 ? 0 : match.index - preText.length, match.index) : "";
                    var tempNext = postText !== undefined ? content.textContent.slice(match.index, match.index > content.textContent.length ? content.textContent.length : match.index + postText.length) : "";
                    if(tempPre === preText && tempNext === postText) {
                        content.innerHTML = content.innerHTML.replace(regex, insertImg);
                    }
                }
            })
        }
        const imageLabel = document.getElementById('imageLabel');
        if (imageLabel !== null) {
            imageLabel.innerText = "이미지 추가 ("+size+"/10)";
        }
    }

    function escapeTagSymbol(content) {
        return content.replace(/</g, '&lt;').replace(/>/g, '&gt;');
    }