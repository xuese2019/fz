let pageSize = 10;
let pageNow = 0;

page(0);

function pageUpOrDow(p){
    if(p > 0){
        pageNow = pageNow - 1;
    }else{
        pageNow = pageNow + 1;
    }
    pageNow = pageNow < 0 ? 0 : pageNow;
    page(pageNow);
}

function page(pageNow){
    this.pageNow = pageNow;
    $.ajax({
        url:root+"/api/classification/classification/"+pageSize+"/"+pageNow,
        dataType:"json",
        data:$('#classification-search-form').serialize(),
        type:"POST",
        beforeSend:function(){
            $("#datas").find('tr').remove();
        },
        success:function(req){
            if(req.length <= 0 && pageNow > 0){
                pageUpOrDow(1);
            }else{
                $(req).each(function(i,e){
                    var r = ttr((i+1),e);
                    $("#datas").append(r);
                });
            }
        },
        complete:function(){
        },
        error:function(e){
            alert2("error",e.responseText);
        }
    });
}
function ttr(i,e){
    return "<tr>"+
            "<td>"+i+"</td>"+
            "<td>"+e.name+"</td>"+
            "<td>"+
                "<button type=\"button\" class=\"btn btn-danger btn-xs\" onclick=\"remove('"+e.id+"')\">删除</button>&nbsp;&nbsp;"+
                "<button type=\"button\" class=\"btn btn-warning btn-xs\" onclick=\"openEditView('"+e.id+"')\">修改</button>&nbsp;&nbsp;"+
            "</td>"+
            "</tr>";
}
function save(){
    $.ajax({
        url:root+"/api/classification/classification",
        dataType:"text",
        data:$('#add-classification-form').serialize(),
        type:"POST",
        beforeSend:function(){
        },
        success:function(req){
            alert2("success","成功");
            $("#add-classification-model-open-button").click();
            page(0);
        },
        complete:function(){
        },
        error:function(e){
            alert2("error",e.responseText);
        }
    });
}
function remove(e){
    alert3('warning','是否确定当前操作')
    .then((isConfirm) => {
        //判断 是否 点击的 确定按钮
        if (isConfirm.value) {
            $.ajax({
                url:root+"/api/classification/classification/"+e,
                dataType:"text",
                type:"DELETE",
                beforeSend:function(){
                },
                success:function(req){
                    alert2("success","成功");
                    page(0);
                },
                complete:function(){
                },
                error:function(e){
                    alert2("error",e.responseText);
                }
            });
        }
    });
}
function openEditView(e){
    $("#id-edit").val(e);
    $("#edit-classification-model-open-button").click();
}
function edit(){
    $.ajax({
        url:root+"/api/classification/classification/"+$("#id-edit").val(),
        dataType:"text",
        data:$('#edit-classification-form').serialize(),
        type:"PUT",
        beforeSend:function(){
        },
        success:function(req){
            alert2("success","成功");
            $("#edit-classification-model-open-button").click();
            page(pageNow);
        },
        complete:function(){
        },
        error:function(e){
            alert2("error",e.responseText);
        }
    });
}
function restPwd(e){
    alert3('warning','是否确定当前操作')
    .then((isConfirm) => {
        //判断 是否 点击的 确定按钮
        if (isConfirm.value) {
            $.ajax({
                url:root+"/api/classification/classification/pwd/"+e,
                dataType:"text",
                type:"PUT",
                beforeSend:function(){
                },
                success:function(req){
                    alert2("success","成功");
                    page(0);
                },
                complete:function(){
                },
                error:function(e){
                    alert2("error",e.responseText);
                }
            });
        }
    });
}