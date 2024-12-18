package com.web.circularlabs_web_backend.process.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QClientOrder is a Querydsl query type for ClientOrder
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QClientOrder extends EntityPathBase<ClientOrder> {

    private static final long serialVersionUID = 2408720L;

    public static final QClientOrder clientOrder = new QClientOrder("clientOrder");

    public final com.web.circularlabs_web_backend.share.QTimeStamped _super = new com.web.circularlabs_web_backend.share.QTimeStamped(this);

    public final StringPath classificationCode = createString("classificationCode");

    public final NumberPath<Long> clientOrderId = createNumber("clientOrderId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> deliveryAt = createDateTime("deliveryAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> hopeDeliveryAt = createDateTime("hopeDeliveryAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath modifiedYN = createString("modifiedYN");

    public final StringPath motherCode = createString("motherCode");

    public final NumberPath<Integer> orderMount = createNumber("orderMount", Integer.class);

    public final StringPath productCode = createString("productCode");

    public final StringPath statementnumber = createString("statementnumber");

    public final StringPath status = createString("status");

    public QClientOrder(String variable) {
        super(ClientOrder.class, forVariable(variable));
    }

    public QClientOrder(Path<? extends ClientOrder> path) {
        super(path.getType(), path.getMetadata());
    }

    public QClientOrder(PathMetadata metadata) {
        super(ClientOrder.class, metadata);
    }

}

