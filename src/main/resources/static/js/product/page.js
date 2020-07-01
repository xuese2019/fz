let pageSize = 10;
let pageNow = 0;

//加载产品分类
findClassification();

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
        url:root+"/api/product/product/"+pageSize+"/"+pageNow,
        dataType:"json",
        data:$('#product-search-form').serialize(),
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
    let cfId = "";
    $("#classificationId-add").find("option").each(function(i,o){
        if($(o).attr("value") == e.classificationId){
            cfId = $(o).text();
        }
    });
    return "<tr>"+
            "<td>"+i+"</td>"+
            "<td><img src=\""+root+e.img+"\" width=\"16\" height=\"16\"></td>"+
            "<td>"+e.name+"</td>"+
            "<td>"+
                (
                    e.shelf == 0
                        ?
                        "<span class=\"btn badge bg-green\" onclick=\"uOrD('"+e.id+"')\">上架</span>"
                        :
                        "<span class=\"btn badge bg-red\" onclick=\"uOrD('"+e.id+"')\">下架</span>"
                )+
            "</td>"+
            "<td>"+cfId+"</td>"+
            "<td>"+e.price+"</td>"+
            "<td>"+e.brief+"</td>"+
            "<td>"+
//                "<button type=\"button\" class=\"btn btn-danger btn-xs\" onclick=\"remove('"+e.id+"')\">删除</button>&nbsp;&nbsp;"+
                "<button type=\"button\" class=\"btn btn-warning btn-xs\" onclick=\"openEditView('"+e.id+"')\">修改</button>&nbsp;&nbsp;"+
            "</td>"+
            "</tr>";
}
function save(){
    $.ajax({
        url:root+"/api/product/product",
        dataType:"text",
        data:$('#add-product-form').serialize(),
        type:"POST",
        beforeSend:function(){
        },
        success:function(req){
            alert2("success","成功");
            $("#add-product-model-open-button").click();
            $("#add-product-form")[0].reset();
            page(0);
        },
        complete:function(){
        },
        error:function(e){
            alert2("error",e.responseText);
        }
    });
}
//function remove(e){
//    alert3('warning','是否确定当前操作')
//    .then((isConfirm) => {
//        //判断 是否 点击的 确定按钮
//        if (isConfirm.value) {
//            $.ajax({
//                url:root+"/api/product/product/"+e,
//                dataType:"text",
//                type:"DELETE",
//                beforeSend:function(){
//                },
//                success:function(req){
//                    alert2("success","成功");
//                    page(0);
//                },
//                complete:function(){
//                },
//                error:function(e){
//                    alert2("error",e.responseText);
//                }
//            });
//        }
//    });
//}
function openEditView(e){
    $("#id-edit").val(e);
    $("#edit-product-model-open-button").click();
    $.ajax({
        url:root+"/api/product/product/"+e,
        dataType:"json",
        type:"GET",
        beforeSend:function(){
        },
        success:function(req){
            $("#name-edit").val(req.name);
            $("#classificationId-edit").val(req.classificationId);
            $("#price-edit").val(req.price);
            $("#brief-edit").val(req.brief);
            $("#img-edit").val(req.img);
            $("#img-edit-img").attr("src",req.img);
        },
        complete:function(){
        },
        error:function(e){
            console.log(e);
        }
    });
}
function edit(){
    $.ajax({
        url:root+"/api/product/product/"+$("#id-edit").val(),
        dataType:"text",
        data:$('#edit-product-form').serialize(),
        type:"PUT",
        beforeSend:function(){
        },
        success:function(req){
            alert2("success","成功");
            $("#edit-product-model-open-button").click();
            $("#edit-product-form")[0].reset();
            $("#img-edit-img").attr("src","");
            page(pageNow);
        },
        complete:function(){
        },
        error:function(e){
            alert2("error",e.responseText);
        }
    });
}
function findClassification(){
    $.ajax({
        url:root+"/api/classification/classification/0/0",
        dataType:"json",
        type:"POST",
        beforeSend:function(){
        },
        success:function(req){
            $(req).each(function(i,e){
                $(".classification").append("<option value=\""+e.id+"\">"+e.name+"</option>");
            });
        },
        complete:function(){
        },
        error:function(e){
        }
    });
}

function uOrD(e){
    alert3('warning','是否确定当前操作')
    .then((isConfirm) => {
        //判断 是否 点击的 确定按钮
        if (isConfirm.value) {
            $.ajax({
                url:root+"/api/product/product/uord/"+e,
                dataType:"text",
                type:"GET",
                beforeSend:function(){
                },
                success:function(req){
                    alert2("success","成功");
                    page(pageNow);
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

function upFile(e,t){
    if(e.files[0].size > 0){
        var formData = new FormData();
        formData.append("file", e.files[0]);
        $.ajax({
            url:root+'/api/file/upload',
            dataType:'text',
            type:'POST',
            async: false,//必须同步
            data: formData,
            processData: false, // 使数据不做处理
            contentType: false, // 不要设置Content-Type请求头
            success: function(data){
                if(t === "add"){
                    $("#img-add").val(data)
                }else{
                    $("#img-edit").val(data)
                }
                alert2("success","成功");
            },
            error:function(response){
               alert2("error",e.responseText);
            }
        });
    }
}