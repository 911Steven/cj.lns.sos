(function(){
	var win=$('.windows>.win-content');
	win.undelegate('.home-panel > .title > .pos>span[label]','click');
	win.delegate('.home-panel > .title > .pos>span[label]','click',function(e){
		var oldvalueid=$('.windows > .titleBar[valueid]').attr('valueid');
		var valueid=oldvalueid;
		if(valueid.indexOf('app://')==0){
			if(valueid.indexOf('?')>0){
				valueid=valueid.substring(6,valueid.indexOf('?'));
			}else{
				valueid=valueid.substring(6,valueid.length);
			}
		}else if(valueid.indexOf('appi://')==0){
			if(valueid.indexOf('?')>0){
				valueid=valueid.substring(7,valueid.indexOf('?'));
			}else{
				valueid=valueid.substring(7,valueid.length);
			}
		}
		valueid='app://'+valueid;
		$.cj.App.openApp(valueid);
	});
	win.undelegate('.home-panel > .title > .pos>span[kind]','click');
	win.delegate('.home-panel > .title > .pos>span[kind]','click',function(e){
		var oldvalueid=$('.windows > .titleBar[valueid]').attr('valueid');
		var valueid=oldvalueid;
		if(valueid.indexOf('app://')==0){
			if(valueid.indexOf('?')>0){
				valueid=valueid.substring(6,valueid.indexOf('?'));
			}else{
				valueid=valueid.substring(6,valueid.length);
			}
		}else if(valueid.indexOf('appi://')==0){
			if(valueid.indexOf('?')>0){
				valueid=valueid.substring(7,valueid.indexOf('?'));
			}else{
				valueid=valueid.substring(7,valueid.length);
			}
		}
		valueid='app://'+valueid+'?filterById='+$(this).attr('kind');
		$.cj.App.openApp(valueid);
	});
	win.undelegate('.home-panel > .title > .category>span[kind]','click');
	win.delegate('.home-panel > .title > .category>span[kind]','click',function(e){
		var oldvalueid=$('.windows > .titleBar[valueid]').attr('valueid');
		var valueid=oldvalueid;
		if(valueid.indexOf('app://')==0){
			if(valueid.indexOf('?')>0){
				valueid=valueid.substring(6,valueid.indexOf('?'));
			}else{
				valueid=valueid.substring(6,valueid.length);
			}
		}else if(valueid.indexOf('appi://')==0){
			if(valueid.indexOf('?')>0){
				valueid=valueid.substring(7,valueid.indexOf('?'));
			}else{
				valueid=valueid.substring(7,valueid.length);
			}
		}
		valueid='app://'+valueid+'?filterById='+$(this).attr('kind');
		$.cj.App.openApp(valueid);
	});
	win.undelegate('.home-panel > .art-ul > .article > .body > .content > pre > .extends','click');
	win.delegate('.home-panel > .art-ul > .article > .body > .content > pre > .extends','click',function(e){
		var art=$(this).parents('.article[ioboxid]');
		var id=art.attr('ioboxid');
		$.get('./cyberportApp/develop/getIoboxContent.service',{ioboxId:id},function(data){
			art.find('> .body > .content > pre').html(data);
		}).error(function(e){
			alert(e.responseText);
		});
	});
	win.undelegate('.home-panel > .art-ul > .article > .body > .pics > .items > li');
	win.delegate('.home-panel > .art-ul > .article > .body > .pics > .items > li','click',function(e){
		var the=$(this);
		var show=the.parents('.pics').find('> .show');
		if(typeof the.attr('media')!='undefined'){
			var media=the.attr('media');
			var mime=the.attr('mime');
			var video=" <video id='example_video_1' class='video-js vjs-default-skin' controls preload='none' width='100%' height='100%' " +
					"data-setup='{ 'html5' : { 'nativeTextTracks' : false } }'>" +
					" <source src='"+media+"' type='"+mime+"'>" +
					" <p class='vjs-no-js'>To view this video please enable JavaScript, and consider upgrading to a web browser that <a href='http://videojs.com/html5-video-support/' target='_blank'>supports HTML5 video</a></p>" +
					" </video>";
			show.html(video);
		}else{
			show.find('>video').remove();
			var img=show.find('>img');
			if(img.length<1){
				show.append('<img src='+the.find('>img').attr('src')+'>');
				return;
			}
			img.attr('src',the.find('>img').attr('src'));
		}
	});
	win.undelegate('.home-panel > .art-ul > .article > .body > .pics > .show','click');
	win.delegate('.home-panel > .art-ul > .article > .body > .pics > .show','click',function(e){
		if('max'==$(this).attr('resize')){
			$(this).attr('style','height:200px;width:auto;');
			$(this).attr('resize','min');
			$(this).find('>img').attr('style','height:100%;width:auto;');
		}else{
			$(this).attr('style','height:auto;width:100%;');
			$(this).find('>img').attr('style','height:auto;width:100%');
			$(this).attr('resize','max');
		}
	});
	win.undelegate('.home-panel > .art-ul > .article > .body > .attachment > ul > li[file]');
	win.delegate('.home-panel > .art-ul > .article > .body > .attachment > ul > li[file]','click',function(e){
		var file=$(this).attr('file');
		var iframe=document.createElement('iframe');
		$(iframe).attr('src',file+'&force-download=true');
		$(iframe).attr('style','display:none;');
		$(this).append(iframe);
	});
	
	
	//操作与评论
	win.undelegate('.home-panel > .art-ul > .article > .body > .forum > .bottom > .typed','mouseenter mouseleave keyup focus');
	win.delegate('.home-panel > .art-ul > .article > .body > .forum > .bottom > .typed','mouseenter mouseleave keyup focus',function(e){
		if(e.type=='mouseenter'){
			$(this).children('span[label]').hide();
			$(this).find('>span[value]>textarea').focus();
		}else if(e.type=='mouseleave'){
			if(''==$(this).find('>span[value]>textarea').val()){
				$(this).children('span[label]').show();
			}
		}else if(e.type=='keyup'){
			if(e.keyCode=='13'){
				var the=$(this);
				var val=$(this).find('>span[value]>textarea').val();
				var artEle=$(this).parents('.article');
				var entity=artEle.attr('entity');
				var creator=artEle.attr('creator');
				var sourceid=artEle.attr('sourceid');
				var ioboxid=artEle.attr('ioboxid');
				if(artEle.attr('appId')=='mine'){
					ioboxid=artEle.find('> .body > .source').attr('ioboxid');
				}
				var onsws=artEle.attr('onsws');
				var url='./swssite/develop/relatives/entityComments.service';
				
				if(val==''||val=='\n'){
					alert('空的评论');
					return;
				}
				$.post(url,{entity:entity,ioboxid:ioboxid,sourceid:sourceid,creator:creator,onsws:onsws,action:'comment',to:'@',comment:val,thread:'$'},function(data){
					var obj=$.parseJSON(data);
					//{'commentid':'5741f8ccaf5bc80dbe487031',reviewerId:'user1',reviewerFace:{"nick":"11","head":"1f88b38ad6b8c7b6f7fe9ab7c994550d.jpg","sex":0}}
					the.find('>span[value]>textarea').val('');
					var ul=artEle.find('> .body > .forum > .threads');
					var li="<li class='thread'><img title='"+obj.reviewerId+"' src='./resource/ud/"+obj.reviewerFace.head+"?path=home://system/img/faces/&u="+obj.reviewerId+"' >" +
							"<ul class='forum-panel'>" +
							"<li class='ctx' uid='"+obj.reviewerId+"' title='"+obj.reviewerFace.nick+"'>" +
							"<span name>"+obj.reviewerId+"</span>" +
							"<p talk>"+val+"</p>" +
							"</li>" +
							"<li class='op'>" +
							"<span time>"+obj.ctime+"</span>" +
							"<span reply>回复</span>" +
							"<span style='display:none' commentid='"+obj.commentid+"' close>X</span>" +
							"</li>"+
							"</ul>" +
							"</li>";
					ul.append(li);
				}).error(function(e){
					alert(e.responseText);
				});
				
			}
		}
	});
	win.undelegate('.forum-panel > .op','mouseenter mouseleave');
	win.delegate('.forum-panel > .op','mouseenter mouseleave',function(e){
		if(e.type=='mouseenter'){
			$(this).find('span[close]').show();
		}else{
			$(this).find('span[close]').hide();
		}
	});
	win.undelegate('.forum-panel > .op > span[close]','click');
	win.delegate('.forum-panel > .op > span[close]','click',function(e){
		var the=$(this);
		var artEle=$(this).parents('.article');
		var entity=artEle.attr('entity');
		var creator=artEle.attr('creator');
		var sourceid=artEle.attr('sourceid');
		var commentid=$(this).attr('commentid');
		var creator=artEle.attr('creator');
		var onsws=artEle.attr('onsws');
		var ioboxid=artEle.attr('ioboxid');
		if(artEle.attr('appId')=='mine'){
			ioboxid=artEle.find('> .body > .source').attr('ioboxid');
		}
		var url='./swssite/develop/relatives/entityComments.service';
		$.post(url,{onsws:onsws,creator:creator,sourceid:sourceid,ioboxid:ioboxid,commentid:commentid,action:'delComment'},function(data){
			the.parent('.op').parent('.forum-panel').parent('li').remove();
		}).error(function(e){
			alert(e.responseText);
		});
	});
	win.undelegate('.forum-panel > .op > span[reply]','click');
	win.delegate('.forum-panel > .op > span[reply]','click',function(e){
		var input=$(this).parent('.op').find('input');
		if(input.length>0){
			input.toggle();
			input.focus();
			return;
		}
		$(this).parent('.op').append("<input style='width:100%;padding:4px;'>");
	});
	win.undelegate('.forum-panel > .op > input','keyup');
	win.delegate('.forum-panel > .op > input','keyup',function(e){
		if(e.keyCode=='13'){
			var input=$(this).parent('.op').find('input');
			var thread=$(this).parents('.thread').find('>.forum-panel>.op>span[commentid]').attr('commentid');
			var the=$(this);
			var val=input.val();
			var artEle=$(this).parents('.article');
			var entity=artEle.attr('entity');
			var creator=artEle.attr('creator');
			var sourceid=artEle.attr('sourceid');
			var onsws=artEle.attr('onsws');
			var ioboxid=artEle.attr('ioboxid');
			if(artEle.attr('appId')=='mine'){
				ioboxid=artEle.find('> .body > .source').attr('ioboxid');
			}
			var to='@'+the.parent('.op').siblings('.ctx').attr('uid');
			var url='./swssite/develop/relatives/entityComments.service';
			
			if(val==''||val=='\n'){
				alert('空的评论');
				return;
			}
			$.post(url,{onsws:onsws,creator:creator,ioboxid:ioboxid,entity:entity,sourceid:sourceid,action:'comment',to:to,comment:val,thread:thread},function(data){
				input.val('');
				input.hide();
				var obj=$.parseJSON(data);
				//{'commentid':'5741f8ccaf5bc80dbe487031',reviewerId:'user1',reviewerFace:{"nick":"11","head":"1f88b38ad6b8c7b6f7fe9ab7c994550d.jpg","sex":0}}
				var panel=the.parents('li.thread').find('>.forum-panel');
				if(panel.children('.follows').length<1){
					panel.append("<li class='follows'><ul></ul></li>");
				}
				var li="<li><img title='"+obj.reviewerId+"' src='./resource/ud/"+obj.reviewerFace.head+"?path=home://system/img/faces/&u="+obj.reviewerId+"' >" +
						"<ul class='forum-panel'>" +
						"<li class='ctx' uid='"+obj.reviewerId+"'>" +
						"<span name>"+obj.reviewerId+"</span>" +
						"<span symbol>:</span>" +
						"<span to>"+obj.to+"</span>" +
						"<p talk>"+val+"</p>" +
						"</li>" +
						"<li class='op'>" +
						"<span time>"+obj.ctime+"</span>" +
						"<span reply>回复</span>" +
						"<span style='display:none' commentid='"+obj.commentid+"' close>X</span>" +
						"</li>"+
						"</ul>" +
						"</li>";
				$(panel).find(">.follows>ul").append(li);
			}).error(function(e){
				alert(e.responseText);
			});
		}
	});
	win.undelegate('.home-panel > .art-ul> .article > .body >.op > ul > li[action]','click');
	win.delegate('.home-panel > .art-ul> .article > .body >.op > ul > li[action]','click',function(e){
		var action=$(this).attr('action');
		var artEle=$(this).parents('.article');
		var entity=artEle.attr('entity');
		var sourceid=artEle.attr('sourceid');
		var creator=artEle.attr('creator');
		var onsws=artEle.attr('onsws');
		var ioboxid=artEle.attr('ioboxid');
		if(artEle.attr('appId')=='mine'){
			ioboxid=artEle.find('> .body > .source').attr('ioboxid');
		}
		var url='./swssite/develop/relatives/entityComments.service';
		
		switch(action){
		case 'forum':
			$(this).parents('.article').find('> .body > .forum > .bottom > .typed').toggle();
			break;
		case 'share':
			$.post(url,{ioboxid:ioboxid,creator:creator,onsws:onsws,action:'share',sourceid:sourceid},function(data){
				
			}).error(function(e){
				alert(e.responseText);
			});
			break;
		case 'follow':
			$.post(url,{action:'follow',ioboxid:ioboxid,creator:creator,onsws:onsws,entity:entity,sourceid:sourceid},function(data){
				var obj=$.parseJSON(data);
				var ctrl=artEle.find('> .body > .op > ul > li[action=follow]');
				if(obj.cancel=='true'){//取消关注
					ctrl.attr('title','我未有关注');
					var ctext=ctrl.find(' > span[value]').html();
					ctext=ctext.substring(1,ctext.length-1);
					var c=parseInt(ctext);
					ctrl.find(' > span[value]').html('('+(c-1)+')');
				}else{
					ctrl.attr('title','我已关注');
					var ctext=ctrl.find(' > span[value]').html();
					ctext=ctext.substring(1,ctext.length-1);
					var c=parseInt(ctext);
					ctrl.find(' > span[value]').html('('+(c+1)+')');
				}
			}).error(function(e){
				alert(e.responseText);
			});
			break;
		case 'great':
			$.post(url,{action:'great',ioboxid:ioboxid,creator:creator,onsws:onsws,sourceid:sourceid,entity:entity},function(data){
				var obj=$.parseJSON(data);
				var great=artEle.find('> .body > .forum > .great');
				var faceul=great.children('.faces');
				if(obj.cancel=='true'){//撤消
					great.children('img.g-main').css('border','none');
					faceul.find('>li[face][uid='+obj.reviewerId+']').remove();
					var ctext=artEle.find('> .body>.op > ul > li[action=great] > span[value]').html();
					ctext=ctext.substring(1,ctext.length-1);
					var c=parseInt(ctext);
					artEle.find('> .body>.op > ul > li[action=great] > span[value]').html('('+(c-1)+')');
				}else{
					great.children('img.g-main').css('border','1px solid #ececec');
					var src=obj.reviewerFace.head;
					var face="<li uid='"+obj.reviewerId+"' face><img src='./resource/ud/"+src+"?path=home://system/img/faces/&u="+obj.reviewerId+"'></li>";
					faceul.append(face);
					var ctext=artEle.find('> .body>.op > ul > li[action=great] > span[value]').html();
					ctext=ctext.substring(1,ctext.length-1);
					var c=parseInt(ctext);
					artEle.find('> .body>.op > ul > li[action=great] > span[value]').html('('+(c+1)+')');
				}
			}).error(function(e){
				alert(e.responseText);
			});
			break;
		}
	});
	win.undelegate('span[face],li[face]','mouseover mouseout');
	win.delegate('span[face],li[face]','mouseover mouseout',function(e){
		//弹出用户的face
		if(e.type=='mouseover'){
			
		}else if(e.type=='mouseout'){
			
		}
	});
	win.undelegate('.home-panel > .art-ul > .article > .body > .forum > .bottom > .extends','click');
	win.delegate('.home-panel > .art-ul > .article > .body > .forum > .bottom > .extends','click',function(e){
		alert('----more');
	});
	win.undelegate('> .desc > span[visits]','click');
	win.delegate('> .desc > span[visits]','click',function(e){
		alert('----visits');
	});
	win.undelegate('.home-panel > .art-ul > .article > .body > .forum > .great > img.g-main','click');
	win.delegate('.home-panel > .art-ul > .article > .body > .forum > .great > img.g-main','click',function(e){
		var action=$(this).attr('action');
		var artEle=$(this).parents('.article');
		var entity=artEle.attr('entity');
		var sourceid=artEle.attr('sourceid');
		var creator=artEle.attr('creator');
		var onsws=artEle.attr('onsws');
		var ioboxid=artEle.attr('ioboxid');
		if(artEle.attr('appId')=='mine'){
			ioboxid=artEle.find('> .body > .source').attr('ioboxid');
		}
		var url='./swssite/develop/relatives/entityComments.service';
		$.post(url,{action:'great',ioboxid:ioboxid,onsws:onsws,creator:creator,entity:entity,sourceid:sourceid},function(data){
			var obj=$.parseJSON(data);
			var great=artEle.find('> .body > .forum > .great');
			var faceul=great.children('.faces');
			if(obj.cancel=='true'){//撤消
				great.children('img.g-main').css('border','none');
				faceul.find('>li[face][uid='+obj.reviewerId+']').remove();
				var ctext=artEle.find('> .body>.op > ul > li[action=great] > span[value]').html();
				ctext=ctext.substring(1,ctext.length-1);
				var c=parseInt(ctext);
				artEle.find('> .body>.op > ul > li[action=great] > span[value]').html('('+(c-1)+')');
			}else{
				great.children('img.g-main').css('border','1px solid #ececec');
				var src=obj.reviewerFace.head;
				var face="<li uid='"+obj.reviewerId+"' face><img src='./resource/ud/"+src+"?path=home://system/img/faces/&u="+obj.reviewerId+"'></li>";
				faceul.append(face);
				var ctext=artEle.find('> .body>.op > ul > li[action=great] > span[value]').html();
				ctext=ctext.substring(1,ctext.length-1);
				var c=parseInt(ctext);
				artEle.find('> .body>.op > ul > li[action=great] > span[value]').html('('+(c+1)+')');
			}
		}).error(function(e){
			alert(e.responseText);
		});
	});
	win.undelegate('.home-panel>.art-ul>.article[more]','click');
	win.delegate('.home-panel>.art-ul>.article[more]','click',function(e){
		var skip=$(this).attr('more');
		var filterbyid=$(this).attr('filterbyid');
		var iobox=$(this).attr('iobox');
		var the=$(this);
		var ul=$(this).parents('.home-panel>.art-ul');
		$.get("./cyberportApp/index.html?iobox="+iobox+"&skip="+skip+"&filterById="+filterbyid,{},function(data){
			the.remove();
			ul.append(data);
		}).error(function(e){
			alert(e.responseText);
		});
	});
	
	win.find('.home-panel > .shuoshuo > .textarea > span[tips]').on('click',function(e){
		$(this).hide();
		$(this).siblings('textarea').focus();
	});
	win.find('.home-panel > .shuoshuo > .textarea > textarea').focus(function(e){
		$(this).siblings('span[tips]').hide();
		$(this).parent('.textarea').siblings('.toolbar').show();
		$(this).css('height','300px');
		
	});
	win.find('.home-panel > .shuoshuo > .textarea > textarea').blur(function(e){
		if($(this).val()==''){
			$(this).siblings('span[tips]').show();
		}
		//$(this).css('height','80px');
		//$(this).parent('.textarea').siblings('.toolbar').hide();
	});
	win.find('.home-panel > .shuoshuo > .toolbar > .buttons > li').on('click',function(e){
		if($(this).is('[pub]')){
			var pictureEs = $('.home-panel > .shuoshuo > .medias > .items > li[pic]>img[fn]');
			var pictures = [];
			for (var i = 0; i < pictureEs.length; i++) {
				var pE = pictureEs[i];
				pictures.push($(pE).attr('fn'));
			}

			var mediaEs = $('.home-panel > .shuoshuo > .medias > .items > li[media]>img[fn]');
			var medias = [];
			for (var i = 0; i < mediaEs.length; i++) {
				var pE = mediaEs[i];
				medias.push($(pE).attr('fn'));
			}
			$.post('./swssite/develop/pubShuoShuo.service',{
				content:$('.home-panel > .shuoshuo > .textarea > textarea').val(),
				pictures:pictures,
				medias:medias
			},function(data){
				var valueid=$('.windows > .titleBar[valueid]').attr('valueid');
				if(valueid.indexOf('app://')!=0&&valueid.indexOf('appi://')!=0){
					valueid='app://'+valueid;
				}
				$.cj.App.openApp(valueid);
			}).error(function(e){
				alert(e.responseText);
			});
			return;
		}
		if($(this).is('[cancel]')){
			$('.home-panel > .shuoshuo > .toolbar').hide();
			$('.home-panel > .shuoshuo > .medias').hide();
			var items=$('.home-panel > .shuoshuo > .medias > .items');
			var files=items.find('>li[path]');
			for(var i=0;i<files.length;i++){
				var path=$(files[i]).attr('path');
				$.post('./swssite/develop/editFileAndDir.service',{
					fstype:'file',
					path:path
				},function(data){
					items.empty();
					$('.home-panel > .shuoshuo > .medias > .show').empty();
				}).error(function(e){
					alert(e.responseText);
				});
			}
			win.find('.home-panel > .shuoshuo > .textarea > textarea').css('height','80px');
			return;
		}
	});
	win.find('.home-panel > .shuoshuo > .textarea > .op>div').on('click',function(e){
		var medias=$('.home-panel > .shuoshuo > .medias');
		medias.show();
		var shower=medias.find('.show');
		if(shower.find('span[del]').length<1){
			shower.append("<span del>X</span>");
		}
		
		if($(this).is('.uploadPic')){
			medias.find('#myfile').remove();
			var fileId='myfile';
			var input="<input id='"
				+ fileId
				+ "' name='"
				+ fileId
				+ "' type='file' style='display:none;' accept='.jpg,.gif,.svg,.png' data-max_size='2000000'>";
			medias.append(input);
			var myfile=medias.find('#myfile');
			myfile.trigger('click');
			myfile.on('change', function(file) {
				if(shower.find('>img').length<1){
					shower.append("<img src='img/waiting.gif'>")
				}else{
					shower.find('>img').attr('src','img/waiting.gif');
				}
				var url = './swssite/develop/uploadPictures.service';
				$.ajaxFileUpload({
					url : url, // 用于文件上传的服务器端请求地址
					secureuri : false, // 一般设置为false
					fileElementId : fileId,
					dataType : 'json',// 返回值类型 一般设置为json
					data : {},
					success : function(data, status) {
						myfile.remove();
	
						var src = data.src.replace('&amp;', '&');
						var fn = data.fn;
						
						var ul = medias.find('> .items').first();
						var li = "<li pic='"+src+"' path='/pictures/"+fn+"'><img src='"+src+"' fn='"+fn+"'></li>";
						ul.append(li);
						
						shower.attr('path','/pictures/'+fn);
						shower.find('>img').remove();
						shower.find('>video').remove();
						if(shower.find('>img').length<1){
							shower.append("<img src='"+src+"'>");
						}else{
							shower.find('>img').attr('src', src);
						}
	
					},
					error : function(data, status, e) {
						myfile.remove();
						alert(e);
					}
				});
				
			});
		}else if($(this).is('.uploadMedia')){
			medias.find('#myfile').remove();
			var fileId='myfile';
			var input="<input id='"
				+ fileId
				+ "' name='"
				+ fileId
				+ "' type='file' style='display:none;' accept=',.mov,.webm,.mp3,.mp4,.avi,.mkv,.rmvb' data-max_size='8000000'>";
			medias.append(input);
			var myfile=medias.find('#myfile');
			myfile.trigger('click');
			myfile.on('change', function(file) {
				if(shower.find('>img').length<1){
					shower.append("<img src='img/waiting.gif'>")
				}else{
					shower.find('>img').attr('src','img/waiting.gif');
				}
				var url = './swssite/develop/uploadMedias.service';
				$.ajaxFileUpload({
					url : url, // 用于文件上传的服务器端请求地址
					secureuri : false, // 一般设置为false
					fileElementId : fileId,
					dataType : 'json',// 返回值类型 一般设置为json
					data : {},
					success : function(data, status) {
						myfile.remove();
	
						var src = data.src.replace('&amp;', '&');
						var fn = data.fn;
						
						$('#my-video').remove();
						var videohtml="<video class='video-js  vjs-default-skin' controls preload='auto' poster='#' data-setup='{}'>";
						var source="";
						var ext='';
						var mime='audio/mp4';
						ext=fn.substring(fn.lastIndexOf('.'),fn.length);
						switch(ext){
						case '.mov':
							source="<source type='video/quicktime' src='"+src+"'></source>";
							mime='video/quicktime';
							break;
						case '.mp3':
							source="<source type='audio/mpeg' src='"+src+"'></source>";
							mime='audio/mpeg';
							break;
						case '.rmvb':
							source="<source type='audio/quicktime' src='"+src+"'></source>";
							mime='audio/quicktime';
							break;
						case '.mkv':
							source="<source type='audio/quicktime' src='"+src+"'></source>";
							mime='audio/quicktime';
							break;
						case '.mp4':
							source="<source type='audio/mp4' src='"+src+"'></source>";
							mime='audio/mp4';
							break;
						case '.avi':
							source="<source type='video/x-msvideo' src='"+src+"'></source>";
							mime='video/x-msvideo';
							break;
						case '.webm':
							source="<source type='video/webm' src='"+src+"'></source>";
							mime='video/webm';
							break;
						default:
							break;
						}
						shower.find('>img').remove();
						shower.find('>video').remove();
						var endvideo="</video>"	;
						shower.append(videohtml+source+endvideo);
						shower.attr('path','/medias/'+fn);
						
						var ul = medias.find('> .items').first();
						var videoimg="<img fn='"+fn+"' src='./swssite/develop/img/media-blue.svg'>";
						var lihtml = "<li media='"+src+"' mime='"+mime+"' path='/medias/"+fn+"'>"+videoimg+"</li>";
						ul.append(lihtml);
					},
					error : function(data, status, e) {
						myfile.remove();
						alert(e);
					}
				});
				
			});
		}
	});
	win.undelegate('.home-panel > .shuoshuo > .medias > .items > li');
	win.delegate('.home-panel > .shuoshuo > .medias > .items > li','click',function(e){
		var the=$(this);
		var show=the.parents('.medias').find('> .show');
		if(typeof the.attr('media')!='undefined'){
			var media=the.attr('media');
			var mime=the.attr('mime');
			var video=" <video id='example_video_1' class='video-js vjs-default-skin' controls preload='none' width='100%' height='100%' " +
					"data-setup='{ 'html5' : { 'nativeTextTracks' : false } }'>" +
					" <source src='"+media+"' type='"+mime+"'>" +
					" <p class='vjs-no-js'>To view this video please enable JavaScript, and consider upgrading to a web browser that <a href='http://videojs.com/html5-video-support/' target='_blank'>supports HTML5 video</a></p>" +
					" </video>";
			show.html(video);
			show.append("<span del>X</span>");
			show.attr('path',the.find('>img').attr('fn'));
		}else{
			show.find('>video').remove();
			var img=show.find('>img');
			if(img.length<1){
				show.append('<img src='+the.find('>img').attr('src')+'>');
				return;
			}
			img.attr('src',the.find('>img').attr('src'));
			show.attr('path',the.attr('path'));
		}
	});
	win.undelegate('.home-panel > .shuoshuo > .medias > .show','click');
	win.delegate('.home-panel > .shuoshuo > .medias > .show','click',function(e){
		if('max'==$(this).attr('resize')){
			$(this).attr('style','height:200px;width:auto;');
			$(this).attr('resize','min');
			$(this).find('>img').attr('style','height:100%;width:auto;');
		}else{
			$(this).attr('style','height:auto;width:100%;');
			$(this).find('>img').attr('style','height:auto;width:100%');
			$(this).attr('resize','max');
		}
	});
	win.undelegate('.home-panel > .shuoshuo > .medias > .show','mouseenter mouseleave');
	win.delegate('.home-panel > .shuoshuo > .medias > .show','mouseenter mouseleave',function(e){
		var del=$(this).find('>span[del]');
		if(e.type=='mouseenter'){
			del.show();
			return;
		}
		if(e.type=='mouseleave'){
			del.hide();
			return;
		}
	});
	win.undelegate('.home-panel > .shuoshuo > .medias > .show > span[del]','click');
	win.delegate('.home-panel > .shuoshuo > .medias > .show > span[del]','click',function(e){
		var shower=$(this).parent('.show');
		var items=$(this).parents('.medias').find('>.items');
		var path=$(this).parent('.show').attr('path');
		$.post('./swssite/develop/editFileAndDir.service',{
			fstype:'file',
			path:path
		},function(data){
			items.find(">li[path='"+path+"']").remove();
			items=items.find(">li");
			if(items.length>0){
				items.first().trigger('click');
			}else{
				shower.empty();
			}
		}).error(function(e){
			alert(e.responseText);
		});
		return false;
	});
	
	win.undelegate('.home-panel > .art-ul > .article > .head > .left>ul>li[name]>span[label],.home-panel > .art-ul > .article > .head > .left>img','mouseenter');
	win.delegate('.home-panel > .art-ul > .article > .head > .left>ul>li[name]>span[label],.home-panel > .art-ul > .article > .head > .left>img','mouseenter',function(e){
		if(e.type=='mouseenter'){
			var box=$(this).parents('.left');
			box.attr('style','position: relative;');
			box.find('>.user-face').remove();
			box.append("<div class='user-face'></div>");
			var user=box.find('>ul>li[name]').attr('user');
			$.get('./servicews/viewUserProfile.service',{user:user},function(data){
				box.find('>.user-face').html(data);
			}).error(function(e){
				alert(e.responseText);
			});
			return;
		}
	});
	win.undelegate('.home-panel > .art-ul > .article > .head > .left','mouseleave');
	win.delegate('.home-panel > .art-ul > .article > .head > .left','mouseleave',function(e){
		if(e.type=='mouseleave'){
			var box=$(this);
			box.find('>.user-face').remove();
			return;
		}
	});
	win.undelegate('.home-panel > .art-ul > .article > .head > .right > ul > .browser>img,.home-panel > .art-ul > .article > .head > .right > ul > .browser>span','click');
	win.delegate('.home-panel > .art-ul > .article > .head > .right > ul > .browser>img,.home-panel > .art-ul > .article > .head > .right > ul > .browser>span','click',function(e){
		var the=$(this).parent('.browser');
		the.attr('style','position: relative;');
		the.find('.e-r-v').remove();
		the.find('style[name=e-r-v]').remove();
		var art=$(this).parents('.article');
		var sourceid=art.attr('sourceid');
		var entity=art.attr('entity');
		var user=art.attr('creator');
		var swsid=art.attr('onsws');
		var kind='see';
		$.get('./servicews/viewEntityRelatives.service',{
			user:user,
			swsid:swsid,
			kind:kind,
			sourceid:sourceid,
			entity:entity
			},
		function(data){
			the.append(data);
		}).error(function(e){
			alert(e.responseText);
		});
	});
	win.undelegate('.home-panel > .art-ul > .article > .head > .right > ul > .browser','mouseleave');
	win.delegate('.home-panel > .art-ul > .article > .head > .right > ul > .browser','mouseleave',function(e){
		if(e.type=='mouseleave'){
			var box=$(this);
			box.find('>.e-r-v').remove();
			return;
		}
	});
	win.undelegate('.e-r-v>.more>span[label]','click');
	win.delegate('.e-r-v>.more>span[label]','click',function(e){
		var more=$(this).parent('.more');
		var events=$(this).parents('.more').siblings('.events');
		var art=$(this).parents('.article');
		var sourceid=art.attr('sourceid');
		var entity=art.attr('entity');
		var user=art.attr('creator');
		var swsid=art.attr('onsws');
		var skip=more.attr('skip');
		var kind='see';
		$.get('./servicews/viewEntityRelatives.service',{
			user:user,
			swsid:swsid,
			kind:kind,
			sourceid:sourceid,
			entity:entity,
			skip:skip
			},
		function(data){
				events.append(data);
		}).error(function(e){
			alert(e.responseText);
		});
	});
	win.undelegate('.e-r-v>.events>li[user]','click');
	win.delegate('.e-r-v>.events>li[user]','click',function(e){
		var user=$(this).attr('user');
		var box=$(this);
		box.parent('.events').find('.user-face').remove();
		box.append("<div class='user-face' style='right:0;left:auto;'></div>");
		$.get('./servicews/viewUserProfile.service',{user:user},function(data){
			box.find('>.user-face').html(data);
		}).error(function(e){
			alert(e.responseText);
		});
	});
	win.undelegate('.e-r-v>.events>li[user]','mouseleave');
	win.delegate('.e-r-v>.events>li[user]','mouseleave',function(e){
		$(this).parents('.events').find('.user-face').remove();
	});
})();