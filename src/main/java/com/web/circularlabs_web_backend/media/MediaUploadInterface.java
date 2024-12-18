package com.web.circularlabs_web_backend.media;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

@Component
public interface MediaUploadInterface {
    /** 업로드 될 경로 **/
    String getFullPath(String filename, String fileDir);

    /** 업로드 시킬 파일 (로컬 환경) **/
    HashMap<String, String> uploadProductImage(MultipartFile multipartFile) throws IOException;

    /** 난수화한 업로드할 파일 이름 **/
    String createServerFileName(String originalFilename);

    /** 업로드 파일 확장자 정보 추출 **/
    String extractExt(String originalFilename);
}
