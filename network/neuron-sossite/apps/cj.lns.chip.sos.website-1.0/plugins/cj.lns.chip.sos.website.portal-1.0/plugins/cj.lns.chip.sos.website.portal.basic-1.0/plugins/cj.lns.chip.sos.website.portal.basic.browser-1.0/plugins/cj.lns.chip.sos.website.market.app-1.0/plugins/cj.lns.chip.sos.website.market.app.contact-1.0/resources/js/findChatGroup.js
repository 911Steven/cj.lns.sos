(function() {
	var findE = $('.contact-panel > .ct-main > .ct-box > .ct-right');
	findE.undelegate('.find-g>.op>input', 'mouseenter mouseleave');
	findE.delegate('.find-g>.op>input', 'mouseenter mouseleave', function(e) {
		if (e.type == 'mouseenter') {
			$(this).parent('.op').children('.tips').hide();
		} else if (e.type == 'mouseleave') {
			var input = $(this).parent('.op').children('input').val();
			if (typeof input == 'undefined' || input == '') {
				$(this).parent('.op').children('.tips').show();
			}
		}
	});
	findE.undelegate('.find-g>.result>.gitem>.gpanel', 'click');
	findE.delegate('.find-g>.result>.gitem>.gpanel', 'click', function(e) {
		e.stopPropagation();
		return false;
	});
	findE.undelegate('.find-g>.result>.gitem', 'click');
	findE.delegate('.find-g>.result>.gitem', 'click', function(e) {
		var panel = $(this).find('.gpanel');
		panel.html('loadding...');
		if (panel.is(":hidden")) {
			var url = './contact/findChatGroup.service?action=view';
			$.get(url, {
				gid : $(this).attr('gid')
			}, function(data) {
				panel.html(data);
			});
		}
		panel.toggle();
		return false;
	});
	findE.undelegate('.find-g>.result>.gitem>img.join', 'click');
	findE.delegate('.find-g>.result>.gitem>img.join', 'click', function(e) {
		var group=$(this).parents('.gitem');
		var security=group.attr('security');//暂不实现加入时验证
		var gid=group.attr('gid');
		var url = './contact/joinChatGroup.service?gid='+gid;
		$.get(url, {}, function(data) {
			var button=group.children('.join');
			button.title=data;
			button.hide();
		});
		return false;
	});

	findE.undelegate('.find-g>.op>img', 'click');
	findE.delegate('.find-g>.op>img', 'click', function(e) {
		var input = $(this).parent('.op').children('input');
		var url = './contact/findChatGroup.service?action=find';
		var by = 1; // 0是群号，1是群名
		var v = input.val();
		if (!isNaN(v)) {
			by = 0;
		} else {
			by = 1;
		}
		$('.find-g>.result').html('loadding...');
		$.post(url, {
			by : by,
			value : v
		}, function(data) {
			$('.find-g>.result').html(data);
		});
		return false;
	});
})();