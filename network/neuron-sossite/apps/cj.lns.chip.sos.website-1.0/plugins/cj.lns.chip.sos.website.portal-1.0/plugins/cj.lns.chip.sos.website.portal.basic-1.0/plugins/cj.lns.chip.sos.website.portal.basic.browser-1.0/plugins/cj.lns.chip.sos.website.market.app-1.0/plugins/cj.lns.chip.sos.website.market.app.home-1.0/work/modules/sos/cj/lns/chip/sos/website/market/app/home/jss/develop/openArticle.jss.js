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
var Date = Java.type('java.util.Date');
var SimpleDateFormat = Java.type('java.text.SimpleDateFormat');
var HashMap = Java.type('java.util.HashMap');
var ArrayList = Java.type('java.util.ArrayList');

exports.flow = function(frame, circuit, plug, ctx) {
	var m = ServiceosWebsiteModule.get();
	var sws = IServicewsContext.context(frame);
	var doc = m.context().html("/develop/openArticle.html",
			m.site().contextPath(), "utf-8");
	var id = frame.parameter('id');
	var disk = m.site().diskOwner(sws.owner());
	if (!disk.existsCube(sws.swsid())) {
		throw new CircuitException('404', '视窗空间不存在');
	}
	var cube = disk.cube(sws.swsid());
	myRead(id, doc, sws, m, cube);
	printBody(id, doc, sws, m, cube);
	printComments(id, doc, sws, m, cube);
	printGreats(id, doc, sws, m, cube);
	printFollows(id, doc, sws, m, cube);
	circuit.content().writeBytes(doc.toString().getBytes());
}
function myRead(id, doc, sws, m, cube){
	var artid=id;
	//返回评论号，评论者的face
	var reviewerface=sws.visitor().face();
	var reviewer=sws.visitor().principal();//评论者
	//看看是否关注了，如果是已点则取消
	
		var ctime=System.currentTimeMillis();//评论时间
		var tuple=new HashMap();
		tuple.put('sourceid',artid);
		tuple.put('entity','myarticle');
		tuple.put('reviewer',reviewer);
		tuple.put('reviewerFace',reviewerface);
		tuple.put('kind','see');//评论类型
		tuple.put('ctime',ctime);
		var newdoc=new TupleDocument(tuple);
		cube.saveDoc('entity.relatives',newdoc);
		
	var cjql="select {'tuple':'*'}.count() from tuple entity.relatives java.lang.Long where {'tuple.entity':'myarticle','tuple.kind':'see','tuple.sourceid':'?(artid)'}";
	var q=cube.createQuery(cjql);
	q.setParameter('artid',artid);
	var count=q.count();
	doc.select('.o-art > .o-box > .desc > span[visits]>span[label]').html(String.format("浏览 (%s)",count));
}
function printFollows(id, doc, sws, m, cube) {
	var reviewer = sws.owner();

	var cjql = "select {'tuple':'*'} from tuple entity.relatives java.util.HashMap where {'tuple.kind':'follow','tuple.entity':'myarticle','tuple.sourceid':'?(artid)'}";
	var q = cube.createQuery(cjql);
	q.setParameter('artid', id);
	var tuples = q.getResultList();
	doc.select('.o-art > .o-box > .op > ul > li[action=follow] > span[value]').html(String.format("(%s)",tuples.size()));
	for (var i = 0; i < tuples.size(); i++) {
		var follow = tuples.get(i);
		if(reviewer==follow.tuple().get('reviewer')){
			doc.select('.o-art > .o-box > .op > ul > li[action=follow]').attr('title','我已关注');
			break;
		}
	}
}
function printGreats(id, doc, sws, m, cube) {
	var reviewer = sws.owner();

	var cjql = "select {'tuple':'*'} from tuple entity.relatives java.util.HashMap where {'tuple.kind':'great','tuple.entity':'myarticle','tuple.sourceid':'?(artid)'}";
	var q = cube.createQuery(cjql);
	q.setParameter('artid', id);
	var tuples = q.getResultList();
	doc.select('.o-art > .o-box > .op > ul > li[action=great] > span[value]')
			.html(String.format('(%s)', tuples.size()));
	var faceul = doc.select('.o-art > .o-box > .forum > .great > .faces')
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
			doc.select('.o-art > .o-box > .forum > .great > img.g-main').attr(
					'style', 'border:1px solid #ececec;');
		}
	}
}
function printComments(id, panel, sws, m, cube) {
	var cjql = "select {'tuple':'*'} from tuple entity.relatives java.util.HashMap where {'tuple.entity':'myarticle','tuple.sourceid':'?(artid)','tuple.kind':'comment'}";
	var q = cube.createQuery(cjql);
	q.setParameter('artid', id);
	var docs = q.getResultList();
	panel.select('.o-art > .o-box > .op > ul > li[action=forum] > span[value]')
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
	var threadul = panel.select('.o-art > .o-box > .forum > .threads');
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
				followli.select('>.forum-panel>.ctx>span[to]').html(
						followtuple.get('to'));
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
function printBody(id, doc, sws, m, cube) {

	var cjql = "select {'tuple':'*'} from tuple article.entities java.util.HashMap where {'_id':ObjectId('?(id)')}";
	var q = cube.createQuery(cjql);
	q.setParameter('id', id);
	var tuple = q.getSingleResult();
	if (tuple == null) {
		throw new CircuitException('404', '博文不存在了。');
	}
	var art = tuple.tuple();
	doc.select('.o-art').attr('artid', tuple.docid());
	doc.select('.o-art').attr('creator', tuple.tuple().get('creator'));
	doc.select('.o-art').attr('onsws', sws.swsid());
	doc.select('.o-art > .o-box > .title').html(art.get('title'));
	doc.select('.o-art > .o-box > .content').html(
			'<pre>' + art.get('content') + '</pre>');
	var createTime = art.get('createTime');
	var date = new Date(createTime);
	var format = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
	doc.select('.o-art > .o-box > .desc > span[pubdate]').html(
			format.format(date));
	var ul = doc.select('.o-art > .o-box > .media > ul > .pics > .items')
			.first();
	var pics = art.get('pictures');
	if (pics == null) {
//		doc.select('.o-art > .o-box > .media > ul > .pics>.items').empty();
//		doc.select('.o-art > .o-box > .media > ul > .pics > .show').empty();
		doc.select('.o-art > .o-box > .media > ul > .pics').remove();
	} else if (pics.length > 0) {
		var first = pics.get(0);
		var fimg = String.format('./resource/ud/%s?path=%s://pictures/&u=%s',
				first, sws.swsid(), sws.owner());
		doc.select('.o-art > .o-box > .media > ul > .pics > .show > img').attr(
				'src', fimg);
		var li = ul.select('>li').first().clone();
		ul.empty();
		for (var i = 0; i < pics.length; i++) {
			var pic = pics.get(i);
			var src = String.format(
					'./resource/ud/%s?path=%s://pictures/&u=%s', pic, sws
							.swsid(), sws.owner());
			li = li.clone();
			li.select('>img').attr('src', src);
			ul.appendChild(li);
		}
	}

	var medias = art.get('medias');
	if (medias != null && medias.length > 0) {
		var first = medias.get(0);
		var fimg = String.format('./resource/ud/%s?path=%s://medias/&u=%s',
				first, sws.swsid(), sws.owner());
		if (pics == null || pics.length < 1) {// 如果没有图片，则显示区显示成视频
			var video = getMediaLi(first, fimg, true);
			doc.select('.o-art > .o-box > .media > ul > .pics > .show').append(
					video);
		}
		for (var i = 0; i < medias.length; i++) {
			var media = medias.get(i);
			var src = String.format('./resource/ud/%s?path=%s://medias/&u=%s',
					media, sws.swsid(), sws.owner());
			var li = getMediaLi(media, src);
			ul.append(li);
		}
	}

	var files = art.get('files');
	if (files == null) {
		var ul = doc.select('.o-art > .o-box > .media > ul > .attachment>ul')
				.first();
		ul.empty();
	} else if (files.length > 0) {
		var ul = doc.select('.o-art > .o-box > .media > ul > .attachment>ul')
				.first();
		var li = ul.select('>li').first().clone();
		ul.empty();
		for (var i = 0; i < files.length; i++) {
			li = li.clone();
			var file = files.get(i);
			var src = String.format(
					'./resource/ud/%s?path=%s://documents/&u=%s', file, sws
							.swsid(), sws.owner());
			li.attr('path', src);
			li.select('>p').html(file);
			ul.appendChild(li);
		}
	}

}

function getMediaLi(fn, src, isShower) {
	var videohtml = "<video class='video-js  vjs-default-skin' controls preload='auto' poster='#' data-setup='{}'>";
	var source = "";
	var ext = '';
	ext = fn.substring(fn.lastIndexOf('.'), fn.length);
	switch (ext) {
	case '.mov':
		source = "<source type='video/quicktime' src='" + src + "'></source>";
		break;
	case '.mp3':
		source = "<source type='audio/mpeg' src='" + src + "'></source>";
		break;
	case '.rmvb':
		source = "<source type='audio/quicktime' src='" + src + "'></source>";
		break;
	case '.mkv':
		source = "<source type='audio/quicktime' src='" + src + "'></source>";
		break;
	case '.mp4':
		source = "<source type='audio/mp4' src='" + src + "'></source>";
		break;
	case '.avi':
		source = "<source type='video/x-msvideo' src='" + src + "'></source>";
		break;
	case '.webm':
		source = "<source type='video/webm' src='" + src + "'></source>";
		break;
	default:
		break;
	}

	var endvideo = "</video>";
	if (isShower) {
		return (videohtml + source + endvideo);
	}
	var videohtml2 = "<video fn='"
			+ fn
			+ "' class='video-js  vjs-default-skin' preload='auto' poster='#' data-setup='{}'>";
	var lihtml = "<li media>" + videohtml2 + source + endvideo + "</li>";
	return lihtml;
}
