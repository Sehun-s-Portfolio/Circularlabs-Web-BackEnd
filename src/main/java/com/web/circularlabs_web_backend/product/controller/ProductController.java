package com.web.circularlabs_web_backend.product.controller;

import com.web.circularlabs_web_backend.device.request.DeviceCreateRequestDto;
import com.web.circularlabs_web_backend.product.request.ProductCreateRequestDto;
import com.web.circularlabs_web_backend.product.request.ProductUpdateRequestDto;
import com.web.circularlabs_web_backend.product.request.updateSupplyProductInfoRequestDto;
import com.web.circularlabs_web_backend.product.service.ProductService;
import com.web.circularlabs_web_backend.share.ResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/cl/product")
@RestController
public class ProductController {

    private final ProductService productService;

    // 제품 등록 api
    @PostMapping("/create")
    public ResponseEntity<ResponseBody> createProduct(
            @RequestPart("productCreateRequestDto") ProductCreateRequestDto productCreateRequestDto,
            @RequestPart("productImg") MultipartFile productImg) throws IOException {
        log.info("제품 등록 api - controller");
        log.info("제품 명 : {} / 제품 이미지 : {}", productCreateRequestDto.getProductName(), productImg.getOriginalFilename());

        return productService.createProduct(productCreateRequestDto, productImg);
    }

    // 상품 리스트(page) api
    @PostMapping("/list")
    public ResponseEntity<ResponseBody> selectProduct(@RequestParam(defaultValue = "1") int page){
        log.info("상품 조회 api - controller");

        return productService.newSelectProduct(page);
    }
    // 상품 리스트(page) api
    @GetMapping("/alllist")
    public ResponseEntity<ResponseBody> selectallProduct(){
        log.info("상품 조회 api - controller");

        return productService.selectProductlist();
    }
    // 상품 정보 수정 api
    @PostMapping("/update")
    public ResponseEntity<ResponseBody> updateProduct(
            @RequestPart ProductUpdateRequestDto ProductUpdateRequestDto,
            @RequestPart(required = false) MultipartFile productImg) throws IOException {
        log.info("상품 정보 수정 api - controller");
        log.info("수정하고자 하는 상품의 이름 : {}", ProductUpdateRequestDto.getProductName());

        return productService.updateProduct(ProductUpdateRequestDto, productImg);
    }
    
    // 상품 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseBody> deleteProduct(String productId){
        log.info("상품 삭제 api - controller");

        return productService.ProductDelete(productId);
    }
    // 상세 상품 리스트 api
    @PostMapping("/detaillist")
    public ResponseEntity<ResponseBody> selectDetailProduct(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "") String supplierCode){
        log.info("상세 상품 조회 api - controller");

        return productService.newSelectDetailProduct(page, supplierCode);
    }
    // 상세 상품 리스트 엑셀 api
    @GetMapping("/productdetaillist/xlsx")
    public ResponseEntity<ResponseBody> selectDetailProductXlsx(@RequestParam String supplierCode){
        log.info("상세 상품 조회 api - controller");

        return productService.selectDetailProductXlsx(supplierCode);
    }
    // 상세 상품 상세정보 api
    @PostMapping("/detailInfo")
    public ResponseEntity<ResponseBody> selectDetailProductInfo(@RequestParam String productdetailcode){
        log.info("상세 상품 상세조회 api - controller");

        return productService.selectDetailProductInfo(productdetailcode);
    }
    // 상세 상품 기록 정보 api
    @PostMapping("/historylist")
    public ResponseEntity<ResponseBody> selectDetailProductHistory(@RequestParam String productSerialCode, @RequestParam int page){
        log.info("상세 상품 기록 조회 api - controller");

        return productService.newSelectDetailProductHistory(productSerialCode, page);
    }
    // 공급사에 재고 리스트 조회 api
    @GetMapping("/stocklist")
    public ResponseEntity<ResponseBody> selectStockList(@RequestParam String classificationCode, @RequestParam int page){
        log.info("공급사에 재고 리스트 조회 api - controller");

        return productService.selectStockList(classificationCode, page);
    }
    // 공급사에 재고 리스트 엑셀 api
    @GetMapping("/stocklist/xlsx")
    public ResponseEntity<ResponseBody> selectStockXlsxList(@RequestParam String classificationCode){
        log.info("공급사에 재고 리스트 조회 api - controller");

        return productService.selectStockXlsxList(classificationCode);
    }
    // 현재 로그인한 공급사가 가지고 있는 제품들 리스트 조회 api
    @GetMapping("/order/list/sc")
    public ResponseEntity<ResponseBody> getOrderSupplyProductList(@RequestParam String classificationCode){
        log.info("상세 상품 기록 조회 api - controller");

        return productService.getSupplyProductList(classificationCode);
    }

    // 제품 관리에서 공급사가 보유한 모든 제품 리스트 조회 api
    @GetMapping("/list/sc")
    public ResponseEntity<ResponseBody> getAllSupplierProducts(@RequestParam String classificationCode, @RequestParam int page){
        log.info("제품관리에서 공급사가 보유한 모든 제품 리스트 조회 api - controller");

        return productService.getAllSupplierProducts(classificationCode, page);
    }

    // 공급사가 가진 특정 제품 정보 수정 (렌탈 비용 / 최대 주문량) api
    @PutMapping("/update/sc")
    public ResponseEntity<ResponseBody> updateSupplyProductInfo(@RequestBody updateSupplyProductInfoRequestDto updateInfo){
        log.info("공급사가 가진 특정 제품 정보 수정 (렌탈 비용 / 최대 주문량) api - controller");

        return productService.updateSupplyProductInfo(updateInfo);
    }

    // 각 제품 스캔 이력 리스트 조회 api
    @GetMapping("/scan/history")
    public ResponseEntity<ResponseBody> getProductScanHistory(@RequestParam String classificationCode, @RequestParam int page){
        log.info("각 제품 스캔 이력 리스트 조회 api - controller");

        return productService.getProductScanHistory(classificationCode, page);
    }

    // 각 제품 심화 스캔 상세 이력 리스트 조회 api
    @GetMapping("/scan/detail/history")
    public ResponseEntity<ResponseBody> getProductScanDetailHistory(
            @RequestParam String classificationCode, @RequestParam String productCode, @RequestParam String productSerialCode, @RequestParam int page){
        log.info("각 제품 심화 스캔 상세 이력 리스트 조회 api - controller");

        return productService.getProductScanDetailHistory(classificationCode, productCode, productSerialCode, page);
    }

    //고객사 주문 가능한 제품 정보 리스트 조회 api
    @GetMapping("/clent/productlist")
    public ResponseEntity<ResponseBody> getClientProductList(@RequestParam String classificationCode){
        log.info("각 제품 심화 스캔 상세 이력 리스트 조회 api - controller");

        return productService.getClientProductList(classificationCode);
    }
}
