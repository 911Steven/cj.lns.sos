(function() {
	$('.l-c>.right>.users>li').on('click', function() {
		var task = $(this).parents('div.panel[task]');
		var selUser = task.find('#select-user');

		var sib = selUser.siblings('span[all]');
		var ch = sib.find('input');
		ch.removeAttr("disabled");
		ch.removeAttr("checked");

		var a = selUser.find('>a');
		var uid = $(this).attr('uid');
		a.attr('uid', uid);
		a.html($(this).find('>.intro>.f1').html());
	});
	
	
	$('.l-c>.left>.filter>.set>div>span[all]').on('click', function() {
		var sib = $(this).siblings('#select-user');
		$(this).find('input').attr("disabled", 'disabled');

		var a = sib.find('>a');
		a.removeAttr('uid');
		a.empty();
	});
	$('#session-tasks>.panels>.panel>.ctx .l-c>.left>.filter>.sender>div>#msgInput')
			.on('keyup',
					function(e) {
						var sel = $(this).parents('li.sender').find(
								'input[name=radio2]:checked');
						if (sel.attr('sel') == 'enter') {
							if (e.keyCode == '13' && !e.ctrlKey) {
								var task = $(this).parents('div.panel[task]');
								var sid = $(
										'#session-tasks>li[task='
												+ task.attr('task') + ']')
										.attr('sid');
								var msgbox = task.find('>.l-c>.left>.cnt');
								var selUser = task.find('#select-user>a').attr(
										'uid');
								var sender = task.find(
										'.l-c>.left>.filter>.sender').attr(
										'sender');
								var msgtext=task.find('textarea#msgInput');
								var msg = msgtext.val();
								onSend(sender, sid, selUser, task, msgbox, msg);
								msgtext.val('');
							}
						} else if (sel.attr('sel') == 'ctr+enter') {
							if (e.keyCode == '13' && e.ctrlKey) {
								var task = $(this).parents('div.panel[task]');
								var sid = $(
										'#session-tasks>li[task='
												+ task.attr('task') + ']')
										.attr('sid');
								var msgbox = task.find('>.l-c>.left>.cnt');
								var selUser = task.find('#select-user>a').attr(
										'uid');
								var sender = task.find(
										'.l-c>.left>.filter>.sender').attr(
										'sender');
								var msgtext=task.find('textarea#msgInput');
								var msg = msgtext.val();
								onSend(sender, sid, selUser, task, msgbox, msg);
								msgtext.val('');
							}
						}

					});
	function onSend(sender, sid, toUser, task, msgbox, msg) {
		if(msg==''){
			return;
		}
		var swsid=$('#device').attr('swsid');
		var win = document.getElementById("device").contentWindow
		var frame = 'command=push\r\nurl=/device/pushMsgService/?sid=' + sid
				+ '&sender=' + sender + '&selectUser=' + toUser
				+ '&swsid='+swsid+'\r\nprotocol=peer/1.0\r\n\r\n\r\n' + msg;
		win.postMessage(frame, '*');
	}
})();