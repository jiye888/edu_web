    function matchContent(id) {
        const regex = new RegExp(id, "g");
        const contentText = document.getElementById('content').textContent;
        const match = contentText.match(regex);

        return match;
    }

    function findImgPosition(tagName) {
        const images = document.querySelectorAll('img[name="'+tagName+'"]');
        const content = document.getElementById('content');
        let imgIndex = [];
        if (images.length !== 0) {
            images.forEach(img => {
                const dataName = img.getAttribute('data-name');
                var preTexts = "";
                var postTexts = "";
                var imgSeq = -1;
                var imgSeqEnd = 0;

                const imgPosition = Array.from(content.childNodes).indexOf(img);
                if (imgPosition > 0) {
                    var base64 = img.getAttribute("data-base64");
                    if (base64 != null) {
                        base64 = base64.substring(base64.indexOf(",")+1);
                    }
                    var q = imgPosition-1;
                    console.log(content.childNodes);
                    if (q > 0 && content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === 'IMG') {
                        imgSeq = 0;
                        var endProcess = 1;
                        var endSeq = 0;
                        while (endProcess > 0 && q >= 0) {
                            if (endSeq === 0) {
                                if (content.childNodes[q].nodeType === 3) {
                                    preTexts = escapeTagSymbol(content.childNodes[q].textContent) + preTexts;
                                    endSeq = 1;
                                } else if (content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === 'IMG') {
                                    imgSeq ++;
                                } else {
                                    preTexts = escapeTagSymbol(content.childNodes[q].innerText) + preTexts;
                                    endSeq = 1;
                                }
                            } else {
                                if (content.childNodes[q].nodeType === 3) {
                                    preTexts = escapeTagSymbol(content.childNodes[q].textContent) + preTexts;
                                } else if (content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === 'IMG') {
                                    endProcess = 0;
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
                            } else {
                                preTexts = escapeTagSymbol(content.childNodes[q].innerText) + preTexts;
                            }
                            q--;
                            if (preTexts.length >= 10) {
                                q = -1;
                            }
                        }
/*
                        if ((content.childNodes[imgPosition-1].nodeType !== 3 && content.childNodes[imgPosition-1].nodeName === "IMG") || (imgPosition + 1 < content.childNodes.length && content.childNodes[imgPosition+1].nodeType !== 3 && content.childNodes[imgPosition+1].nodeName === "IMG")) {
                            imgSeq = 0;
                            for (var i=imgPosition; i>=1; i--) {
                                if (content.childNodes[i-1].nodeType !== 3 && content.childNodes[i-1].nodeName === "IMG") {
                                    imgSeq++;
                                } else if (content.childNodes[i-1].nodeType === 3 || content.childNodes[i-1].nodeName !== "IMG") {
                                    i = 0;
                                }
                            }
                        }
*/

                    }

                    q = imgPosition+1;

                    if (q < content.childNodes.length && content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === 'IMG') {
                        if (imgSeq < 0) {
                            imgSeq = 0;
                        }
                        var endProcess = 1;
                        var endSeq = 0;
                        while (endProcess > 0 && q < content.childNodes.length) {
                            if (endSeq === 0) {
                                if (content.childNodes[q].nodeType === 3) {
                                    postTexts += escapeTagSymbol(content.childNodes[q].textContent);
                                    endSeq = 1;
                                } else if (content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === 'IMG') {
                                } else {
                                    postTexts += escapeTagSymbol(content.childNodes[q].innerText);
                                    endSeq = 1;
                                }
                            } else {
                                if (content.childNodes[q].nodeType === 3) {
                                    postTexts += escapeTagSymbol(content.childNodes[q].textContent);
                                } else if (content.childNodes[q].nodeType === 1 && content.childNodes[q].nodeName === 'IMG') {
                                    endProcess = 0;
                                } else {
                                    postTexts += escapeTagSymbol(content.childNodes[q].innerText);
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
                            } else {
                                postTexts += escapeTagSymbol(content.childNodes[q].innerText);
                            }
                            q++;
                            if (postTexts.length >= 10) {
                                q = content.childNodes.length;
                            }
                        }
/*
                        if ((content.childNodes[imgPosition-1].nodeType !== 3 && content.childNodes[imgPosition-1].nodeName === "IMG") || (imgPosition + 1 < content.childNodes.length && content.childNodes[imgPosition+1].nodeType !== 3 && content.childNodes[imgPosition+1].nodeName === "IMG")) {
                            imgSeq = 0;
                            for (var i=imgPosition; i>=1; i--) {
                                if (content.childNodes[i-1].nodeType !== 3 && content.childNodes[i-1].nodeName === "IMG") {
                                    imgSeq++;
                                } else if (content.childNodes[i-1].nodeType === 3 || content.childNodes[i-1].nodeName !== "IMG") {
                                    i = 0;
                                }
                            }
                        }*/


                    }
                    /*
                    while (q <= content.childNodes.length-1) {
                        if (content.childNodes[q].nodeType === 3) {
                            postTexts += content.childNodes[q].textContent;
                        } else if (content.childNodes[q].nodeType === 1) {
                            q = content.childNodes.length;
                        } else {
                            postTexts += content.childNodes[q].innerText;
                        }
                        q++;
                        if (postTexts.length >= 10) {
                            q = content.childNodes.length;
                        }
                    }*/
                    preTexts += "\n";

                    const preText = preTexts.length < 10 ? preTexts.slice(0, preTexts.length) : preTexts.slice(preTexts.length -10, preTexts.length);
                    const postText = postTexts.length < 10 ? postTexts.slice(0, postTexts.length) : postTexts.slice(0, 10);

                    var reg = escapeRegExp(preText) + "[\\s\\n]*" + escapeRegExp(postText);
                    reg = reg + "|" + escapeN(reg);
                    const regex = new RegExp(reg, 'g');
                    const prePost = preTexts + postText;
                    const match = prePost.match(regex);
                    const index = match.length;

                    imgIndex.push({name: dataName, preText: preText, postText: postText, textIndex: index, arrayIndex: imgSeq, base64: base64});
                    if (imgSeqEnd === 1) {

                        imgSeq = -1;
                    }
                }
            });
        }
        alert("finish");
        return imgIndex;
    }

    function preventDiv(event) {
        const select = window.getSelection();
        const selectArea = select.getRangeAt(0);
        if (selectArea) {
            const afterSelect = selectArea.commonAncestorContainer.nextSibling;
            if (event.key === 'Enter') {
                event.preventDefault();
                //const brTag = document.createElement('br');
                //afterSelect.parentNode.insertBefore(brTag, afterSelect);
                const lineBreakText = document.createTextNode('\n');
                selectArea.insertNode(lineBreakText);
                selectArea.setStartAfter(lineBreakText);
                selectArea.collapse(true);
                select.removeAllRanges();
                select.addRange(selectArea);
                //afterSelect.parentNode.insertBefore(lineBreakText, afterSelect);
                /*if (selectArea.endOffset > 0) {
                    selectArea.setEnd(selectArea.endContainer, selectArea.endOffset +1);
                    select.removeAllRanges();
                    select.addRange(selectArea);
                }*/
            }

        }
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
            var imgSeqs = img[0].preText;
            img.forEach(tag => {
                imgSeqs += '<img src="data:'+tag.mimeType+';base64 ,'+tag.base64+'" data-name='+tag.originalName+' name=\"exist\">';
            });
            imgTag += img[0].postText;
            var contentText = document.getElementById('content').innerHTML;
            let count = 0;
            var reg = escapeRegExp(img[0].preText) + "[\\s\\n]*" + escapeRegExp(img[0].postText);
            /*if (img[0].preText.length < 10) {
                reg = "^" + reg;
            }
            if (img[0].postText.length < 10) {
                reg = reg + "$";
            }*/
            reg = reg + "|" + escapeN(reg);
            const regex = new RegExp(reg, 'g');

            var imageContent = contentText.replace(regex, function(match) {
                count++;
                if (img[0].textIndex === count) {
                    return imgTag;
                }
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
        const img = document.createElement('img');
        img.setAttribute("src", dataURL);
        img.setAttribute("draggable", "true");
        img.setAttribute("name", "new");
        img.setAttribute("data-name", image.name);
        content.appendChild(img);
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