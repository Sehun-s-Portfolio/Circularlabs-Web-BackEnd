package com.web.circularlabs_web_backend.product.service;

import com.web.circularlabs_web_backend.device.request.DeviceCreateRequestDto;
import com.web.circularlabs_web_backend.media.MediaUploadInterface;
import com.web.circularlabs_web_backend.product.domain.Product;
import com.web.circularlabs_web_backend.product.repository.ProductRepository;
import com.web.circularlabs_web_backend.product.request.ProductCreateRequestDto;
import com.web.circularlabs_web_backend.product.request.ProductUpdateRequestDto;
import com.web.circularlabs_web_backend.product.request.updateSupplyProductInfoRequestDto;
import com.web.circularlabs_web_backend.product.response.AllSupplyProductResponseDto;
import com.web.circularlabs_web_backend.product.response.ProductCreateResponseDto;
import com.web.circularlabs_web_backend.query.product.ProductQueryData;
import com.web.circularlabs_web_backend.share.ResponseBody;
import com.web.circularlabs_web_backend.share.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductQueryData productQueryData;
    private final ProductRepository productRepository;
    private final MediaUploadInterface mediaUploadInterface;

    // 제품 등록 service
    public ResponseEntity<ResponseBody> createProduct(
            ProductCreateRequestDto productCreateRequestDto,
            MultipartFile productImg) throws IOException {
        log.info("제품 등록 api - service");

        // 제품 고유 코드 생성
        // String productCode = productQueryData.getProductCode();
        // 제품 이미지 등록 후 호출 경로 추출
        HashMap<String, String> result = mediaUploadInterface.uploadProductImage(productImg);

        String productImgUrl = result.get("uploadUrl");

        // 제품 등록 정보 기입 및 등록
        Product createProduct = Product.builder()
                .productCode(productCreateRequestDto.getProductCode())
                .productName(productCreateRequestDto.getProductName())
                .purchasePrice(productCreateRequestDto.getPurchasePrice())
                .productImgName(productImg.getOriginalFilename())
                .productQtt(productCreateRequestDto.getProductQtt())
                .productImgUrl(productImgUrl)
                .status("Y")
                .build();

        productRepository.save(createProduct);

        // 확인 용 반환 객체에 정보들 저장
        ProductCreateResponseDto responseDto = ProductCreateResponseDto.builder()
                .productCode(createProduct.getProductCode())
                .productName(createProduct.getProductName())
                .purchasePrice(createProduct.getPurchasePrice())
                .productImgName(createProduct.getProductImgName())
                .productImgUrl(createProduct.getProductImgUrl())
                .status(createProduct.getStatus())
                .build();

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, responseDto), HttpStatus.OK);
    }

    /**
    // 상품 리스트(page) service
    public ResponseEntity<ResponseBody> selectProduct (int page){
        log.info("상품 리스트 api - service");
        page =  (page - 1) * 10;
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, productQueryData.getProductList(page)), HttpStatus.OK);
    }
     **/

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 관리자 상품 리스트 조회 service [개선]
    public ResponseEntity<ResponseBody> newSelectProduct (int page){
        log.info("상품 리스트 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, productQueryData.newGetProductList(page)), HttpStatus.OK);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 상품 전체리스트 service
    public ResponseEntity<ResponseBody> selectProductlist (){
        log.info("상품 전체리스트 api - service");
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, productQueryData.getProductAllList()), HttpStatus.OK);

    }

    // 상품 정보 수정 service
    public ResponseEntity<ResponseBody> updateProduct(ProductUpdateRequestDto ProductUpdateRequestDto, MultipartFile productImg) throws IOException {
        log.info("상품 수정 api - service");

        HashMap<String, String> imageUploatInfo = new HashMap<>();

        if(productImg != null){
            imageUploatInfo = mediaUploadInterface.uploadProductImage(productImg);
            productQueryData.getProductUpdate(ProductUpdateRequestDto, imageUploatInfo, productImg.getOriginalFilename());
        }else{
            productQueryData.getProductUpdate(ProductUpdateRequestDto, imageUploatInfo, "");
        }


        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, "success"), HttpStatus.OK);

    }
    //상품 삭제
    public ResponseEntity<ResponseBody> ProductDelete(String productId){
        log.info("상품 삭제 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, productQueryData.ProductDelete(productId)), HttpStatus.OK);
    }

    /**
    // 상품 리스트 service
    public ResponseEntity<ResponseBody> selectDetailProduct (int page,String supplierCode){
        log.info("상세 상품 리스트 api - service");
        page =  (page - 1) * 10;
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, productQueryData.getDetailProductList(page, supplierCode)), HttpStatus.OK);

    }
     **/

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 관리자 상품 상세 이력 리스트 service [개선]
    public ResponseEntity<ResponseBody> newSelectDetailProduct (int page,String supplierCode){
        log.info("상세 상품 리스트 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, productQueryData.newGetDetailProductList(page, supplierCode)), HttpStatus.OK);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    // 상품 리스트 service
    public ResponseEntity<ResponseBody> selectDetailProductXlsx (String supplierCode){
        log.info("상세 상품 리스트 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, productQueryData.selectDetailProductXlsx(supplierCode)), HttpStatus.OK);

    }
    // 상품 상세정보 service
    public ResponseEntity<ResponseBody> selectDetailProductInfo (String productdetailcode){
        log.info("상세 상품 기록 리스트 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, productQueryData.getDetailProductInfo(productdetailcode)), HttpStatus.OK);

    }
    /**
    // 상품 히스토리 리스트 service
    public ResponseEntity<ResponseBody> selectDetailProductHistory (String productdetailcode, int page){
        log.info("상세 상품 기록 리스트 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, productQueryData.getDetailProductHistoryList(productdetailcode, page)), HttpStatus.OK);

    }
     **/

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 관리자 특정 상품 상세 이력의 내부 상세 이력 리스트 조회 service [개선]
    public ResponseEntity<ResponseBody> newSelectDetailProductHistory (String productDetailCode, int page){
        log.info("상세 상품 기록 리스트 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, productQueryData.newGetDetailProductHistoryList(productDetailCode, page)), HttpStatus.OK);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 공급사에 재고 리스트 조회 service
    public ResponseEntity<ResponseBody> selectStockList (String classificationCode, int page){
        log.info("공급사에 재고 리스트 조회 api - service");
        page =  (page - 1) * 10;
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, productQueryData.selectStockList(classificationCode, page)), HttpStatus.OK);
    }
    // 공급사에 재고 리스트 조회 service
    public ResponseEntity<ResponseBody> selectStockXlsxList (String classificationCode){
        log.info("공급사에 재고 리스트 조회 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, productQueryData.selectStockXlsxList(classificationCode)), HttpStatus.OK);
    }
    // 현재 로그인한 공급사가 가지고 있는 제품들 리스트 조회 service
    public ResponseEntity<ResponseBody> getSupplyProductList(String classificationCode){
        log.info("상세 상품 기록 조회 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, productQueryData.getSupplyProductList(classificationCode)), HttpStatus.OK);
    }


    // 제품 관리에서 공급사가 보유한 모든 제품 리스트 조회 service
    public ResponseEntity<ResponseBody> getAllSupplierProducts(String classificationCode, int page){
        log.info("제품관리에서 공급사가 보유한 모든 제품 리스트 조회 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, productQueryData.getAllSupplierProducts(classificationCode, page)), HttpStatus.OK);
    }


    // 공급사가 가진 특정 제품 정보 수정 (렌탈 비용 / 최대 주문량) service
    @Transactional
    public ResponseEntity<ResponseBody> updateSupplyProductInfo(updateSupplyProductInfoRequestDto updateInfo){
        log.info("공급사가 가진 특정 제품 정보 수정 (렌탈 비용 / 최대 주문량) api - service");

        AllSupplyProductResponseDto updateSupplyProductResponse = productQueryData.updateSupplyProductInfo(updateInfo);

        if(updateSupplyProductResponse == null){
            return new ResponseEntity<>(new ResponseBody(StatusCode.NOT_EXIST_DIFFERENCE, null), HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity<>(new ResponseBody(StatusCode.OK, updateSupplyProductResponse), HttpStatus.OK);
        }

    }


    // 각 제품 스캔 이력 리스트 조회 service
    public ResponseEntity<ResponseBody> getProductScanHistory(String classificationCode, int page){
        log.info(" 각 제품 스캔 이력 리스트 조회 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, productQueryData.getProductScanHistory(classificationCode, page)), HttpStatus.OK);
    }


    // 각 제품 심화 스캔 상세 이력 리스트 조회 service
    public ResponseEntity<ResponseBody> getProductScanDetailHistory(String classificationCode, String productCode, String productSerialCode, int page){
        log.info("각 제품 심화 스캔 상세 이력 리스트 조회 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, productQueryData.getProductScanDetailHistory(classificationCode, productCode, productSerialCode, page)), HttpStatus.OK);
    }

    //고객사 주문 가능한 제품 정보 리스트 조회 service
    public ResponseEntity<ResponseBody> getClientProductList(String classificationCode){
        log.info("각 제품 심화 스캔 상세 이력 리스트 조회 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, productQueryData.getClientProductList(classificationCode)), HttpStatus.OK);
    }
}
