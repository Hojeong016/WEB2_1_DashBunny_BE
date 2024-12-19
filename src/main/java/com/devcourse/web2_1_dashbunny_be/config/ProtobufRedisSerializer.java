package com.devcourse.web2_1_dashbunny_be.config;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import org.springframework.data.redis.serializer.RedisSerializer;
public class ProtobufRedisSerializer<T extends Message> implements RedisSerializer<T> {

    private final Parser<T> parser;

    // 생성자에서 Protobuf 파서 전달
    public ProtobufRedisSerializer(Parser<T> parser) {
        this.parser = parser;
    }

    @Override
    public byte[] serialize(T message) {
        if (message == null) {
            return null;
        }
        return message.toByteArray();
    }

    @Override
    public T deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return parser.parseFrom(bytes); // 특정 Protobuf 메시지로 역직렬화
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize Protobuf message", e);
        }
    }
}

