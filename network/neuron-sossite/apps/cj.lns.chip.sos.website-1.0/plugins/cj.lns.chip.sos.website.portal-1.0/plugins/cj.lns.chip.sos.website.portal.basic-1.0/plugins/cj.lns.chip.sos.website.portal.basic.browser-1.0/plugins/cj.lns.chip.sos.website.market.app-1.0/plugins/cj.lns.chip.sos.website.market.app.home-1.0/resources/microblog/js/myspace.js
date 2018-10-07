(function(){
	$('.year > li > .month > li , .year > li > .month > li > .day > li, .year > li').hover(function(e){
		$(this).children('ul').show();
	},function(e){
		$(this).children('ul').hide();
	});
	
	var folder=$('.cube > .show > .finder > .folder');
	folder.undelegate('>.items > .file','mouseover mouseout');
	folder.delegate('>.items > .file','mouseover mouseout',function(e){
		if(e.type=='mouseover'){
			$(this).find('>span.op').show();
		}else if(e.type=='mouseout'){
			$(this).find('>span.op').hide();
		}
	});
	folder.undelegate('>.items > .file > .op > span','click');
	folder.delegate('>.items > .file > .op > span','click',function(e){
		var f=$(this).parents('li[fstype]');
		var fstype=f.attr('fstype');
		var path=f.attr('path');
		var cube=$('#cube').val();
		if(typeof $(this).attr('rename')!='undefined'){
			$.post('./swssite/microblog/renameFileAndDir.service?cube='+cube,{path:path,fstype:fstype},function(data){
				
			}).error(function(e){
				alert(e.responseText);
			});
		}else if(typeof $(this).attr('remove')!='undefined'){
			if('dir'==fstype){
				 if(!confirm("将删除其内的所有文件和子目录\r\n注意：系统目录、我的产品、我的图片等非你自己创建的目录被应用所用，\r\n如果删除将可能导致应用的图片或文件丢失，且不可恢复\r\n是否要删除？")){
				      return false;
				 }
			}
			$.post('./swssite/microblog/editFileAndDir.service?cube='+cube,{path:path,fstype:fstype},function(data){
				f.remove();
			}).error(function(e){
				alert(e.responseText);
			});
		}
	});
	
	function switchDir(data){
		var folder= $('.cube > .show > .finder > .folder');
		folder.empty();
		folder.html(data);
		var dir=folder.children('.toolbar').attr('currentdir')
		var cfolder=folder.children('.toolbar').attr('currentfolder')
		$('.cube > .dims > ul > li.path > input').val(dir);
		$('.cube > .show > .title > span[label]').html(cfolder);
		var site="{\"dir\":\""+dir+"\"}";
		$('.cube > .show > .title>span[value]').html(site);
	}
	var pathInput= $('.cube > .dims > ul > li.path > input');
	pathInput.on('keyup',function(e){
		if(e.keyCode!='13'){
			return;
		}
		var path=$(this).val();
		if(path==''){
			path='/';
		}
		if(path.lastIndexOf('\n')>0){
			path=path.substring(0,path.lastIndexOf('\n'));
		}
		var cube=$('#cube').val();
		$.post('./swssite/microblog/openDir.service?cube='+cube,{path:path},function(data){
			switchDir(data);
		}).error(function(e){
			alert(e.responseText);
		});
	});
	
	
	folder.undelegate(' >.items> .file >img,>.items> .file >ul>li[t]>span,>.items> .parentdir >img,>.items> .parentdir >ul>li[t]>span','click');
	folder.delegate(' >.items> .file >img,>.items> .file >ul>li[t]>span,>.items> .parentdir >img,>.items> .parentdir >ul>li[t]>span','click',function(e){
		var li=$(this).parents('li[fstype=dir]');
		if(li.length>0){//只用于打开目录
			var path=li.attr('path');
			var cube=$('#cube').val();
			$.post('./swssite/microblog/openDir.service?cube='+cube,{path:path},function(data){
				switchDir(data);
			}).error(function(e){
				alert(e.responseText);
			});
		}else{//打开文件
			var path=$(this).parents('li[fstype=file]').attr('path');
			var cube=$('#cube').val();
			$.post('./swssite/microblog/openFileInfo.service?cube='+cube,{path:path},function(data){
				var detail=$('.cube > .show > .finder > .box > .detail');
				detail.html(data);
				
				detail.attr('style','display:block;');
				var folder=$('.cube > .show > .finder > .folder').first();
				folder.attr('style','width:510px;margin-left:-100%');
			}).error(function(e){
				alert(e.responseText);
			});
		}
	});
	
	function switchSite(data,siteobj){
		var folder= $('.cube > .show > .finder > .folder');
		folder.empty();
		folder.html(data);
		var dir=folder.children('.toolbar').attr('currentdir')
		var cfolder=folder.children('.toolbar').attr('currentfolder')
		$('.cube > .dims > ul > li.path > input').val(dir);
		$('.cube > .show > .title > span[label]').html(cfolder);
		var site=JSON.stringify(siteobj);
		$('.cube > .show > .title>span[value]').html(site);
	}
	var fstypes=$('.cube > .dims > ul > li.type > .right > span[value]');
	fstypes.on('click',function(e){
		var path=$(this).attr('path');
		var site=$('.cube > .show > .title > span[value]').html();
		var obj=$.parseJSON(site);
		obj.fileType=path;
		var cube=$('#cube').val();
		$.post('./swssite/microblog/openSite.service?cube='+cube,{site:obj},function(data){
			//switchSite
			switchSite(data,obj);
		}).error(function(e){
			alert(e.responseText);
		});
	});
	var cdates=$('.year > li > .month > li > span, .year > li > .month > li > .day > li > span, .year > li > span');
	cdates.on('click',function(e){
		var path=$(this).parent('li[path]').attr('path');
		var site=$('.cube > .show > .title > span[value]').html();
		var obj=$.parseJSON(site);
		obj.createDate=path;
		var cube=$('#cube').val();
		$.post('./swssite/microblog/openSite.service?cube='+cube,{site:obj},function(data){
			//switchSite
			switchSite(data,obj);
		}).error(function(e){
			alert(e.responseText);
		});
	});
	var  cdate= $('.cube > .dims > ul > li.cdate > .left > span[label]');
	cdate.on('click',function(e){
		var site=$('.cube > .show > .title > span[value]').html();
		var obj=$.parseJSON(site);
		delete obj.createDate;
		var cube=$('#cube').val();
		$.post('./swssite/microblog/openSite.service?cube='+cube,{site:obj},function(data){
			//switchSite
			switchSite(data,obj);
		}).error(function(e){
			alert(e.responseText);
		});
	});
	var  fstype= $('.cube > .dims > ul > li.type > .left > span[label]');
	fstype.on('click',function(e){
		var site=$('.cube > .show > .title > span[value]').html();
		var obj=$.parseJSON(site);
		delete obj.fileType;
		var cube=$('#cube').val();
		$.post('./swssite/microblog/openSite.service?cube='+cube,{site:obj},function(data){
			//switchSite
			switchSite(data,obj);
		}).error(function(e){
			alert(e.responseText);
		});
	});
	var box =$('.cube > .show > .finder > .box');
	box.undelegate('> .detail > .preview > .op > li','click');
	box.delegate('> .detail > .preview > .op > li','click',function(e){
		if(typeof $(this).attr('download')!='undefined'){
			var prev=$(this).parents('.preview');
			var path=prev.find('.media').attr('file');
			var the=$(this);
			var cube=$('#cube').val();
			$.post('./swssite/microblog/loggerForDownload.service?cube='+cube,{path:path},function(data){
				var path=prev.find('>.area>.media').attr('path');
				var iframe="<iframe style='display:none' src='"+path+"&force-download=true'><iframe>";
				the.append(iframe);
			}).error(function(e){
				alert(e.responseText);
			});
		}
	});
	
	var finder=$('.cube > .show > .finder ');
	var op=$('.cube > .show > .finder > .folder > .toolbar > div[op]');
	finder.undelegate('> .folder > .toolbar > div[op]>span[tool=upload]','click');
	finder.delegate('> .folder > .toolbar > div[op]>span[tool=upload]','click',function(e){
		var curdir=finder.find(' > .folder > .toolbar').attr('currentdir');
		$(this).find('#uploadfile').remove();
		var upload="<input style='display:none' id='uploadfile' name='uploadfile' type='file'>";
		$(this).append(upload);
		$(this).find('#uploadfile').on('click',function(e){
			e.stopPropagation();
		});
		$(this).find('#uploadfile').trigger('click');
		$(this).find('#uploadfile').change(function(e){
			var cube=$('#cube').val();
			$.ajaxFileUpload({
			url : './swssite/microblog/uploadFileByLocation.service?cube='+cube, // 用于文件上传的服务器端请求地址
			secureuri : false, // 一般设置为false
			fileElementId : 'uploadfile',
			dataType : 'json',// 返回值类型 一般设置为json
			data : {location:curdir},
			success : function(data, status) {
				$.post('./swssite/microblog/openDir.service?cube='+cube,{path:curdir},function(data){
					switchDir(data);
				}).error(function(e){
					alert(e.responseText);
				});
			},
			error : function(e) {
				alert(e.responseText);
			}
			});
		});
	});
	finder.undelegate('> .folder > .toolbar > div[op]>span[tool=newfolder]','click');
	finder.delegate('> .folder > .toolbar > div[op]>span[tool=newfolder]','click',function(e){
		var finder=$(this).parents('.finder');
		finder.find('.popup').remove();
		var popup="<div class='popup'><input dirname title='输入目录名,推荐英文'><input foldername title='输入文件夹名，推荐中文'><div class='op'><span save>保存</span><span close>X</span></div></div>";
		finder.append(popup);
		finder.find('.popup>.op>span[close]').on('click',function(e){
			$(this).parents('.popup').remove();
		});
		finder.find('.popup>.op>span[save]').on('click',function(e){
			var curdir=finder.find(' > .folder > .toolbar').attr('currentdir');
			var folderName=$(this).parents('.popup').find('input[foldername]').val();
			var dirName=$(this).parents('.popup').find('input[dirname]').val();
			if(dirName==''){
				alert('目录名为空');
				return;
			}
			if(folderName==''){
				folderName=dirName;
			}
			var cube=$('#cube').val();
			$.post('./swssite/microblog/createFileAndDir.service?cube='+cube,{op:'createFolder',location:curdir,folderName:folderName,dirName:dirName},function(data){
				finder.find('.popup').remove();
				var cube=$('#cube').val();
				$.post('./swssite/microblog/openDir.service?cube='+cube,{path:curdir},function(data){
					switchDir(data);
				}).error(function(e){
					alert(e.responseText);
				});
			}).error(function(e){
				alert(e.responseText);
			});
		});
	});
	finder.undelegate('> .folder > .toolbar > div[op]>span[tool=newfile]','click');
	finder.delegate('> .folder > .toolbar > div[op]>span[tool=newfile]','click',function(e){
		var finder=$(this).parents('.finder');
		finder.find('.popup').remove();
		var popup="<div class='popup'><input title='输入文件名'><textarea title='输入文件内容'></textarea><div class='op'><span save>保存</span><span close>X</span></div></div>";
		finder.append(popup);
		finder.find('.popup>.op>span[close]').on('click',function(e){
			$(this).parents('.popup').remove();
		});
		finder.find('.popup>.op>span[save]').on('click',function(e){
			var curdir=finder.find(' > .folder > .toolbar').attr('currentdir');
			var fileName=$(this).parents('.popup').find('input').val();
			var content=$(this).parents('.popup').find('textarea').val();
			if(fileName==''){
				alert('文件名为空');
				return;
			}
			if(content==''){
				alert('文件内容为空');
				return;
			}
			var cube=$('#cube').val();
			$.post('./swssite/microblog/createFileAndDir.service?cube='+cube,{op:'createFile',location:curdir,fileName:fileName,content:content},function(data){
				finder.find('.popup').remove();
				$.post('./swssite/microblog/openDir.service?cube='+cube,{path:curdir},function(data){
					switchDir(data);
				}).error(function(e){
					alert(e.responseText);
				});
			}).error(function(e){
				alert(e.responseText);
			});
		});
	});
	
	//来访
	var plist=$('.cube > .show > .finder');
	plist.undelegate('> .box > .detail > .preview > .count > li[visitors] > span','mouseenter');
	plist.delegate('> .box > .detail > .preview > .count > li[visitors] > span','mouseenter',function(e){
		var the=$(this).parent('li[visitors]');
		the.attr('style','position: relative;');
		the.find('.e-r-v').remove();
		the.find('style[name=e-r-v]').remove();
		var art=$(this).parents('.preview').find('> .area>div[file]');
		var sourceid=art.attr('file');
		var entity=art.attr('entity');
//		var user=art.attr('creator');
//		var swsid=art.attr('onsws');
		var kind='see';
		$.get('./servicews/viewEntityRelatives.service',{
//			user:user,
//			swsid:swsid,
			kind:kind,
			sourceid:sourceid,
			entity:entity
			},
		function(data){
			the.append(data);
			the.find('div.e-r-v').attr('style','right:0;left:auto;top:15px;');
		}).error(function(e){
			alert(e.responseText);
		});
	});
	plist.undelegate('> .box > .detail > .preview > .count > li[visitors]','mouseleave');
	plist.delegate('> .box > .detail > .preview > .count > li[visitors]','mouseleave',function(e){
			var box=$(this);
			box.find('>.e-r-v').remove();
	});
	plist.undelegate('.e-r-v>.more>span[label]','click');
	plist.delegate('.e-r-v>.more>span[label]','click',function(e){
		var more=$(this).parent('.more');
		var events=more.siblings('.events');
		var art=$(this).parents('.preview').find('> .area>div[file]');
		var sourceid=art.attr('file');
		var entity=art.attr('entity');
//		var user=art.attr('creator');
//		var swsid=art.attr('onsws');
		var kind='see';
		var skip=more.attr('skip');
		$.get('./servicews/viewEntityRelatives.service',{
//			user:user,
//			swsid:swsid,
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
	plist.undelegate('.e-r-v>.events>li[user]','click');
	plist.delegate('.e-r-v>.events>li[user]','click',function(e){
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
	plist.undelegate('.e-r-v>.events>li[user]','mouseleave');
	plist.delegate('.e-r-v>.events>li[user]','mouseleave',function(e){
		$(this).parents('.events').find('.user-face').remove();
	});
	
	//下载
	var plist=$('.cube > .show > .finder');
	plist.undelegate('> .box > .detail > .preview > .count > li[downloads] > span','mouseenter');
	plist.delegate('> .box > .detail > .preview > .count > li[downloads] > span','mouseenter',function(e){
		var the=$(this).parent('li[downloads]');
		the.attr('style','position: relative;');
		the.find('.e-r-v').remove();
		the.find('style[name=e-r-v]').remove();
		var art=$(this).parents('.preview').find('> .area>div[file]');
		var sourceid=art.attr('file');
		var entity=art.attr('entity');
//		var user=art.attr('creator');
//		var swsid=art.attr('onsws');
		var kind='download';
		$.get('./servicews/viewEntityRelatives.service',{
//			user:user,
//			swsid:swsid,
			kind:kind,
			sourceid:sourceid,
			entity:entity
			},
		function(data){
			the.append(data);
			the.find('div.e-r-v').attr('style','right:0;left:auto;top:15px;');
		}).error(function(e){
			alert(e.responseText);
		});
	});
	plist.undelegate('> .box > .detail > .preview > .count > li[downloads]','mouseleave');
	plist.delegate('> .box > .detail > .preview > .count > li[downloads]','mouseleave',function(e){
			var box=$(this);
			box.find('>.e-r-v').remove();
	});
	plist.undelegate('.e-r-v>.more>span[label]','click');
	plist.delegate('.e-r-v>.more>span[label]','click',function(e){
		var more=$(this).parent('.more');
		var events=more.siblings('.events');
		var art=$(this).parents('.preview').find('> .area>div[file]');
		var sourceid=art.attr('file');
		var entity=art.attr('entity');
//		var user=art.attr('creator');
//		var swsid=art.attr('onsws');
		var kind='download';
		var skip=more.attr('skip');
		$.get('./servicews/viewEntityRelatives.service',{
//			user:user,
//			swsid:swsid,
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
	plist.undelegate('.e-r-v>.events>li[user]','click');
	plist.delegate('.e-r-v>.events>li[user]','click',function(e){
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
	plist.undelegate('.e-r-v>.events>li[user]','mouseleave');
	plist.delegate('.e-r-v>.events>li[user]','mouseleave',function(e){
		$(this).parents('.events').find('.user-face').remove();
	});
})();