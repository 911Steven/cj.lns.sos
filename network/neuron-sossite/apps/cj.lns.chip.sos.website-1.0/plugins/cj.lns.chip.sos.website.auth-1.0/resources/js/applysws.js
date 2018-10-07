(function() {
	var topop = $('.new-sws > .left > .top > .op');
	topop.children('span').on('click', function(e) {
		$(this).hide();
		topop.children('input').focus();
	});
	topop.children('input').focus(function(e) {
		topop.children('span').hide();
	});
	topop.children('input').blur(function(e) {
		if (topop.children('input').val() == '')
			topop.children('span').show();
	});
	$('.new-sws > .left').undelegate('> .sws-table > li > .face > li ',
			'click');
	$('.new-sws > .left').delegate(
			'> .sws-table > li > .face > li ',
			'click',
			function(e) {
				$('.new-sws > .typed').hide();
				var swsid = $(this).parents('li[swsid]').attr('swsid');
				var url = '../servicews/getServicewsBody2.service';
				$.get(
						url,
						{
							swsid : swsid
						},
						function(data) {
							data = $.parseJSON(data);
							var obj = eval(data);
							var face = $('.new-sws > .right > .face');
							face.children('img').attr('src', obj.faceImg);
							face.children('p').html(obj.swsName);
							var box = $('.new-sws > .right > .detail');
							box.find('>li[owner]>span[value]').html(
									obj.owner);
							box.find('>li[swsid]>span[value]').html(
									obj.swsid);
							var extra= $('.new-sws > .right > .detail > li.extra>ul');
							var extrali=extra.children('li').first().clone();
							extra.empty();
							var level = '个人视窗';
							switch (obj.level) {
							case 0:
								level = '超级视窗';
								break;
							case 1:
								level = '基础视窗';
								var li='<li platform><span label>平台：</span><span value>'+obj.extra.platform+'</span><span unit>&nbsp</span></li>';
								extra.append(li);
								break;
							case 2:
								level = '公共视窗';
								$('.new-sws>.typed').attr('swsname',obj.swsName);
								
								var li='<li crop><span label>公司/组织：</span><span value>'+obj.extra.crop+'</span><span unit>&nbsp</span></li>';
								extra.append(li);
								li='<li address><span label>地址：</span><span value>'+obj.extra.address+'</span><span unit>&nbsp</span></li>';
								extra.append(li);
								li='<li home><span label>主页：</span><span value>'+obj.extra.home+'</span><span unit>&nbsp</span></li>';
								extra.append(li);
								break;
							case 3:
								level = '个人视窗';
								
								var li='<li hobby><span label>兴趣/爱好：</span><span value>'+obj.extra.hobby+'</span><span unit>&nbsp</span></li>';
								extra.append(li);
								break;
							}
							$('.new-sws>.typed').attr('level',obj.level);
							$('.new-sws>.typed').attr('inheritId',obj.swsid);
							box.find('>li[level]>span[value]').html(level);
							box.find('li[level]>span[value]').html(level);
							if (obj.capacity == -1) {
								box.find('li[capacity]>span[value]').html(
										'不限');
								box.find('li[capacity]>span[unit]').html(
										'&nbsp;')
							} else {
								box.find('li[capacity]>span[value]').html(
										(obj.capacity / 1024 / 1024 / 1024)
												.toFixed(6));
							}
							box.find('li[useSpace]>span[value]').html(
									(obj.useSpace / 1024 / 1024 / 1024)
											.toFixed(6));
							box.find('li[dataSize]>span[value]').html(
									(obj.dataSize / 1024 / 1024 / 1024)
											.toFixed(6));
							
							var intro="<li intro><span label>简介：</span><p>"+obj.extra.intro+"</p></li>"
							extra.append(intro);
							
							var right = $('.new-sws > .right');
							if (right.is(':hidden')) {
								right.show();
							}
						}).error(function(e) {
					alert('error:' + e.responseText);
				});
			});
	$('.new-sws > .right > .op > li[op=apply]').on('click', function(e) {
		//$(this).parents('.popup').hide();
		var typed = $('.new-sws>.typed');
		var level=$('.new-sws>.typed').attr('level');
		var extra= $('.new-sws > .typed > .extra');
		var extrali=extra.children('li').first().clone();
		extra.empty();
		
		var field=$('.new-sws > .typed > .fields');
		field.find('>li[swsid]>input').attr("disabled","disabled");
		var op=$('.new-sws > .typed > .op > li[op=apply]');
		switch(level){
		case "0"://打印基础视窗面板
			typed.find('.title>span[value]').html('基础视窗');
			var li=extrali.clone();
			$(li).attr('platform','');
			$(li).children('span[label]').html('绑定平台号，必填');
			extra.append(li);
			var li=extrali.clone();
			$(li).attr('desc','');
			$(li).children('span[label]').html('说明');
			extra.append(li);
			op.attr('level','1');
			break;
		case "1"://打印公共视窗面板
			op.attr('level','2');
			typed.find('.title>span[value]').html('公共视窗');
			var li=extrali.clone();
			$(li).attr('crop','');
			$(li).children('span[label]').html('公司名或组织名，必填');
			extra.append(li);
			li=extrali.clone();
			$(li).attr('cellphone','');
			$(li).children('span[label]').html('电话，必填');
			li=extrali.clone();
			$(li).attr('address','');
			$(li).children('span[label]').html('地址，必填');
			extra.append(li);
			li=extrali.clone();
			$(li).attr('home','');
			$(li).children('span[label]').html('网址，可选');
			extra.append(li);
			li=extrali.clone();
			$(li).attr('intro','');
			$(li).children('input').remove();
			$(li).append('<textarea></textarea>')
			$(li).children('span[label]').html('简介，必填');
			extra.append(li);
			break;
		case "2"://打印个人视窗面板
			op.attr('level','3');
			typed.find('.title>span[value]').html('个人视窗');
			typed.find('> .face>p').html('&nbsp;');
			var src=$('.new-sws > .right > .face>img').attr('src');
			typed.find('> .face>img').attr('src',src);
			//直接由父视窗的名和给定的容量赋给该子视窗，不需要用户输入
			field.find('>li[name]>input').val(typed.attr('swsname'))
			field.find('>li[name]>input').attr("disabled","disabled");
			field.find('>li[name]>span').hide();
			field.find('>li[capacity]>input').val('5');
			field.find('>li[capacity]>input').attr("disabled","disabled");
			field.find('>li[capacity]>input').attr("title","5g视窗空间");
			field.find('>li[capacity]>span').hide();
			li=extrali.clone();
			$(li).attr('hobby','');
			$(li).children('span[label]').html('兴趣、爱好，必填');
			extra.append(li);
			li=extrali.clone();
			$(li).attr('intro','');
			$(li).children('input').remove();
			$(li).append('<textarea></textarea>')
			$(li).children('span[label]').html('简介，必填');
			extra.append(li);
			break;
		}
		if (typed.is(':hidden')) {
			typed.show();
		}
	});

	var limit = 10;
	var skip = limit;
	var polling = false;

	var searcher = $('.new-sws > .left > .top > .op>input');
	searcher.keydown(function(e) {
		if (e.keyCode == 13) {
			skip = 0;
			var url = '../auth/applyServicews.html';
			var kind = $('.new-sws > .left > .top > .label').attr('kind');
			var where = searcher.val();
			var swsid = '';
			var swsName = '';
			if (isNaN(where)) {
				swsName = where;
			} else {
				swsid = where;
			}
			polling = true;
			$.get(url, {
				kind : kind,
				skip : skip,
				limit : limit,
				swsid : swsid,
				swsName : swsName,
				search:true
			}, function(data) {
				var msg_list = $('.new-sws > .left > .sws-table');
				msg_list.html(data);
				skip = skip + limit;
				polling = false;
			});
		}
	});

	$('.new-sws > .left > .sws-table')
			.on(
					'scroll',
					function() {
						var msg_list = $(this);
						if (msg_list.height() + msg_list[0].scrollTop >= msg_list[0].scrollHeight - 60) {
							var url = '../auth/applyServicews.html';
							var kind = $('.new-sws > .left > .top > .label')
									.attr('kind');
							var where = searcher.val();
							var swsid = '';
							var swsName = '';
							if (isNaN(where)) {
								swsName = where;
							} else {
								swsid = where;
							}
							if (!polling) {
								polling = true;
								$.get(url, {
									kind : kind,
									skip : skip,
									limit : limit,
									swsid : swsid,
									swsName : swsName
								}, function(data) {
									msg_list.append(data);
									skip = skip + limit;
									polling = false;
								});
							}
						}
					});

})();