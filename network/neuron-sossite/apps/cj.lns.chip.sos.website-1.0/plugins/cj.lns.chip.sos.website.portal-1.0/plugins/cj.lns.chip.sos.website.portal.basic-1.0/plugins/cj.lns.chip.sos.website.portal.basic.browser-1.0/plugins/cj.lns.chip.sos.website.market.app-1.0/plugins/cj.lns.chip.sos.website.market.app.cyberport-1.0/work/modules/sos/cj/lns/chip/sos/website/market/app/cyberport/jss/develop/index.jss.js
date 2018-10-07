/*
 * 说明：
 * 作者：extends可以实现一种类型，此类型将可在java中通过调用服务提供器的.getServices(type)获取到。
 * <![jss:{
		scope:'runtime'
 	}
 ]>
 <![desc:{
	ttt:'2323',
	obj:{
		name:'09skdkdk'
		}
 * }]>
 */
//var imports = new JavaImporter(java.io, java.lang)导入类型的范围，单个用Java.type
var Frame = Java.type('cj.studio.ecm.frame.Frame');
var FormData = Java.type('cj.studio.ecm.frame.FormData');
var FieldData = Java.type('cj.studio.ecm.frame.FieldData');
var Circuit = Java.type('cj.studio.ecm.frame.Circuit');
var ISubject = Java.type('cj.lns.chip.sos.website.framework.ISubject');
var String = Java.type('java.lang.String');
var CircuitException = Java.type('cj.studio.ecm.graph.CircuitException');
var ServiceosWebsiteModule = Java
		.type('cj.lns.common.sos.website.moduleable.ServiceosWebsiteModule');
var Gson = Java.type('cj.ultimate.gson2.com.google.gson.Gson');
var StringUtil = Java.type('cj.ultimate.util.StringUtil');
var Document = Java.type('org.jsoup.nodes.Document');
var Jsoup = Java.type('org.jsoup.Jsoup');
var WebUtil = Java.type('cj.studio.ecm.net.web.WebUtil');
var IServicewsContext = Java
		.type('cj.lns.chip.sos.website.framework.IServicewsContext');
