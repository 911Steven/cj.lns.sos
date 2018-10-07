(function() {
	$('.pub>.title>span,.pub>.context>span').on('click', function(e) {
		$(this).hide();
		$(this).siblings('input').focus();
		$(this).siblings('textarea').focus();
	});
	$('.pub>.title>input,.pub>.context>textarea').focus(function(e) {
		$(this).siblings('span').hide();
	});
	$('.pub>.title>input,.pub>.context>textarea').blur(function(e) {
		if ($(this).val() == '') {
			$(this).siblings('span').show();
			return;
		}
		if ($(this).is('input') && $(this).val().length > 50) {
			alert('出超50字');
		}
	});
	$('.pub > .op > .area > .pics > .items').undelegate(' > li', 'click');
	$('.pub > .op > .area > .pics > .items').delegate(' > li', 'click',
			function(e) {
				var isMedia=$(this).attr('media');
				var shower=$('.pub > .op > .area > .pics > .show');
				shower.empty();
				if(typeof isMedia=='undefined'){
					var src = $(this).children('img').attr('src');
					var img="<img src='"+src+"' />"
					shower.append(img);
				}else{
					/////
					var video=$(this).find('video').first().clone();
					shower.append(video);
					shower.find('video').attr('controls','');
				}
			});
	$('.pub>.op>.right>.security').hover(function(e) {
		$(this).children('ul').show();
	}, function(e) {
		// $(this).children('ul').hide();
	});
	$('.pub>.op>.right>.security>ul>li')
			.on(
					'click',
					function(e) {// 选中安全设置
						$(this).parent('ul').hide();
						$(this).parents('.security').attr('title',
								$(this).html());
						var security = $(this).parents('.security');
						security.attr('selectedType', $(this).attr('role'));
						if ($(this).attr('role') == 'options') {// 弹出用户选择
							var url = './security/contactPad.html';
							$
									.ajax(
											{
												type : 'post',
												url : url,
												success : function(data) {
													$('.pub .popup-user')
															.remove();
													$('.pub').append(data);
													$('.popup-user')
															.on(
																	'confirm',
																	function(
																			e1,
																			selected) {
																		var list = $('.desktop .user-list[subject-allow]');
																		list
																				.empty();
																		var usersStr = "";
																		var users = $(
																				selected)
																				.children(
																						'li.person');
																		users
																				.each(function() {
																					var img = $(
																							this)
																							.find(
																									'img.p-h')
																							.attr(
																									'src');
																					var uc = $(
																							this)
																							.attr(
																									'uc');
																					var name = $(
																							this)
																							.find(
																									'span')
																							.html();
																					var html = "<li class='user'><img src='"
																							+ img
																							+ "' plugin><br><span>"
																							+ name
																							+ "</span></li>";
																					list
																							.append(html);
																					usersStr += uc
																							+ ',';
																				});
																		if (usersStr == '')
																			return;
																		security
																				.attr(
																						'selectedUsers',
																						usersStr);
																		security
																				.children(
																						'img')
																				.attr(
																						'title',
																						'发送给：'
																								+ usersStr);
																	});
												}
											}).error(function(e) {
										alert(e.responseText);
									});
						}
					});
	var main = $('.workbench[region=workbench]>ul>li>.main');
	main.undelegate('.main-right>.portlet[category] > .op > img', 'click');
	main.delegate('.main-right>.portlet[category] > .op > img', 'click',
			function(e) {
				$(this).siblings('input').show();
				$(this).siblings('span').show();
			});
	main.undelegate('.main-right>.portlet[category] > .op > span', 'click');
	main.delegate('.main-right>.portlet[category] > .op > span', 'click',
			function(e) {
				$(this).hide();
				$(this).siblings('input').focus();
			});
	main.undelegate('.main-right>.portlet[category] > .op > input', 'focus');
	main.delegate('.main-right>.portlet[category] > .op > input', 'focus',
			function(e) {
				$(this).siblings('span').hide();
			});
	main.undelegate('.main-right>.portlet[category] > .op > input', 'focusout');
	main.delegate('.main-right>.portlet[category] > .op > input', 'focusout',
			function(e) {
				if ($(this).val() == '') {
					$(this).siblings('span').show();
				}
			});
	main.undelegate('.main-right>.portlet[category] > .op > input', 'keyup');
	main.delegate('.main-right>.portlet[category] > .op > input', 'keyup',
			function(e) {
				if (e.keyCode == '13') {
					var name = $(this).val();
					if (name == '') {
						alert('分类名为空');
						return;
					}
					$.post('./swssite/microblog/addArticleCategory.service', {
						name : name
					}, function(e) {
						var category = $.parseJSON(e);
						category = eval(category);
						var ul = $('.portlet[category] > ul').first();
						var li = ul.children('li').first().clone();
						li.removeClass('cat-selected');
						li.attr('category', category.id);
						li.children('span[label]').html(category.name);
						ul.append(li);
						$(this).hide();
					}).error(function(e) {
						alert(e.responseText);
					});

				}
			});
	main.undelegate('.portlet[category] > ul > li', 'click');
	main.delegate('.portlet[category] > ul > li', 'click', function(e) {
		$(this).siblings('li.cat-selected').removeClass('cat-selected');
		$(this).addClass('cat-selected');
	});
	main.undelegate('.portlet[category] > ul > li', 'mouseenter mouseleave');
	main.delegate('.portlet[category] > ul > li', 'mouseenter mouseleave',
			function(e) {
				if (e.type == 'mouseenter') {
					$(this).children('span[x]').show();
				} else if (e.type == 'mouseleave') {
					$(this).children('span[x]').hide();
				}

			});
	main.undelegate('.portlet[category] > ul > li>span[x]', 'click');
	main.delegate('.portlet[category] > ul > li>span[x]', 'click', function(e) {
		var the = $(this).parent('li');
		var categoryId = the.attr('category');
		$.get('./swssite/microblog/delArticleCategory.service', {
			id : categoryId
		}, function(e) {
			the.remove();
		}).error(function(e) {
			alert(e.responseText);
		});
	});
	$('.pub > .op > .right > .save').on('click', function(e) {
		sendit();
	});
	$('.pub > .op > .right > .upload > li[upload]').on('click', function(e) {
		var type = $(this).attr('upload');
		switch (type) {
		case "pic":
			uploadPic(this);
			break;
		case "media":
			uploadMedia(this);
			break;
		case "file":
			uploadFile(this);
			break;
		}
	});

	function sendit() {
		var title = $('.pub > .title > input').val();
		if (title == '') {
			alert('标题为空');
			return;
		}
		var content = $('.pub > .context > textarea').val();
		if (content == '') {
			alert('内容为空');
			return;
		}
		var categoryE = $('.portlet[category] > ul > li.cat-selected');
		if (categoryE.length < 1) {
			alert('没有选择分类');
			return;
		}
		var category = categoryE.attr('category');
		// 发送文章
		var security = $('.pub > .op > .right > .security');
		var securityType = security.attr('selectedType');
		if (typeof securityType == 'undefined') {
			securityType = 'allUsers';
		}
		var securityUsers = security.attr('selectedUsers');
		if (typeof securityUsers == 'undefined') {
			securityUsers = '';
		}
		var pictureEs = $('.pub > .op > .area > .pics > .items > li > img[fn]');
		var pictures = [];
		for (var i = 0; i < pictureEs.length; i++) {
			var pE = pictureEs[i];
			pictures.push($(pE).attr('fn'));
		}

		var mediaEs = $('.pub > .op > .area > .pics > .items > li > video[fn]');
		var medias = [];
		for (var i = 0; i < mediaEs.length; i++) {
			var pE = mediaEs[i];
			medias.push($(pE).attr('fn'));
		}
		
		var fileEs = $('.pub > .op > .area > .files > li[fn]');
		var files = [];
		for (var i = 0; i < fileEs.length; i++) {
			var pE = fileEs[i];
			files.push($(pE).attr('fn'));
		}

		$
				.post(
						'./swssite/microblog/createArticle.service',
						{
							title : title,
							content : content,
							categoryId : category,
							securityType : securityType,
							securityUsers : securityUsers,
							pictures : pictures,
							medias:medias,
							files : files
						},
						function(data) {
							// 发送成功则跳转到文章列表
							var obj = eval("(" + data + ")");
							$.cj.App
									.openApp('appi://site.portal.microblog.myarticle');
						}).error(function(e) {
					alert(e.responseText);
				});

		// 发送文件，即传即用
	}
	var fileCount = 0;
	function uploadPic(e) {
		// 发送图片，即传即用,不是在发放文件时一次上传，而是分开,采用formData发送
		var sendButton = $('.pub > .op > .right');
		var fileId = 'file_' + fileCount;
		fileCount++;
		sendButton
				.append("<input id='"
						+ fileId
						+ "' name='"
						+ fileId
						+ "' type='file' style='display:none;' accept='.jpg,.gif,.svg,.png' data-max_size='2000000'>");
		var fileE = sendButton.children('#' + fileId);
		fileE.on('change', function(file) {
			if (fileE.val() == '') {
				fileE.remove();
				return false;
			}
			
			
			var shower=$('.pub > .op > .area > .pics > .show');
			shower.find('video').remove();
			shower.find('>img').remove();
			var imghtml="<img src='#'>";
			shower.append(imghtml);
			var areaImg = shower.find('>img');
			areaImg.attr('src', 'img/waiting.gif');
			if (areaImg.is(':hidden')) {
				areaImg.show();
			}
			var url = './swssite/microblog/uploadPictures.service';
			$.ajaxFileUpload({
				url : url, // 用于文件上传的服务器端请求地址
				secureuri : false, // 一般设置为false
				fileElementId : fileId,
				dataType : 'json',// 返回值类型 一般设置为json
				data : {},
				success : function(data, status) {
					
					var src = data.src.replace('&amp;', '&');
					var fn = data.fn;
					
					var ul = $('.pub > .op > .area > .pics > .items').first();
					var li = "<li><img src='"+src+"' fn='"+fn+"'></li>";
					ul.append(li);

					areaImg.attr('src', src);

					fileE.remove();
				},
				error : function(data, status, e) {
					areaImg.hide();
					fileE.remove();
					alert(e);
				}
			});

		})
		fileE.trigger('click');

		return false;
	}
	function uploadMedia(e) {
		// 发送图片，即传即用,不是在发放文件时一次上传，而是分开,采用formData发送
		var sendButton = $('.pub > .op > .right');
		var fileId = 'file_' + fileCount;
		fileCount++;
		sendButton
				.append("<input id='"
						+ fileId
						+ "' name='"
						+ fileId
						+ "' type='file' style='display:none;' accept=',.mov,.webm,.mp3,.mp4,.avi,.mkv,.rmvb' data-max_size='8000000'>");
		var fileE = sendButton.children('#' + fileId);
		fileE.on('change', function(file) {
			if (fileE.val() == '') {
				fileE.remove();
				return false;
			}
			var shower=$('.pub > .op > .area > .pics > .show');
			shower.find('video').remove();
			shower.find('>img').remove();
			var imghtml="<img src='#'>";
			shower.append(imghtml);
			var areaImg = shower.find('>img');
			areaImg.attr('src', 'img/waiting.gif');
			if (areaImg.is(':hidden')) {
				areaImg.show();
			}
			var url = './swssite/microblog/uploadMedias.service';
			$.ajaxFileUpload({
				url : url, // 用于文件上传的服务器端请求地址
				secureuri : false, // 一般设置为false
				fileElementId : fileId,
				dataType : 'json',// 返回值类型 一般设置为json
				data : {},
				success : function(data, status) {
					areaImg.remove();
					var src = data.src.replace('&amp;', '&');
					var fn = data.fn;
					
					$('#my-video').remove();
					var videohtml="<video class='video-js  vjs-default-skin' controls preload='auto' poster='#' data-setup='{}'>";
					var source="";
					var ext='';
					ext=fn.substring(fn.lastIndexOf('.'),fn.length);
					switch(ext){
					case '.mov':
						source="<source type='video/quicktime' src='"+src+"'></source>";
						break;
					case '.mp3':
						source="<source type='audio/mpeg' src='"+src+"'></source>";
						break;
					case '.rmvb':
						source="<source type='audio/quicktime' src='"+src+"'></source>";
						break;
					case '.mkv':
						source="<source type='audio/quicktime' src='"+src+"'></source>";
						break;
					case '.mp4':
						source="<source type='audio/mp4' src='"+src+"'></source>";
						break;
					case '.avi':
						source="<source type='video/x-msvideo' src='"+src+"'></source>";
						break;
					case '.webm':
						source="<source type='video/webm' src='"+src+"'></source>";
						break;
					default:
						break;
					}
					
					var endvideo="</video>"	;
					shower.append(videohtml+source+endvideo);
					
					var ul = $('.pub > .op > .area > .pics > .items').first();
					var videohtml2="<video fn='"+fn+"' class='video-js  vjs-default-skin' preload='auto' poster='#' data-setup='{}'>";
					var lihtml = "<li media>"+videohtml2+source+endvideo+"</li>";
					ul.append(lihtml);
					
					//video.show();
//					var v=videojs('my-video');
//					v.play();
					
					fileE.remove();
				},
				error : function(data, status, e) {
					areaImg.remove();
					fileE.remove();
					alert(e);
				}
			});

		})
		fileE.trigger('click');

		return false;
	}
	function uploadFile(e) {
		// 发送图片，即传即用,不是在发放文件时一次上传，而是分开,采用formData发送
		var sendButton = $('.pub > .op > .right');
		var fileId = 'file_' + fileCount;
		fileCount++;
		sendButton
				.append("<input id='"
						+ fileId
						+ "' name='"
						+ fileId
						+ "' type='file' style='display:none;' accept='.rar,.zip,.jar,.doc,.docx,.txt,.java,.js,.html' data-max_size='2000000'>");
		var fileE = sendButton.children('#' + fileId);
		fileE.on('change', function(file) {
			if (fileE.val() == '') {
				fileE.remove();
				return false;
			}

			var ul = $('.pub > .op > .area > .files').first();
			var li = ul.children('li').first().clone();
			li.children('img').attr('src', 'img/waiting.gif');
			if (li.is(':hidden')) {
				li.attr('style', 'display:inline-block;');
			}
			ul.append(li);
			var url = './swssite/microblog/uploadFiles.service';
			$.ajaxFileUpload({
				url : url, // 用于文件上传的服务器端请求地址
				secureuri : false, // 一般设置为false
				fileElementId : fileId,
				dataType : 'json',// 返回值类型 一般设置为json
				data : {},
				success : function(data, status) {
					li.attr('fn', data.fn);
					li.attr('title', '文件名:' + data.old);
					li.children('img').attr('src',
							'./swssite/microblog/img/file.svg');
					fileE.remove();
				},
				error : function(data, status, e) {
					li.hide();
					fileE.remove();
					alert(e);
				}
			});

		})
		fileE.trigger('click');

		return false;
	}
})();