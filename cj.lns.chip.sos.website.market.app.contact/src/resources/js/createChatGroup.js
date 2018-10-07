(function(){
	var save=$('.contact-panel > .ct-main > .ct-box > .ct-right');
	
	save.undelegate('.cg-panel>ul>li[label=pic]', 'click');
	save.delegate('.cg-panel>ul>li[label=pic]', 'click',function(e){
				var the=$(this);
				var panel=$(this).parents('ul');
				$(panel).find('#gfile').remove();
				$(panel).append("<input id='gfile' name='gfile' style='display:none;' type='file' >");
				$(panel).find('#gfile').trigger('click');
				$(panel).find('#gfile').on('change',function(e){
					$.ajaxFileUpload({
						url : './contact/uploadPictures.service',
						secureuri : false, // 一般设置为false
						fileElementId : 'gfile',
						dataType : 'json',// 返回值类型 一般设置为json
						data : {},
						success : function(obj, status) {
							panel.find('>li[label=pic]').append("<p>已成功上传</p>");
							panel.find('>li[label=pic]').attr('path',obj.path);
							the.parents('.popup-panel').show();
						},
						error : function(e) {
							alert(e.responseText);
							the.parents('.popup-panel').show();
						}
						});

				});
			});
	
	save.undelegate(
			'.cg-panel>.op>.save', 'click');
	save.delegate(
			'.cg-panel>.op>.save', 'click', function(e) {
				var nameE=$('.cg-panel>ul>li[label=name]>input');
				var name=nameE.val();
				if(name==''||typeof name=='undefined')	{
					nameE.css('border','1px solid red');
					return false;
				}
				var intrE=$('.cg-panel>ul>li[label=introduce]>textarea');
				var intr=intrE.val();
				if(intr==''||typeof intr=='undefined'){
					intrE.css('border','1px solid red');
					return false;
				}
				var gnum=$('.cg-panel>ul>li[label=num]>input').val();
				var pic=$('.cg-panel>ul>li[label=pic]').attr('path');
				if(pic==null||typeof pic=='undefined'){
					alert('必须为群上传群logo');
					return;
				}
				var url='./contact/createChatGroup.service?action='+'create';
				url=encodeURI(url);
				var the=$(this);
				$.post(url,{name:name,introduce:intr,num:gnum,path:pic},function(){
					//回到群组页面
					$.get('./contact/chatGroup.html',
							{}, function(data) {
								var cnt=the.parents('#ct-content');
								cnt.empty();
								cnt.html(data);
							}).error(function(e){
								alert(e.responseText);
							});
				});
				
			});
	var ret=$('.contact-panel > .ct-main > .ct-box > .ct-right');
	ret.undelegate(	'.cg-panel>.op>.ret', 'click');
	ret.delegate('.cg-panel>.op>.ret', 'click', function(e) {
				var the=$(this);
				//回到群组页面
				$.get('./contact/chatGroup.html',
						{}, function(data) {
							var cnt=the.parents('#ct-content');
							cnt.empty();
							cnt.html(data);
						});
			});
})();