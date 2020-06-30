findAll();
//读取所有关键字
function findAll(){
    $.ajax({
        url:root+"/k/k",
        dataType:"json",
        type:"GET",
        beforeSend:function(){
            $('#data').find("div").remove();
        },
        success:function(req){

            $(req).each(function(i,e){
                let a = '<div class="form-group">'
                        +'    <label class="col-sm-2 control-label">参数'+(i+1)+'</label>'
                        +'    <div class="col-sm-9">'
                        +'        <div class="form-control">'+e+'</div>'
                        +'    </div>'
                        +'    <div class="col-sm-1">'
                        +'        <button type="button" onclick="edit(this)" class="btn btn-box-tool"><i class="fa fa-minus"></i></button>'
                        +'    </div>'
                        +'</div>';
                $('#data').append(a);
            });
        },
        complete:function(){
        },
        error:function(e){
           console.log('error',e.responseText);
        }
    });
}
function add(obj){
    let k = $("#k").val();
    if(!k){
        alert("关键字不能为空");
        return;
    }
    $.ajax({
        url:root+"/k/k",
        dataType:"text",
        data:{
            "k": k
        },
        type:"POST",
        beforeSend:function(){
            //请求前的处理
            $(obj).attr('disabled',true);
        },
        success:function(req){
            //请求成功时处理
            findAll();
        },
        complete:function(){
            $(obj).attr('disabled',false);
        },
        error:function(e){
            console.log('error',e.responseText);
        }
    });
}
function edit(obj){
    let k = $(obj).parents(".form-group").find(".form-control:eq(0)").text();
    console.log(k)
    if(!k){
        alert("关键字不能为空");
        return;
    }
    $.ajax({
        url:root+"/k/k",
        dataType:"text",
        data:{
            "k": k
        },
        type:"POST",
        beforeSend:function(){
            //请求前的处理
            $(obj).attr('disabled',true);
        },
        success:function(req){
            //请求成功时处理
            findAll();
        },
        complete:function(){
            $(obj).attr('disabled',false);
        },
        error:function(e){
            console.log('error',e.responseText);
        }
    });
}
function test(obj){
    $.ajax({
        url:root+"/t/t",
        dataType:"text",
        type:"get",
        beforeSend:function(){
            //请求前的处理
            $(obj).attr('disabled',true);
        },
        success:function(req){
            //请求成功时处理
            alert("成功");
        },
        complete:function(){
            $(obj).attr('disabled',false);
        },
        error:function(e){
            console.log('error',e.responseText);
        }
    });
}