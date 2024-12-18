package com.web.circularlabs_web_backend.media;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Component
public class MediaUpload implements MediaUploadInterface {

    // 배포 서버 업로드 경로
    private final String uploadFilePath = File.separator + "home" + File.separator + "circularlabs" + File.separator + "web" + File.separator ;
    // 로컬 환경 업로드 경로
    private final String uploadFileLocalPath = "C:" + File.separator + "BackEnd_Project" + File.separator + "Music" + File.separator + "MusicIsMyLife" + File.separator + "src" + File.separator + "main" + File.separator + "webapp" + File.separator;

    @Value("${deploy.upload.path}")
    private String deployPath;


    // 업로드 될 경로
    @Override
    public String getFullPath(String fileDir, String filename) {
        // 파일명과 업로드될 경로 반환
        return fileDir + filename;
    }

    // 프로필 이미지 업데이트
    @Override
    public HashMap<String, String> uploadProductImage(MultipartFile productImage) throws IOException{
        // 업로드할 프로필 이미지 파일의 진짜 이름 추출
        String originalFilename = productImage.getOriginalFilename();
        // 난수화된 파일 이름과 확장자를 합친 파일명 추출
        String serverUploadFileName = createServerFileName(originalFilename);

        // 업로드한 프로필 이미지와 경로를 합친 업로드 경로를 File 객체에 넣어 생성
        //[배포 서버]
        File file = new File(getFullPath(uploadFilePath + "image" + File.separator, serverUploadFileName)); // 배포 서버 File
        //[로컬]
        //File file = new File(getFullPath(uploadFileLocalPath + "upload-thumbnail" + File.separator, serverUploadFileName)); // 로컬 File

        // multipartfile에서 지원하는 transferTo 함수 사용. (해당 파일을 지정한 경로로 전송)
        productImage.transferTo(file); // 배포 서버 파일 등록

        //[배포 서버]
        HashMap<String, String> result = new HashMap<>();
        result.put("uploadUrl", deployPath + "image" + File.separator + serverUploadFileName);
        result.put("uploadImgUUID", serverUploadFileName);

        return result; // 배포 서버 업로드 이미지 경로 호출
        //[로컬]
        //return uploadFileLocalPath + "upload-thumbnail" + File.separator + serverUploadFileName; // 로컬 업로드 이미지 경로 호출
    }


    // 난수화한 업로드할 파일 이름
    @Override
    public String createServerFileName(String originalFilename) {
        // 원래 이름이 아닌 난수화한 uuid 이름 추출
        String uuid = UUID.randomUUID().toString();
        // 파일의 원래 이름 중에 . 기호 기준으로 확장자 추출
        String ext = extractExt(originalFilename);

        // 난수화된 이름과 확장자를 합쳐 난수화된 파일명 반환
        return uuid + "." + ext;
    }

    // 업로드 파일 확장자 정보 추출
    @Override
    public String extractExt(String originalFilename) {
        // 파일명의 . 기호가 몇번째에 존재하는지 인덱스 값 추출
        int pos = originalFilename.lastIndexOf(".");

        // 원래 이름에서 뽑은 인덱스값에 위치한 . 기호 다음 확장자 추출
        return originalFilename.substring(pos + 1);
    }
}
