$(document)
		.ready(
				function() {
					var content = $(".register>div[flows]>div[flowId='1']")
							.html();
					$('.register>.content').html(content);

					var register = {
						service : '../pages/register.service',
						data : {
							user : {},
							diskSet : {},
							swsSet : {
							// swsId:999,inheritId:2222
							},
							profile : {}
						},

						genSwsNum : function(assignedNum, swsList,
								templateSwsId) {
							$
									.post(
											register.service
													+ '?action=genSwsNum',
											{
												assignedNum : assignedNum
											},
											function(data) {
												var d = $.parseJSON(data);
												var sws = register.data.swsSet;
												swsList.attr('newswsid',
														d.wholeNum);
												var swsSize=register.data.swsSet.size;
												var len=$('.register>.content>.c-left>ul>li[newswsid]').length;
												var size=swsSize/len;
												$(".content ul[name='swsTemplateList']>li[swsSize]").each(function(i,e){
													$(e).attr('swsSize',
															size);
													$(e).find('#displaysize').html('大小:'+size);
												});
												
												swsList.attr('swsSize',size);
												swsList
														.append('<p style=\'font-size:12px;\'>编号:'
																+ d.wholeNum
																+ '</p>');
												swsList
														.append('<p id=displaysize style=\'font-size:12px;\'>大小:'
																+ size
																+ 'G</p>');
												$(
														".content #swsNum>.auto>input")
														.val(d.reedomNum);
											});
						},
						setForm : function(flow) {
							var f = parseInt(flow);
							var cnt = $('.content');
							if (f == 1) {
								var user = register.data.user;
								user.userCode = cnt.find('#accountName input')
										.val();
								user.nickName = cnt.find('#nickName input')
										.val();
								user.password = cnt.find('#password input')
										.val();
								user.sex = cnt.find(
										"#sex input[name='sex']:checked").val();
								user.photoUrl = cnt.find('#file').val();
								if (user.userCode == '') {
									cnt.find('#accountName input').css(
											'border', '1px solid red');
									return false;
								}
								if (user.nickName == '') {
									cnt.find('#nickName input').css('border',
											'1px solid red');
									return false;
								}
								if (user.password == '') {
									cnt.find('#password input').css('border',
											'1px solid red');
									return false;
								}
								var error = false;
								$.ajax({
									type : 'POST',
									url : '../pages/registerUser.service',
									data : user,
									success : function(data) {
									},
									error : function(e) {
										error = true;
										alert('error');
									},
									async : false
								});
								return !error;
							} else if (f == 2) {
								var disk = register.data.diskSet;
								var user = register.data.user;
								disk.diskSize = cnt.find('#diskSize').val();
								disk.homeSize = cnt.find('#homeSize').val();
								if(disk.diskSize==''){
									cnt.find('#diskSize').css('border','1px solid red');
									return false;
								}
								if(disk.homeSize==''){
									cnt.find('#homeSize').css('border','1px solid red');
									return false;
								}
								if (isNaN(disk.homeSize)) {
									return false;
								}
								var size=parseFloat(disk.diskSize)-parseFloat(disk.homeSize);
								register.data.swsSet.size=size;
								var error = false;
								$.ajax({
									type : 'POST',
									url : '../pages/registerDiskSet.service',
									data : {
										disk : disk,
										user : user,
										userState : 1
									},
									success : function(data) {
									},
									error : function(e) {
										error = true;
										alert('error');
									},
									async : false
								});
								return !error;
							} else if (f == 3) {
								var swsSet = register.data.swsSet;
								// var newSwsIds = sws.newSwsIds;
								var newswsList = cnt
										.find("> .c-left > ul > li[newswsid]");
								if(newswsList.length==0){
									alert('未选视窗');
									return false;
								}
								var swsArr = [];
								newswsList
										.each(function(i, e) {
											var sws = {};
											sws.swsid = $(e).attr('newswsid');
											sws.swstid = $(e).attr('swsid');
											sws.swsSize = parseFloat($(e).attr('swssize'));
											swsArr.push(sws);
										});
								swsSet.list=swsArr;
								
								var user = register.data.user;
								var error = false;
								var data={
										swsSet : swsSet,
										user : user,
										userState : 2
									};
								var dataJson=JSON.stringify(data);
								$.ajax({
									type : 'POST',
									url : '../pages/registerSwsSet.service',
									data : {json:dataJson},
									success : function(data) {
									},
									error : function(e) {
										error = true;
										alert('error');
									},
									async : false
								});
								return !error;
							} else if (f == 4) {//头像
								
							}

						}
					};
					$('.content').delegate(
							'input[required]',
							'blur',
							function() {
								var v = $(this).val();
								if (typeof v == 'undefined' || v == '') {
									$(this).attr('style',
											'border:1px solid red;');
								}
							});
					$('.content').delegate(
							'input[required]',
							'focus',
							function() {
								$(this).attr('style',
										'border:1px solid #bfbfbf;');
							});
					$('.content')
							.delegate(
									'.disk>div>input',
									'keyup',
									function() {
										var oldborder = $(this).css('border');
										if (isNaN($(this).val())
												|| $(this).val() == '') {
											$(this).css('border',
													"1px solid red");
											$(this).val(0.0);
										} else {
											$(this).css('border', oldborder);
											var size = parseFloat($('#diskSize')
													.val())
													- parseFloat($('#homeSize')
															.val());
											$('.register > .content  #swsSize').html(size);
										}

									});
					$('.content')
							.delegate(
									'#swsNum>li.genNum',
									'click',
									function() {
										var list = $(this)
												.parents('.cr-swsNum').find(
														'li>.updown');
										var str = '';
										for (var i = 0; i < list.length; i++) {
											str += $(list.get(i)).find(
													'li>input').val()
													+ '';
										}
										var swsTemplate = $('.content .c-r-t>p>#swsName');
										var swsid = swsTemplate.attr('swsid');
										if (typeof swsid == 'undefined') {
											alert('未选中视窗模板');
										}
										var swsList = $(".content ul[name='swsTemplateList']>li[swsId='"
												+ swsid + "']");
										swsList.find('p').remove();

										register.genSwsNum(str, swsList, swsid);
									});
					$('.content')
							.delegate(
									"ul[name='swsTemplateList']>li",
									'click',
									function() {
										var input = $(this).find('input');
										if (input.prop('checked')) {
											input.prop('checked', false);
										} else {
											input.prop('checked', true);
										}
										var swsName = $(this).find(
												'span.swsName').html();
										var swsId = $(this).attr('swsId');
										var selectSws = $('.content .c-r-t>p>span');
										selectSws.attr('swsId', swsId);
										selectSws.html(swsName);
										var selDesc = $('.content .c-r-t>p:last-child');
										selDesc.html($(this).attr('desc'));
									});
					$('.content').delegate(
							'.updown>.up',
							'click',
							function() {
								var val = $(this).parent('.updown').find(
										'li>input').val();
								var pos = parseInt(val);
								pos++;
								if (pos < 10) {
									$(this).parent('.updown').find('li>input')
											.val(pos);
								}
							});
					$('.content').delegate(
							'.updown>.down',
							'click',
							function() {
								var val = $(this).parent('.updown').find(
										'li>input').val();
								var pos = parseInt(val);
								pos--;
								if ($(this).attr('first')) {
									if (pos < 6) {
										return;
									}
								}
								if (pos > 0) {
									$(this).parent('.updown').find('li>input')
											.val(pos);
								}
							});

					$('#flow')
							.delegate(
									'li',
									'click',
									function() {
										var currFlow = parseInt($(
												'.register>.flow>ul>li.selected')
												.attr("flow"));
										var selectFlow = $(this).attr('flow');
										if (selectFlow=='finish') {//完成，可跳转页面
											if (false == register
													.setForm(currFlow))
												return;
										}
										if (selectFlow == 'next') {
											if (false == register
													.setForm(currFlow))
												return;
										}
										if (selectFlow == 'next') {
											currFlow++;
										} else if (selectFlow == "prev") {
											currFlow--;
										}
										if (currFlow == 1) {
											$('#flow').html(
													"<li flow='next'>下一步</li>");
											content = $(
													".register>div[flows]>div[flowId='"
															+ currFlow + "']")
													.html();
											$('.register>.content').html(
													content);
										} else if (currFlow == 2) {
											$('#flow').html('');
											$('#flow').append(
													"<li flow='next'>下一步</li>");

											content = $(
													".register>div[flows]>div[flowId='"
															+ currFlow + "']")
													.html();
											$('.register>.content').html(
													content);
										} else if (currFlow == 3) {
											$('#flow').html('');
											$('#flow').append(
													"<li flow='next'>下一步</li>");
											//$('#flow').append(
											//"<li finish flow='finish'>完成</li>");
											content = $(
													".register>div[flows]>div[flowId='"
															+ currFlow + "']")
													.html();
											$('.register>.content').html(
													content);
										} else if (currFlow == 4) {
											$('#flow').html('');
											$('#flow').append(
													"<li finish flow='finish'>完成</li>");
											//$('#flow').append(
											//"<li flow='prev'>上一步</li>");
											content = $(
													".register>div[flows]>div[flowId='"
															+ currFlow + "']")
													.html();
											$('.register>.content').html(
													content);
										} 
										$(
												".register>.flow>ul>li[class='selected']")
												.attr('class', '');
										$(
												".register>.flow>ul>li[flow='"
														+ currFlow + "']")
												.attr('class', 'selected');
									});
				});