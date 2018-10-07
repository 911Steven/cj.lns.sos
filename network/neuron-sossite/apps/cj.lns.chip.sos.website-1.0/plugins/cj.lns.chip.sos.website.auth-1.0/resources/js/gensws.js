(function() {
	$('.new-sws > .typed').undelegate('span[label]', 'click');
	$('.new-sws > .typed').delegate('span[label]', 'click', function(e) {
		if (typeof $(this).parent('li').attr('swsid') != 'undefined') {
			return;
		}
		$(this).hide();
		$(this).siblings('input').focus();
	});
	$('.new-sws > .typed')
			.undelegate('>ul>li>input , >ul>li>textarea', 'focus');
	$('.new-sws > .typed').delegate('>ul>li>input , >ul>li>textarea', 'focus',
			function(e) {
				$(this).siblings('span[label]').hide();
			});
	$('.new-sws > .typed').undelegate('>ul>li>input , >ul>li>textarea', 'blur');
	$('.new-sws > .typed').delegate('>ul>li>input , >ul>li>textarea', 'blur',
			function(e) {
				if ($(this).val() == '') {
					var label = $(this).siblings('span[label]');
					label.show();
				}
			});

	$(".new-sws > .typed > .face>p>input")
			.on(
					'change',
					function preview(e) {
						var prevDiv = $(".new-sws > .typed > .face>img");
						var file = $(this);
						if (file.prop('files') && file.prop('files')[0]) {
							var reader = new FileReader();
							reader.onload = function(evt) {
								prevDiv.attr('src', evt.target.result);
							}
							reader.readAsDataURL(file.prop('files')[0]);
						} else {
							prevDiv
									.attr(
											'style',
											"filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale,src=\'"
													+ file.val() + "\'");
						}
					});
	function rndNum() {
		var fixed = '6';
		var ret = Math.floor(Math.random() * 100);
		var ret2 = Math.floor(Math.random() * 1000);
		return fixed + '' + ret + '' + fixed + '' + ret2;
	}
	$('.new-sws > .typed > .fields > li[swsid] > span[label] > a').on(
			'click',
			function(e) {
				var input = $(this).parents('li[swsid]').children('input');
				var label = $(this).parents('li[swsid]')
						.children('span[label]');
				var assignedNum = rndNum();
				$.get('../servicews/genSwsNum.service', {
					assignedNum : assignedNum
				}, function(data) {
					data = $.parseJSON(data);
					var obj = eval(data);
					input.val(obj.wholeNum);
					label.hide();
				}).error(function(e) {
					alert('error:' + e.responseText);
				});
			});
	
	$('.new-sws > .typed > .op > li[op=apply]')
			.on(
					'click',
					function(e) {
						var level = $(this).attr('level');
						var inheritId = $('.new-sws > .typed')
								.attr('inheritId');
						var swsid = $('.new-sws > .typed > .fields > li[swsid] > input');
						var swsName = $('.new-sws > .typed > .fields > li[name] > input');
						var capacity = $('.new-sws > .typed > .fields > li[capacity] > input');
						var face = $(".new-sws > .typed > .face>p>input");
						var the = $(this);
						face.attr('id', 'faceImg');
						the.attr('disabled', 'disabled');
						if (face.val() == '') {
							the.removeAttr('disabled');
							alert('请为视窗设logo');
							return;
						}
						if (swsid.val() == '') {
							the.removeAttr('disabled');
							alert('请先生成视窗号');
							return;
						}
						if (swsName.val() == '') {
							the.removeAttr('disabled');
							alert('请指定视窗名');
							return;
						}
						if (the.prop("disabled")) {
							alert('');
							the.removeAttr('disabled');
							return;
						}
						the.html('稍候...');
						level = parseInt(level);
						switch (level) {
						case 1:// 生成基础视窗
							var url = '../servicews/buildBasicSws.service';
							var desc = $('.new-sws > .typed > .extra > li[desc] > input');
							var platform = $('.new-sws > .typed > .extra > li[platform] > input');
							if(platform.val()==''){
								the.removeAttr('disabled');
								alert('平台号不能为空');
								return;
							}
							if (capacity.val() == '' || isNaN(capacity.val())
									|| parseInt(capacity.val()) > 100) {
								the.removeAttr('disabled');
								alert('请指定网盘限额，数字，不能超过5G');
								return;
							}
							var cap=parseInt(capacity.val());
							if(cap!=-1){
								cap=cap*1024*1024*1024;
							}
							
							//采用ajaxFileUpload来一次性提交以生成视窗，因为它可以赋参数，采用的是formData协议
							$.ajaxFileUpload({
								url : url, // 用于文件上传的服务器端请求地址
								secureuri : false, // 一般设置为false
								fileElementId : 'faceImg', 
								dataType : 'json',// 返回值类型 一般设置为json
								data:{
									level : level,
									inheritId : inheritId,
									desc : desc.val(),
									swsid : swsid.val(),
									swsName : swsName.val(),
									platform:platform.val(),
									capacity:cap
								},
								success : function(data, status) {
									the.html('视窗生成成功');
									var basicpanel=$('.sws-win > .center > .kind[bsws]>.list');
									var src='../resource/ud/'+data.faceImg+'?path='+data.id+'://system/faces/&u='+data.owner;
									var li="<li swsid='"+data.id+"'><img src='"+src+"'><p>"+data.name+"</p></li>";
									basicpanel.append(li);
									$('.sws-win > .popup').hide();
								},
								error : function(data, status, e){
									the.html('失败,再试一次');
									swsid.val('');
									$('.new-sws > .typed > .fields > li[swsid]>span[label]').show();
									the.removeAttr('disabled');
								}
							});
							
							break;
						case 2:// 生成公共视窗
							var url = '../servicews/buildCommonSws.service';
							var desc = $('.new-sws > .typed > .extra > li[desc] > input');
							if (capacity.val() == '' || isNaN(capacity.val())
									|| parseInt(capacity.val()) > 100) {
								alert('请指定网盘限额，数字，不能超过5G');
								the.removeAttr('disabled');
								return;
							}
							var cap=parseInt(capacity.val());
							if(cap!=-1){
								cap=cap*1024*1024*1024;
							}
							
							var crop = $('.new-sws > .typed > .extra > li[crop] > input');
							if(crop.val()==''){
								the.removeAttr('disabled');
								alert('请填公司或组织名字');
								return;
							}
							var address = $('.new-sws > .typed > .extra > li[address] > input');
							if(address.val()==''){
								the.removeAttr('disabled');
								alert('请填公司或组织地址');
								return;
							}
							var home = $('.new-sws > .typed > .extra > li[home] > input');
							var intro = $('.new-sws > .typed > .extra > li[intro] > textarea');
							if(intro.val()==''){
								the.removeAttr('disabled');
								alert('请填写简介：可以是服务范围、经营等');
								return;
							}
							var introstr=intro.val();
							if(introstr!=''){
								introstr.replace(/\r\n/,'<br>');
							}
							//采用ajaxFileUpload来一次性提交以生成视窗，因为它可以赋参数，采用的是formData协议
							$.ajaxFileUpload({
								url : url, // 用于文件上传的服务器端请求地址
								secureuri : false, // 一般设置为false
								fileElementId : 'faceImg', 
								dataType : 'json',// 返回值类型 一般设置为json
								data:{
									level : level,
									inheritId : inheritId,
									swsid : swsid.val(),
									swsName : swsName.val(),
									capacity:cap,
									crop:crop.val(),
									address:address.val(),
									home:home.val(),
									intro:intro.val()
								},
								success : function(data, status) {
									the.html('视窗生成成功');
									var basicpanel=$('.sws-win > .center > .kind[csws]>.list');
									var src='../resource/ud/'+data.faceImg+'?path='+data.id+'://system/faces/&u='+data.owner;
									var li="<li swsid='"+data.id+"'><img src='"+src+"'><p>"+data.name+"</p></li>";
									basicpanel.append(li);
									$('.sws-win > .popup').hide();
									the.removeAttr('disabled');
								},
								error : function(data, status, e){
									the.html('失败,再试一次');
									swsid.val('');
									$('.new-sws > .typed > .fields > li[swsid]>span[label]').show();
									the.removeAttr('disabled');
								}
							});
							break;
						case 3:// 生成个人视窗
							var url = '../servicews/buildPersonSws.service';
							if (capacity.val() == '' || isNaN(capacity.val())
									|| parseInt(capacity.val()) > 6||parseInt(capacity.val())<0 ) {
								the.removeAttr('disabled');
								alert('请指定网盘限额，数字，不能超过5G');
								return;
							}
							var cap=parseInt(capacity.val());
							if(cap!=-1){
								cap=cap*1024*1024*1024;
							}
							var hobby = $('.new-sws > .typed > .extra > li[hobby] > input');
							if(hobby.val()==''){
								the.removeAttr('disabled');
								alert('请填兴趣爱好');
								return;
							}
							var intro = $('.new-sws > .typed > .extra > li[intro] > textarea');
							if(intro.val()==''){
								the.removeAttr('disabled');
								alert('请填简介');
								return;
							}
							//采用ajaxFileUpload来一次性提交以生成视窗，因为它可以赋参数，采用的是formData协议
							$.ajaxFileUpload({
								url : url, // 用于文件上传的服务器端请求地址
								secureuri : false, // 一般设置为false
								fileElementId : 'faceImg', 
								dataType : 'json',// 返回值类型 一般设置为json
								data:{
									level : level,
									inheritId : inheritId,
									swsid : swsid.val(),
									swsName : swsName.val(),
									capacity:cap,
									hobby:hobby.val(),
									intro:intro.val()
								},
								success : function(data, status) {
									the.html('视窗生成成功');
									var personpanel=$('.sws-win > .center > .kind[psws]>.list');
									var src='../resource/ud/'+data.faceImg+'?path='+data.id+'://system/faces/&u='+data.owner;
									var li="<li swsid='"+data.id+"'><img src='"+src+"'><p>"+data.name+"</p></li>";
									personpanel.append(li);
									$('.sws-win > .popup').hide();
								},
								error : function(data, status, e){
									the.html('失败,再试一次');
									swsid.val('');
									$('.new-sws > .typed > .fields > li[swsid]>span[label]').show();
									the.removeAttr('disabled');
								}
							});
							break;
						}
					}).error(function(e) {
				alert('error:' + e.responseText);
			});
})();