var TupleDocument = Java.type('cj.lns.chip.sos.cube.framework.TupleDocument');
var System = Java.type('java.lang.System');
var HashMap = Java.type('java.util.HashMap');
var ArrayList = Java.type('java.util.ArrayList');
var Mailbox = Java.type('cj.studio.ecm.sns.mailbox.Mailbox');
var ByteArrayOutputStream = Java.type('java.io.ByteArrayOutputStream');
var Integer=Java.type('java.lang.Integer');
var SimpleDateFormat=Java.type('java.text.SimpleDateFormat');
var Date=Java.type('java.util.Date');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws = IServicewsContext.context(frame);
	var iobox = frame.parameter('iobox');
	if('outbox'!=iobox){
		unreadAllAppMessages(sws, m);
	}
	
	var doc = m.context().html("/develop/index.html", m.site().contextPath(),
			"utf-8");
	var filterById = frame.parameter('filterById');
	var skip=0;
	if (!StringUtil.isEmpty(frame.parameter('skip'))) {
		skip=parseInt(frame.parameter('skip'));
	}
	
	var disk = m.site().diskOwner(sws.owner());
	var cube = disk.cube(sws.swsid());
	var list = getDynamics(iobox, filterById, skip, sws, m,cube);
	if(!sws.isOwner()){
		doc.select('.home-panel > .shuoshuo').remove();
	}
	if(list.size()<1){
		if(skip==0){
			if('shuoshuo'!=filterById){
				doc.select('.home-panel > .shuoshuo').remove();
			}
			doc.select('.home-panel>.art-ul').remove();
			printNav(filterById, list, doc,sws, m)
			circuit.content().writeBytes(doc.toString().getBytes());
		}
		return;
	}
	printNav(filterById, list, doc,sws, m);
	printView(filterById, list, doc,sws, m);
	var ul=doc.select('.home-panel > .art-ul').first();
	ul.append("<li class='article' more='"+(skip+list.size())+"' iobox='"+(iobox==null?'':iobox)+"' filterById='"+(filterById==null?'':filterById)+"'>加载更多...</li>");
	if('mine'!=filterById){
		doc.select('.home-panel > .art-ul > .article > .body > .source').remove();
	}
	if(skip<1){
		circuit.content().writeBytes(doc.toString().getBytes());
	}else{
		circuit.content().writeBytes(ul.html().getBytes());
	}
}
function getDynamics(iobox, filterById, skip, sws, m,cube) {
	var cjql = '';
	if (StringUtil.isEmpty(filterById)) {
		cjql = "select {'tuple.appId':1} from tuple sns.session java.util.HashMap where {'tuple.appId':{$ne:'mine'},'tuple.appCode':'dynamic'}";
	} else {
		cjql = String
				.format(
						"select {'tuple.appId':1} from tuple sns.session java.util.HashMap where {'tuple.appCode':'dynamic','tuple.appId':'%s'}",
						filterById);
	}
	var q = cube.createQuery(cjql);
	var result = q.getResultList();
	var ids = new ArrayList();
	for (var i = 0; i < result.size(); i++) {
		var doc = result.get(i);
		ids.add(String.format("'%s'", doc.docid()));
	}
	if (StringUtil.isEmpty(iobox)) {
		cjql = String.format("select {'tuple':'*'}.sort({'tuple.sendTime':-1}).limit(5).skip(%s) from tuple iobox java.util.HashMap where {'tuple.sid':{$in:?(sid)}}",skip+'');
	} else {
		cjql = String
				.format(
						"select {'tuple':'*'}.sort({'tuple.sendTime':-1}).limit(5).skip(%s) from tuple iobox java.util.HashMap where {'tuple.sid':{$in:?(sid)},'tuple.iobox':'%s'}",skip+'',
						iobox);
	}
	q = cube.createQuery(cjql);
	q.setParameter("sid", ids);
	return q.getResultList();
}
function visitCount(entity,sourceid,  sws, cube){
	var reviewerface=sws.visitor().face();
	var reviewer=sws.visitor().principal();//浏览者
	
	var ctime=System.currentTimeMillis();//浏览者时间
	var tuple=new HashMap();
	tuple.put('sourceid',sourceid);
	tuple.put('entity',entity);
	tuple.put('reviewer',reviewer);
	tuple.put('reviewerFace',reviewerface);
	tuple.put('kind','see');//相关类型
	tuple.put('ctime',ctime);
	var newdoc=new TupleDocument(tuple);
	cube.saveDoc('entity.relatives',newdoc);
	
	var cjql="select {'tuple':'*'}.count() from tuple entity.relatives java.lang.Long where {'tuple.entity':'?(entity)','tuple.kind':'see','tuple.sourceid':'?(sourceid)'}";
	var q=cube.createQuery(cjql);
	q.setParameter('sourceid',sourceid);
	q.setParameter('entity',entity);
	return q.count();
}

