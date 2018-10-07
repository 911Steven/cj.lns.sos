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
var IServicewsContext = Java.type('cj.lns.chip.sos.website.framework.IServicewsContext');

exports.fileMedia=function(file,fullpath,media){
	var img= './swssite/develop/img/file.svg';
	var name=file.name();
	var mediaimg=media.select('>img');
	var at=name.lastIndexOf('.');
	if(at<0){//无扩展名
		if(file.spaceLength()>1024*1024){
			mediaimg.attr('src',img);
			return true;
		}
		var content=new String(file.reader(0).readFully());
		content=" <pre>"+content+"</pre>";
		media.select('>img').remove();
		media.append(content);
		return true;
	}
	var ext=name.substring(at,name.length)	;
	switch(ext){
	case '.txt':
		if(file.spaceLength()>1024*1024){
			mediaimg.attr('src',img);
			return true;
		}
		var content=new String(file.reader(0).readFully());
		content=" <pre>"+content+"</pre>";
		media.select('>img').remove();
		media.append(content);
		return true;
	case '.gif':
	case '.svg':
	case '.png':
	case '.tif':
	case '.jpg':
		mediaimg.attr('src',fullpath);
		return true;
	case '.doc':
	case '.docx':
		img='./swssite/develop/img/word.svg';
		mediaimg.attr('src',img);
		return true;
	case '.rtf':
		img='./swssite/develop/img/rtf.svg';
		mediaimg.attr('src',img);
		return true;
	case '.xls':
	case '.xlsx':
		img='./swssite/develop/img/exl.svg';
		mediaimg.attr('src',img);
		return true;
	case '.ppt':
		img='./swssite/develop/img/ppt.svg';
		mediaimg.attr('src',img);
		return true;
	case '.mov':
	case '.rmvb':
	case '.mp4':
	case '.avi':
	case '.webm':
		//img='./swssite/develop/img/video.svg';
		var mime='video/mp4';
		if('.mov'==ext){
			mime='video/quicktime';
		}else if('.rmvb'==ext){
			mime='video/vnd.rn-realvideo';
		}else if('.avi'==ext){
			mime='video/x-msvideo';
		}else if('.webm'==ext){
			mime='video/webm';
		}
		var video=" <video id='example_video_1' class='video-js vjs-default-skin' controls preload='none' width='100%' height='100%' " +
				"data-setup='{ 'html5' : { 'nativeTextTracks' : false } }'>" +
				" <source src='"+fullpath+"' type='"+mime+"'>" +
				" <p class='vjs-no-js'>To view this video please enable JavaScript, and consider upgrading to a web browser that <a href='http://videojs.com/html5-video-support/' target='_blank'>supports HTML5 video</a></p>" +
				" </video>";
		media.select('>img').remove();
		media.append(video);
		return true;
	case '.mp3':
	case '.mp2':
	case '.ogg':
	case '.aac':
		//img='./swssite/develop/img/radio.svg';
		var mime='video/mp3';
		var video=" <video id='example_video_1' class='video-js vjs-default-skin' controls preload='none' width='100%' height='100%' " +
		"data-setup='{ 'html5' : { 'nativeTextTracks' : false } }'>" +
		" <source src='"+fullpath+"' type='"+mime+"'>" +
		" <p class='vjs-no-js'>To view this video please enable JavaScript, and consider upgrading to a web browser that <a href='http://videojs.com/html5-video-support/' target='_blank'>supports HTML5 video</a></p>" +
		" </video>";
		media.select('>img').remove();
		media.append(video);
		return true;
	case '.zip':
	case '.gz':
	case '.rar':
	case '.jar':
	case '.gz2':
		img='./swssite/develop/img/zip.svg';
		mediaimg.attr('src',img);
		return true;
	default:
		mediaimg.attr('src',img);
		return true;	
	}
}


exports.getFileImgSrc = function getFileImgSrc(name){
	var img= './swssite/develop/img/file.svg';
	var at=name.lastIndexOf('.');
	if(at<0){
		return img;
	}
	var ext=name.substring(at,name.length)	;
	switch(ext){
	case '.gif':
	case '.svg':
	case '.png':
	case '.tif':
	case '.jpg':
		img='./swssite/develop/img/pic.svg';
		break;
	case '.doc':
	case '.docx':
		img='./swssite/develop/img/word.svg';
		break;
	case '.rtf':
		img='./swssite/develop/img/rtf.svg';
		break;
	case '.xls':
	case '.xlsx':
		img='./swssite/develop/img/exl.svg';
		break;
	case '.ppt':
		img='./swssite/develop/img/ppt.svg';
		break;
	case '.mov':
	case '.rmvb':
	case '.mp4':
	case '.avi':
	case '.webm':
		img='./swssite/develop/img/video.svg';
		break;
	case '.mp3':
	case '.mp2':
	case '.ogg':
	case '.aac':
		img='./swssite/develop/img/radio.svg';
		break;
	case '.zip':
	case '.gz':
	case '.rar':
	case '.jar':
	case '.gz2':
		img='./swssite/develop/img/zip.svg';
		break;
	}
	return img;
}
