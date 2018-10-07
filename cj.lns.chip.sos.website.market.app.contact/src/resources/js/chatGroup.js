(function() {
	var parr = $('.contact-panel > .ct-main > .ct-box > .ct-right');
	parr.undelegate('.chat-g>.chat-g-my>.chat-g-list>li.g', 'click');
	parr.delegate('.chat-g>.chat-g-my>.chat-g-list>li.g', 'click', function(e) {
		e.stopPropagation();
		var the = $(this);
		if ($(this).attr('op') == 'add') {
			// 在sossite上打开创建群的选项
			$.get('./contact/createChatGroup.service', {}, function(data) {
				the.parents('#ct-content').html(data);
			});
			return false;
		}
		// 其下：在device上打开群的会话，调用:document.getElementById('device').contentWindow.postMessage(frame,"*");

	});
	parr.undelegate('.chat-g>.chat-g-my>.chat-g-list>li.g', 'mouseenter');
	parr.delegate('.chat-g>.chat-g-my>.chat-g-list>li.g', 'mouseenter',
			function(e) {
				var the = $(this);
				if (the.attr('op') == 'add') {
					return false;
				}
				the.find('span.close').show();
			});
	parr.undelegate('.chat-g>.chat-g-my>.chat-g-list>li.g', 'mouseleave');
	parr.delegate('.chat-g>.chat-g-my>.chat-g-list>li.g', 'mouseleave',
			function(e) {
				var the = $(this);
				if (the.attr('op') == 'add') {
					return false;
				}
				the.find('span.close').hide();
			});
	parr.undelegate('.chat-g>.chat-g-my>.chat-g-list>li.g>span.close', 'click');
	parr.delegate('.chat-g>.chat-g-my>.chat-g-list>li.g>span.close', 'click',
			function(e) {
				var the = $(this);
				if (the.attr('op') == 'add') {
					return false;
				}
				$.get('./contact/deleteChatGroup.service', {
					gid : the.parent('li.g').attr('gid')
				}, function(data) {
					the.parent('li.g').remove();
				});
			});
	
	//其下处理我加入的群
	parr.undelegate('.chat-g>.chat-g-jo>.chat-g-list>li.g', 'click');
	parr.delegate('.chat-g>.chat-g-jo>.chat-g-list>li.g', 'click', function(e) {
		e.stopPropagation();
		var the = $(this);
		if ($(this).attr('op') == 'find') {
			// 在sossite上打开创建群的选项
			$.get('./contact/findChatGroup.service', {}, function(data) {
				the.parents('#ct-content').html(data);
			});
			return false;
		}
		// 其下：在device上打开群的会话，调用:document.getElementById('device').contentWindow.postMessage(frame,"*");

	});
	parr.undelegate('.chat-g>.chat-g-jo>.chat-g-list>li.g', 'mouseenter');
	parr.delegate('.chat-g>.chat-g-jo>.chat-g-list>li.g', 'mouseenter',
			function(e) {
				var the = $(this);
				if (the.attr('op') == 'find') {
					return false;
				}
				the.find('span.close').show();
			});
	parr.undelegate('.chat-g>.chat-g-jo>.chat-g-list>li.g', 'mouseleave');
	parr.delegate('.chat-g>.chat-g-jo>.chat-g-list>li.g', 'mouseleave',
			function(e) {
				var the = $(this);
				if (the.attr('op') == 'find') {
					return false;
				}
				the.find('span.close').hide();
			});
	parr.undelegate('.chat-g>.chat-g-jo>.chat-g-list>li.g>span.close', 'click');
	parr.delegate('.chat-g>.chat-g-jo>.chat-g-list>li.g>span.close', 'click',
			function(e) {
				var the = $(this);
				if (the.attr('op') == 'find') {
					return false;
				}
				$.get('./contact/unjoinChatGroup.service', {
					gid : the.parent('li.g').attr('gid')
				}, function(data) {
					the.parent('li.g').remove();
				});
			});
	parr.undelegate('.chat-g-list>li.g','click');
	parr.delegate('.chat-g-list>li.g','click',function(e){
		var the = $(this);
		if (the.attr('op') == 'add') {
			return false;
		}
		var linkerurl = "./servicews/session/chatGroup.service";
		$.getJSON(linkerurl, {
			'gid' : the.attr('gid')
		}, function(session) {
			var openurl = "./servicews/session/openSession.html";
			$.get(openurl, {
				sid : session.sid
			}, function(html) {
				the.showWin(html, null, session);
			});
		});
	});
})();