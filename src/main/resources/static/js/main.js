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

                const imgPosition = Array.from(content.childNodes).indexOf(img);
                if (imgPosition > 0) {
                    for (var i=0; i<imgPosition; i++) {
                        if (content.childNodes[i].nodeType === 3) {
                            preTexts += content.childNodes[i].textContent;
                        } else {
                            preTexts += content.childNodes[i].innerText;
                        }
                    }
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
                    for (var i=imgPosition; i<content.childNodes.length; i++) {
                        if (content.childNodes[i].nodeType === 3) {
                            postTexts += content.childNodes[i].textContent;
                        } else {
                            postTexts += content.childNodes[i].innerText;
                        }
                    }
                }

                const preText = preTexts.length < 10 ? preTexts.slice(0, preTexts.length) : preTexts.slice(preTexts.length -10, preTexts.length);
                const postText = postTexts.length < 10 ? postTexts.slice(0, postTexts.length) : postTexts.slice(0, 10);

                var reg = escapeRegExp(img[0].preText) + "[\\s\\n]*" + escapeRegExp(img[0].postText);
                reg = reg + "|" + escapeN(reg);
                const regex = new RegExp(reg, 'g');
                const prePost = preTexts + postText;
                const match = prePost.match(regex);
                const index = match.length;
                var base64 = img.getAttribute("data-base64");
                if (base64 != null) {
                    base64 = base64.substring(base64.indexOf("base64,")+7);
                }
                imgIndex.push({name: dataName, preText: preText, postText: postText, textIndex: index, arrayIndex: imgSeq , base64: base64, duplicate: img.getAttribute("data-duplicate")});
            });
        }
        return imgIndex;
    }

    function setImgTag(imgObjects) {
        const seqObjects = imgObjects.filter(img => img.arrayIndex != null);
        const singleObjects = imgObjects.filter(img => img.arrayIndex == null);
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
        seqImg.forEach(img => {
            if (img[0].arrayIndex >= 0) {
                img.sort((a, b) => {
                    return parseInt(a.arrayIndex, 10) - parseInt(b.arrayIndex, 10);
                });
            }
            var imgTag = img[0].preText;
            img.forEach(tag => {
                var insertImg = '<img src="data:'+tag.mimeType+';base64 ,'+tag.base64+'" data-name='+tag.originalName+' name="exist">';
                imgTag += insertImg;
            });
            imgTag += img[0].postText;
            var contentText = document.getElementById('content').innerHTML;
            let count = 0;
            var reg = escapeRegExp(img[0].preText) + "[\\s\\n]*" + escapeRegExp(img[0].postText);
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
            let checkList = [];
            if (images.length === 0) {
                setImgElement(image, dataURL);
            } else {
                images.forEach(img => {
                    if (image.name === img.getAttribute("data-name")) {
                        if (dataURL !== img.src) {
                            checkList.push("base64");
                            //const newImg = setImgElement(image, dataURL);
                            //newImg.setAttribute("data-base64", dataURL);
                            img.setAttribute("data-base64", img.src);
                            //imgList.push(image);
                        } else {
                            //const newImg = setImgElement(image, dataURL);
                            //newImg.setAttribute("data-duplicate", "true");
                            //imgList.push(image);
                            checkList.push("duplicate");
                        }
                    }
                })
                const newImg = setImgElement(image, dataURL);
                if (checkList.includes("base64")) {
                    newImg.setAttribute("data-base64", dataURL);
                } else if (checkList.includes("duplicate")) {
                    newImg.setAttribute("data-duplicate", "true");
                }
            }
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
        img.setAttribute("style", "max-height:600px; max-width:600px;");
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
                const insertImg = '<img src="data:'+image.mimeType+';base64 ,'+image.base64+'" data-name='+image.originalName+'>';
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