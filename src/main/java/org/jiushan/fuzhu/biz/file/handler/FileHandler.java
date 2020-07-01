package org.jiushan.fuzhu.biz.file.handler;

import lombok.extern.slf4j.Slf4j;
import org.jiushan.fuzhu.biz.product.db.ProductRepository;
import org.jiushan.fuzhu.sys.util.FileUtil;
import org.jiushan.fuzhu.util.uuid.UuidUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.time.LocalDate;
import java.util.StringJoiner;

@Slf4j
@Component
public class FileHandler {

    private ProductRepository productRepository;
    private FileUtil fileUtil;

    public FileHandler(
            ProductRepository productRepository,
            FileUtil fileUtil
    ) {
        this.productRepository = productRepository;
        this.fileUtil = fileUtil;
    }

    /**
     * 上传
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> upload(ServerRequest request) {
        LocalDate localDate = LocalDate.now();
        String d = "" + localDate.getYear()
                + localDate.getMonth()
                + localDate.getDayOfMonth();
        File filePath = new File(fileUtil.getFile() + d);
        if (!filePath.exists()) {
            boolean mkdirs = filePath.mkdirs();
            if (!mkdirs) {
                return ServerResponse
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .bodyValue("目录创建失败");
            }
        }

        return request
                .multipartData()
                .flatMap(map -> {
                    StringJoiner sj = new StringJoiner("");
                    map.forEach((k, v) -> {
                        v.forEach(i -> {
                            FilePart f = (FilePart) i;
                            String suffix = f.filename().substring(f.filename().lastIndexOf("."));
                            String fileName = UuidUtil.uuid() + suffix;
                            sj.add(fileName);
                            f.transferTo(new File(filePath.getPath() + "/" + fileName));
                        });
                    });
                    return ServerResponse
                            .ok()
                            .bodyValue("/" + d + "/" + sj.toString());
                });
    }

    /**
     * 下载
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> dow(ServerRequest request) {
//        //读取文件并包装为DataBuffer返回，spring-webflux会自动写入response
//        File file = new File("/tmp/test.jpeg");
//        return ServerResponse.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test.jpeg")
//                .contentType(MediaType.IMAGE_JPEG).contentLength(file.length())
//                .body(BodyInserters.fromDataBuffers(Mono.create(r -> {
//                    DataBuffer buf = new DefaultDataBufferFactory().wrap(FileIOUtil.syncRead(file));
//                    r.success(buf);
//                    return;
//                })));
        return null;
    }

}