function getMime(fn){
	var name=fn.substring(0,fn.indexOf('?'));
	name=name.substring(name.lastIndexOf("/")+1,name.length);
	var at=name.lastIndexOf('.');
	if(at<0){//无扩展名
		return "video/mp4";
	}
	var ext=name.substring(at,name.length)	;
	var mime='video/mp4';
	if('.mov'==ext){
		mime='video/quicktime';
	}else if('.rmvb'==ext){
		mime='video/vnd.rn-realvideo';
	}else if('.avi'==ext){
		mime='video/x-msvideo';
	}else if('.webm'==ext){
		mime='video/webm';
	}else if('.mp3'==ext||'.mp2'==ext||'.aac'==ext||'.ogg'==ext){
		mime='video/mp3';
	}else{
		mime="video/mp4";
	}
	return mime;
}
function printNav(filterById, list, doc,sws, m) {
	var pathname = '';
	var pnE=doc.select('.home-panel > .title > .pos > span[kind]');
	switch (filterById) {
	case 'shuoshuo':
		pathname = '说说';
		pnE.html(pathname);
		pnE.attr('kind','shuoshuo');
		break;
	case 'myarticle':
		pathname = '博客';
		pnE.html(pathname);
		pnE.attr('kind','myarticle');
		break;
	case 'myproduct':
		pathname = '产品';
		pnE.html(pathname);
		pnE.attr('kind','myproduct');
		break;
	case 'myspace':
		pathname = '视窗空间';
		pnE.html(pathname);
		pnE.attr('kind','myspace');
		break;
	case 'homespace':
		pathname = '主空间';
		pnE.html(pathname);
		pnE.attr('kind','homespace');
		break;
	case 'mine':
		pathname = '@与我相关';
		pnE.html(pathname);
		pnE.attr('kind','mine');
		break;
	case 'undefined':
	case null:
	case '':
		doc.select('.home-panel > .title > .pos').attr('style','display:none;');
		break;
	}
	return pathname;
	
}
function printView(filterById, list, doc,sws, m) {
	var pathname =  printNav(filterById, list, doc,sws, m) ;
	
	var ul=doc.select('.home-panel > .art-ul').first();
	var artli=ul.select('> .article').first().clone();
	ul.empty();
	var format=new SimpleDateFormat('yyyy-MM-dd hh:mm');
	for(var i=0;i<list.size();i++){
		var tupleDoc=list.get(i);
		var tuple=tupleDoc.tuple();
		var li=artli.clone();
		li.attr('ioboxid',tupleDoc.docid());
		var source=tuple.get('source');
		if(source.get('id')!=null){
			li.attr('sourceid',source.get('id'));
		}
		if(source.get('entity')!=null){
			li.attr('entity',source.get('entity'));
		}
		li.attr('appid',source.get('appId'));
		if(source.get('creator')!=null){
			li.attr('creator',source.get('creator'));
		}
		if(source.get('onsws')!=null){
			li.attr('onsws',source.get('onsws'));
		}
//		li.attr('onsws',tuple.get('senderOnSws'));
		var body=tuple.get('body');
		if(body==null||body.size()==0){
			li.select('> .body > .content>pre').html('&nbsp;');
		}else{
			var out=new ByteArrayOutputStream();
			for(var j=0;j<body.size();j++){
				out.write(Integer.valueOf(body.get(j)));
			}
			out.close();
			li.select('> .body > .content>pre').html(new String(out.toByteArray()));
			if(true==tuple.get('source').get('cnt-hidden')){
				li.select('> .body > .content>pre').append("<p class='extends'>展开...</p>");
			}
			var title=tuple.get('source').get('title');
			var id=tuple.get('source').get('id');
			if(title==null){
				li.select('>.head>.left>ul>li[sign]>p>span[label]').html('&nbsp;');
			}
			var e=li.select('>.head>.left>ul>li[sign]>p>span[label]');
			var s=li.select('> .head > .left > ul > li[sign] > span[source]');
			var appId=tuple.get('source').get('appId');
			switch(appId){
			case 'shuoshuo':
				e.html('&nbsp;');
				s.html('说说');
				break;
			case 'myarticle':
				e.html('《'+title+'》');
				s.html('博客');
				break;
			case 'myproduct':
				e.html('《'+title+'》');
				s.html('产品');
				break;
			case 'myspace':
				e.html(id.replace('//','/'));
				s.html('视窗空间');
				break;
			case 'homespace':
				e.html(id.replace('//','/'));
				s.html('主空间');
				break;
			case 'mine':
				e.html('&nbsp;');
				var entityName='';
				var enti=source.get('entity');
				if('shuoshuo'==enti){
					entityName='说说';
				}else if('myarticle'==enti){
					entityName='博客';
				}else if('myproduct'==enti){
					entityName='产品';
				}else if('myspace'==enti){
					entityName='视窗空间';
				}else if('homespace'==enti){
					entityName='主空间';
				}
				s.html(entityName);
				break;
			case null:
			case 'undefined':
			case '':
				e.html('&nbsp;');
				s.html('&nbsp;');
				break;
			default:
				e.html('&nbsp;');
				s.html('&nbsp;');
				break;
			}
				
		}
		var senderFace=tuple.get('source').get('senderFace');
		if(senderFace==null){
			li.select('> .head > .left > ul > li[name]>span[label]').html(tuple.get('sender'));
			li.select('> .head > .left > ul > li[name]').attr('user',tuple.get('sender'));
			li.select('> .head > .left > img').attr('src','#');
		}else{
			li.select('> .head > .left > ul > li[name]>span[label]').html(tuple.get('sender'));
			li.select('> .head > .left > ul > li[name]').attr('user',tuple.get('sender'));
			var src=String.format("./resource/ud/%s?path=home://system/img/faces/&u=%s",senderFace.get('head'),tuple.get('sender'));
			li.select('> .head > .left > img').attr('src',src);
		}
		var senderDate=tuple.get('sendTime');
		li.select(' > .head > .right>ul>li.date').html(format.format(new Date(senderDate)));
		
		if('mine'!=source.get('appId')){
			var picregion=li.select('> .body > .pics').first();
			var shower=picregion.select('>.show').first();
			var itemsul=picregion.select('>.items').first();
			var itemli=itemsul.select('>li').first().clone();
			itemsul.empty();
			
			var pictures=tuple.get('source').get('pictures');
			var hasShower=false;
			if(pictures!=null){
				for(var t=0;t<pictures.size();t++){
					var pli=itemli.clone();
					pli.select('>img').attr('src',pictures.get(t+''));
					itemsul.append(pli);
				}
				if(pictures.size()>0){
					shower.select('>img').attr('src',pictures.get('0'));
					hasShower=true;
				}
			}
			
			var medias=tuple.get('source').get('medias');
			if(medias!=null){
				for(var t=0;t<medias.size();t++){//添加视频图片
					var pli=itemli.clone();
					var fn=medias.get(t+'');
					pli.attr('media',fn);
					var mime=getMime(fn);
					pli.attr('mime',mime);
					pli.select('>img').attr('src','img/vedio.svg');
					itemsul.append(pli);
				}
				if(pictures.size()<1&&medias.size()>0){//在视区中添加视频
					shower.select('>img').attr('src',pictures.get('0'));
					hasShower=true;
				}
			}
			if(!hasShower){
				picregion.remove();
			}
			
			var attachment=li.select('> .body > .attachment').first();
			var attachmentul=attachment.select('>ul').first();
			var attachmentli=attachmentul.select('>li').first().clone();
			attachmentul.empty();
			var files=tuple.get('source').get('files');
			if(files!=null&&files.size()>0){
				for(var t=0;t<files.size();t++){//添加视频图片
					var pli=attachmentli.clone();
					var fn=files.get(t+'');
					pli.attr('file',fn);
					var name=fn.substring(0,fn.indexOf('?'));
					name=name.substring(name.lastIndexOf("/")+1,name.length);
					name=name.substring(name.indexOf('_')+1,name.length);
					pli.select('>p[name]').html(name);
					attachmentul.append(pli);
				}
			}else{
				attachment.remove();
			}
		}else{
			li.select('> .body > .attachment').remove();
			li.select('> .body > .pics').remove();
			li.select('>.show').remove();
		}
		
		var disk = m.site().diskOwner(source.get('creator'));
		var cube = disk.cube(source.get('onsws'));
		var entity=source.get('entity');
		if('mine'==source.get('appId')){
			printMine(tuple, li, sws, m, cube);
			
		}
		var seeCount=visitCount(entity,source.get('id'), sws,cube);
		li.select(' > .head > .right > ul > .browser > span[value]').html(seeCount);
		printGreats(entity,source.get('id'), li, sws, m, cube);
		if('myarticle'==entity||'myproduct'==entity){
			printFollows(entity,source.get('id'), li, sws, m, cube);
		}else{
			li.select('>.body > .op > ul > li[action=follow]').remove();
		}
		printComments(entity,source.get('id'), li, sws, m, cube);
		
		ul.append(li);
	}
}

