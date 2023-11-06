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
                        if (nodeList[q].nodeName !== "IMG" && nodeList[q].nodeName !== "BR") {
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
                    while (q <= nodeList.length -1) {
                        if (endProcess < 1) {
                            if (nodeList[q].nodeName !== "IMG" && nodeList[q].nodeName !== "BR") {
                                postTexts += escapeTagSymbol(nodeList[q].textContent);
                                endProcess = 1;
                            } else if (nodeList[q].nodeName === 'BR') {
                                postTexts += escapeTagSymbol('\n');
                            }
                        } else {
                            if (nodeList[q].nodeName !== "IMG" && nodeList[q].nodeName !== "BR") {
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

    function getSeqImg(seqImg) {
        var seqLimit = seqImg.length - 1;
        var i = 0;
        while (i < seqLimit) {
            if (seqImg[i][0].postText === seqImg[i+1][0].preText) {
                seqImg[i].push(seqImg[i+1]);
                seqImg.splice(i+1, 1);
                seqLimit--;
                var endProcess = 0;
                while (endProcess === 0) {
                    if (seqImg[i][seqImg[i].length-1][0] && seqImg[i+1] && seqImg[i][seqImg[i].length-1][0].postText === seqImg[i+1][0].preText) {
                        seqImg[i].push(seqImg[i+1]);
                        seqImg.splice(i+1, 1);
                        seqLimit--;
                    } else {
                        endProcess = 1;
                    }
                }
            }
            i++;
        }
        return seqImg;
    }

    function setImgTag(imgObjects) {
        const seqObjects = imgObjects.filter(img => img.arrayIndex != null);
        var seqImg = [];

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
        seqImg = getSeqImg(seqImg);
        if (seqImg[0].length === 1 && seqImg[0][0].preText.length < 5) {
            const firstImg = document.createElement('img');
            firstImg.setAttribute("src", "data: "+seqImg[0][0].mimeType+';base64, '+seqImg[0][0].base64);
            firstImg.setAttribute("data-name", seqImg[0][0].originalName);
            firstImg.setAttribute("name", "exist");
            const content = document.getElementById('content');
            content.insertBefore(firstImg, content.firstChild);
            seqImg.shift();
        }

        seqImg.forEach(img => {
            var imgTag = "";
            var reg = "";
            var addPreText = 1;
            if (!Array.isArray(img[0])) {
                addPrePost = 0;
            }
            for (var i=0; i<img.length; i++) {
            Array.isArray("isarray" + img[i]);
                if (!Array.isArray(img[i]) && Array.isArray(img[i+1])) {
                    img[i].last = true;
                }
                if (i > 0 && !Array.isArray(img[i]) && Array.isArray(img[i-1])) {
                    img[i].first = true;
                }
            }
            if (!Array.isArray(img[0])) {
                img[0].first = true;
            }

            img.forEach(tag => {
                if (Array.isArray(tag)) {
                    if (addPreText > 0) {
                        imgTag += tag[0].preText;
                        for (var i=0; i<tag.length; i++) {
                            imgTag += '<img name="image_is_included_here"/>';
                        }
                        reg += escapeRegExp(tag[0].preText.replace(new RegExp(String.fromCharCode(160), 'g'), '\u0020')) + "[\\s\\n]*";
                    } else {
                        for (var i=0; i<tag.length; i++) {
                            imgTag += '<img name="image_is_included_here"/>';
                        }
                        imgTag += tag[0].postText;
                        reg += escapeRegExp(tag[0].postText.replace(new RegExp(String.fromCharCode(160), 'g'), '\u0020')) + "[\\s\\n]*";
                    }
                } else {
                    addPreText = 0;
                    if (tag.last !== undefined) {
                        imgTag += '<img name="image_is_included_here"/>';
                        imgTag += tag.postText;
                        reg += escapeRegExp(tag.postText.replace(new RegExp(String.fromCharCode(160), 'g'), '\u0020')) + "[\\s\\n]*";
                    } else if (tag.first !== undefined) {
                        imgTag += tag.preText;
                        reg += escapeRegExp(tag.preText.replace(new RegExp(String.fromCharCode(160), 'g'), '\u0020')) + "[\\s\\n]*";
                        imgTag += '<img name="image_is_included_here"/>';
                    } else {
                        imgTag += '<img name="image_is_included_here"/>';
                    }
                }
            });

            var contentText = document.getElementById('content').innerHTML;
            contentText = contentText.replace(/&nbsp;|&#160;/g, '\u0020');
            let count = 1;
            reg = reg + "|" + escapeN(reg);
            const regex = new RegExp(reg, 'g');

            var imageContent = contentText.replace(regex, function(match) {
                if (img[0].textIndex === count) {
                    return imgTag;
                }
                count++;
                return match;
            });


            const content = document.getElementById('content');
            content.innerHTML = imageContent;
        });

        const flatSeqImg = seqImg.flat();
        flatSeqImg.forEach(img => {
            if (Array.isArray(img)) {
                img.forEach(one => {
                    const imgIncludes = document.querySelectorAll('img[name="image_is_included_here"]');
                    imgIncludes[0].setAttribute("src", "data: "+one.mimeType+';base64, '+one.base64);
                    imgIncludes[0].setAttribute("data-name", one.originalName);
                    imgIncludes[0].setAttribute("name", "exist");
                });
            } else {
                const imgIncludes = document.querySelectorAll('img[name="image_is_included_here"]');
                imgIncludes[0].setAttribute("src", "data: "+img.mimeType+';base64, '+img.base64);
                imgIncludes[0].setAttribute("data-name", img.originalName);
                imgIncludes[0].setAttribute("name", "exist");
            }
        });

    }

    function escapeRegExp(string) {
        return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    }

    function escapeN(string) {
        return string.replace(/\n/g, "");
    }

    function uniteSpace(string) {
        string = string.replace(/\s/g, ' ');
        return string;
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

    function getContentText() {
        const content = document.getElementById('content');
        const contentCopy = content.innerHTML;
        const copiedDiv = document.createElement('div');
        copiedDiv.innerHTML = contentCopy;
        const brTags = copiedDiv.querySelectorAll('br');
        brTags.forEach(tag => {
            var nNode = document.createTextNode('\n');
            tag.parentNode.replaceChild(nNode, tag);
        });
        return copiedDiv.textContent;
    }

    function replaceN() {
        var nodeList = getNodeList();
        nodeList.forEach(node => {
            if (node.nodeName === '#text') {
                if (node.data.slice(0, 2) === "\n") {
                    var brTag = document.createElement('br');
                    node.parentNode.insertBefore(brTag, node);
                    node.data = node.data.slice(2);
                }
                if (node.data.slice(-2) === "\n") {
                    var brTag = document.createElement('br');
                    node.parentNode.insertBefore(brTag, node.nextSibling);
                    node.data = node.data.slice(0, -2);
                }
            }
        });
    }