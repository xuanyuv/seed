$(function(){
    var inHtml = '<div class="shade_bg"></div><div class="prompt_box"><span></span></div>';
    $("body").append(inHtml);
    $.setMenu("ul.c_menu"); //Main menu
});


/***Prompt*/
$.promptBox = function(txt, bg, t, pos){
    var txt  = txt==null ? "" : txt,
        bg   = bg ==null ? "" : bg,
        pos  = pos==null ? "" : pos,
        t    = /^\d+$/.test(t) ? t : 2000,
        o    = $("div.prompt_box"),
        p    = $("div.shade_bg");

    o.removeAttr("style");
    if(bg.length>0) {
        o.css("background", bg);
    }
    if(pos == "bottom") {
        o.css({"top":"auto", "bottom":0});
    }
    p.show();
    o.show().find("span").html(txt);
    p.delay(t).fadeOut(200);
    o.delay(t).fadeOut(200);
}


/*Main menu*/
/*
$.setMenu = function(o){
    var o = $(o);

    o.find("li > a").on("click", function(){
        var c = $(this),
            h = c.next().height();
        if(c.parent().is(".on")) {
            if(c.next().length > 0) {
                c.next().stop(true, false).animate({
                    "height" : 0
                }, 200, function(){
                    c.next().removeAttr("style");
                    c.parent().removeClass("on");
                });
            } else {
                c.parent().removeClass("on");
            }
        } else {
            c.parent().addClass("on");
            c.next().height(0).stop(true, false).animate({
                "height" : h
            }, 200);
        }
    });

    o.find("div a.on").parent().prev().find("i").css("display", "block");
}
*/
$.setMenu = function(o){
    var o = $(o);

    o.find("li > a").on("click", function(){
        var _this = $(this),
            _next = _this.next(),
            _par = _this.parent();

        _next.finish();
        if(_par.is(".on")) {
            _next.slideUp(300, function(){
                _par.removeClass("on");
            });
        } else {
            var _old = _par.siblings(".on");
            _old.find("div").stop(true, false).slideUp(300, function(){
                _old.removeClass("on");
            });

            _par.addClass("on");
            _next.slideDown(300);
        }
    });

    o.find("div a.on").parent().prev().find("i").css("display", "block");
}


/***Pub Dialog*/
$.dialogBox = function(o){
    var o    = o == null ? $("div.dialog_box") : $(o),
        p    = $("div.shade_box"),
        oTop = Math.floor(($(window).height()-o.height())/2);
        oTop = oTop<0 ? 0 : oTop;

    p.fadeIn(200);
    o.css("top", oTop).fadeIn(200);

    $(window).resize(function(){
        oTop = Math.floor(($(window).height()-o.height())/2);
        o.animate({
            "top" : oTop
        }, 200);
    });

    if(o.find("#dg_close").length>0) {
        o.find("#dg_close").on("click", function(){
            p.fadeOut(300);
            o.fadeOut(300);
        });
    }

    if(o.find(".dg_close").length>0) {
        o.find(".dg_close").on("click", function(){
            p.fadeOut(300);
            o.fadeOut(300);
        });
    }
}