function printMine(tuple, li, sws, m, cube) {
	var source=tuple.get('source');
	var text='';
	var sourceTitle=source.get('sourceTitle');
	if(sourceTitle!=null&&sourceTitle!='&nbsp;'){
		text=sourceTitle+' ';
	}
	var sourceCnt=source.get('sourceCnt');
	if(sourceCnt!=null){
		text=text+sourceCnt;
	}
	var pictures=source.get('pictures');
	if(pictures!=null&&pictures.size()>0){
		li.select(' > .body > .source > img').attr('src',pictures.get('0'));
	}
	if(source.get('ioboxid')!=null){
		li.select(' > .body > .source').attr('ioboxid',source.get('ioboxid'));
	}
	li.select(' > .body > .source > .text>span[person]').html(source.get('creator'));
	li.select(' > .body > .source > .text').append(text);
}
function printFollows(entity,sourceid, panel, sws, m, cube) {
	var reviewer = sws.owner();

	var cjql = "select {'tuple':'*'} from tuple entity.relatives java.util.HashMap where {'tuple.entity':'?(entity)','tuple.kind':'follow','tuple.sourceid':'?(sourceid)'}";
	var q = cube.createQuery(cjql);
	q.setParameter('sourceid', sourceid);
	q.setParameter('entity', entity);
	var tuples = q.getResultList();
	panel.select('>.body > .op > ul > li[action=follow] > span[value]').html(String.format("(%s)",tuples.size()));
	for (var i = 0; i < tuples.size(); i++) {
		var follow = tuples.get(i);
		if(reviewer==follow.tuple().get('reviewer')){
			panel.select('>.body > .op > ul > li[action=follow]').attr('title','我已关注');
			break;
		}
	}
}
function printComments(entity,sourceid, panel, sws, m, cube) {
	var cjql = "select {'tuple':'*'} from tuple entity.relatives java.util.HashMap where {'tuple.entity':'?(entity)','tuple.sourceid':'?(sourceid)','tuple.kind':'comment'}";
	var q = cube.createQuery(cjql);
	q.setParameter('sourceid', sourceid);
	q.setParameter('entity', entity);
	var docs = q.getResultList();
	panel.select('>.body> .op > ul > li[action=forum] > span[value]')
			.html(String.format('(%s)', docs.size()));
	var followMap = new HashMap();
	var threadList = new ArrayList();
	// 下面两次循环将评论打印出来
	for (var i = 0; i < docs.size(); i++) {
		var doc = docs.get(i);
		var thread = doc.tuple().get('thread');
		if ('$'.equals(thread)) {
			// 打印thread
			threadList.add(doc);
		} else {
			// 求跟贴
			var follows = followMap.get(thread);
			if (follows == null) {
				follows = new ArrayList();
				followMap.put(thread, follows);
			}
			follows.add(doc);
		}
	}
	var threadul = panel.select('>.body> .forum > .threads').first();
	var threadliE = threadul.select('>.thread').first().clone();
	threadul.empty();
	
	for (var i = 0; i < threadList.size(); i++) {
		var threadli = threadliE.clone();
		var thread = threadList.get(i);
		var threadtuple = thread.tuple();
		var src = String.format(
				"./resource/ud/%s?path=home://system/img/faces&u=%s",
				threadtuple.get('reviewerFace').get('head'), threadtuple
						.get('reviewer'));
		threadli.select('>img').attr('src', src);
		threadli.select('>img').attr('title', threadtuple.get('reviewer'));
		threadli.select('>.forum-panel>.ctx').attr('uid',
				threadtuple.get('reviewer'));
		threadli.select('>.forum-panel>.ctx').attr('title',
				threadtuple.get('reviewerFace').get('nick'));
		threadli.select('>.forum-panel>.ctx>span[name]').html(
				threadtuple.get('reviewer'));
		threadli.select('>.forum-panel>.ctx>p[talk]').html(
				threadtuple.get('comment'));
		threadli.select('>.forum-panel>.op>span[close]').attr('commentid',
				thread.docid());
		var format = new SimpleDateFormat("hh:mm MM月dd日");
		var timeDisplay = format.format(new Date(threadtuple.get('ctime')));
		threadli.select('>.forum-panel>.op>span[time]').html(timeDisplay);

		var follows = followMap.get(thread.docid());
		if (follows != null) {
			var followul = threadli.select('>.forum-panel>.follows>ul').first();
			var followli = followul.select('>li').first().clone();
			followul.empty();
			for (var j = 0; j < follows.size(); j++) {
				followli = followli.clone();
				var follow = follows.get(j);
				var followtuple = follow.tuple();

				var src = String.format(
						"./resource/ud/%s?path=home://system/img/faces&u=%s",
						followtuple.get('reviewerFace').get('head'),
						followtuple.get('reviewer'));
				followli.select('>img').attr('src', src);
				followli.select('>img').attr('title',
						followtuple.get('reviewer'));
				followli.select('>.forum-panel>.ctx').attr('uid',
						followtuple.get('reviewer'));
				followli.select('>.forum-panel>.ctx>span[name]').html(
						followtuple.get('reviewer'));
				if(followtuple.containsKey('to')){
					followli.select('>.forum-panel>.ctx>span[to]').html(
							followtuple.get('to'));
				}
				followli.select('>.forum-panel>.ctx>p[talk]').html(
						followtuple.get('comment'));
				followli.select('>.forum-panel>.op>span[close]').attr(
						'commentid', follow.docid());
				var format = new SimpleDateFormat("hh:mm MM月dd日");
				var timeDisplay = format.format(new Date(followtuple
						.get('ctime')));
				followli.select('>.forum-panel>.op>span[time]').html(
						timeDisplay);

				followul.append(followli);
			}
		} else {
			threadli.select('>.forum-panel>.follows>ul').empty();
		}
		threadul.append(threadli);
	}
}

