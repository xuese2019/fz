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
        url:root+"/api/user/user/"+pageSize+"/"+pageNow,
        dataType:"json",
        data:$('#user-search-form').serialize(),
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
            "<td>"+e.acc+"</td>"+
            "<td>"+(e.type == 0 ? "<span class=\"badge bg-green\">正常</span>" : "<span class=\"badge bg-red\">离职</span>")+"</td>"+
            "<td>"+
                "<button type=\"button\" class=\"btn btn-danger btn-xs\" onclick=\"remove('"+e.id+"')\">删除</button>&nbsp;&nbsp;"+
                "<button type=\"button\" class=\"btn btn-warning btn-xs\" onclick=\"openEditView('"+e.id+"')\">修改</button>&nbsp;&nbsp;"+
                "<button type=\"button\" class=\"btn btn-warning btn-xs\" onclick=\"restPwd('"+e.id+"')\">重置密码</button>&nbsp;&nbsp;"+
            "</td>"+
            "</tr>";
}
function save(){
    $.ajax({
        url:root+"/api/user/user",
        dataType:"text",
        data:$('#add-user-form').serialize(),
        type:"POST",
        beforeSend:function(){
        },
        success:function(req){
            alert2("success","成功");
            $("#add-user-model-open-button").click();
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
                url:root+"/api/user/user/"+e,
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
    $("#edit-user-model-open-button").click();
}
function edit(){
    $.ajax({
        url:root+"/api/user/user/"+$("#id-edit").val(),
        dataType:"text",
        data:$('#edit-user-form').serialize(),
        type:"PUT",
        beforeSend:function(){
        },
        success:function(req){
            alert2("success","成功");
            $("#edit-user-model-open-button").click();
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
                url:root+"/api/user/user/pwd/"+e,
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