function printGreats(entity,sourceid, panel, sws, m, cube) {
	var reviewer = sws.owner();
	
	var cjql = "select {'tuple':'*'} from tuple entity.relatives java.util.HashMap where {'tuple.entity':'?(entity)','tuple.kind':'great','tuple.sourceid':'?(sourceid)'}";
	var q = cube.createQuery(cjql);
	q.setParameter('sourceid', sourceid);
	q.setParameter('entity', entity);
	var tuples = q.getResultList();
	panel.select('>.body> .op > ul > li[action=great] > span[value]')
			.html(String.format('(%s)', tuples.size()));
	var faceul = panel.select('>.body > .forum > .great > .faces')
			.first();
	var faceli = faceul.select('>li[face]').first().clone();
	faceul.empty();
	for (var i = 0; i < tuples.size(); i++) {
		var great = tuples.get(i);
		var greattuple = great.tuple();
		faceli = faceli.clone();
		faceli.attr('uid', greattuple.get('reviewer'));
		faceli.select('img').attr(
				'src',
				String.format(
						"./resource/ud/%s?path=home://system/img/faces/&u=%s",
						greattuple.get('reviewerFace').get('head'), greattuple
								.get('reviewer')));
		faceul.append(faceli);
		if (greattuple.get('reviewer') == reviewer) {
			panel.select('>.body > .forum > .great > img.g-main').attr(
					'style', 'border:1px solid #ececec;');
		}
	}
}
function unreadAllAppMessages(sws, m) {
	var f = new Frame(
			"flagMessageReadedByAppCode /device/mailbox/inbox peer/1.0");
	f.parameter("appCode", 'dynamic');
	f.parameter("owner", sws.owner());
	f.parameter('swsid', sws.swsid());
	var c = new Circuit("peer/1.0 200 ok");
	m.site().out().flow(f, c);
}