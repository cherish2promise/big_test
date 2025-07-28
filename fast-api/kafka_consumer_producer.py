from confluent_kafka import Producer, Consumer, KafkaException
import json
import logging
import threading
import time

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

KAFKA_BROKER = 'localhost:9092' # 실제 Kafka 브로커 주소로 변경 필요

# --- Producer 관련 ---
_producer = None

def get_kafka_producer():
    global _producer
    if _producer is None:
        try:
            _producer = Producer({
                'bootstrap.servers': KAFKA_BROKER,
                'client.id': 'python-ai-service-producer' 
            })
            logger.info(f"Kafka Producer initialized for broker: {KAFKA_BROKER}")
        except Exception as e:
            logger.error(f"Failed to initialize Kafka Producer: {e}")
            raise
    return _producer

def delivery_report(err, msg):
    """Producer 메시지 전송 결과 콜백 함수."""
    if err is not None:
        logger.error(f"Message delivery failed: {err}")
    else:
        logger.info(f"Producer: Message delivered to topic '{msg.topic()}' [{msg.partition()}] at offset {msg.offset()}")

def send_message(topic: str, key: str, value: dict):
    """Kafka로 메시지를 발행하는 함수."""
    try:
        producer_instance = get_kafka_producer()
        json_value = json.dumps(value, ensure_ascii=False).encode('utf-8')
        
        producer_instance.produce(
            topic, 
            key=key.encode('utf-8'), 
            value=json_value, 
            callback=delivery_report
        )
        producer_instance.poll(0) # 비동기 전송을 위해 0으로 설정
        logger.info(f"Producer: Sent message to topic {topic} with key {key}")

    except Exception as e:
        logger.error(f"Producer: Failed to send message to topic {topic}: {e}")
        raise

def close_producer():
    global _producer
    if _producer:
        logger.info("Flushing and closing Kafka Producer...")
        _producer.flush(timeout=5) # 남아있는 메시지 모두 전송, 5초 타임아웃
        _producer = None
        logger.info("Kafka Producer closed.")

# --- Consumer 관련 ---
def start_consumer(topic: str, group_id: str, message_handler):
    """
    Kafka Consumer를 시작하고 메시지를 처리하는 함수.
    이 함수는 별도의 스레드에서 실행되어야 합니다.
    """
    conf = {
        'bootstrap.servers': KAFKA_BROKER,
        'group.id': group_id,
        'auto.offset.reset': 'earliest' # 가장 오래된 오프셋부터 시작
    }
    
    consumer = Consumer(conf)
    
    try:
        consumer.subscribe([topic])
        logger.info(f"Consumer for group '{group_id}' subscribed to topic '{topic}'.")

        while True:
            msg = consumer.poll(1.0) # 1초 대기
            
            if msg is None:
                continue
            if msg.error():
                if msg.error().code() == KafkaException._PARTITION_EOF:
                    # End of partition event
                    logger.info(f"Consumer: End of partition reached {msg.topic()} [{msg.partition()}]")
                elif msg.error():
                    logger.error(f"Consumer error: {msg.error()}")
                    break
            else:
                # 메시지 처리
                key = msg.key().decode('utf-8') if msg.key() else None
                value_str = msg.value().decode('utf-8')
                logger.info(f"Consumer: Received message from topic '{msg.topic()}' (Key: {key}, Value: {value_str})")
                
                try:
                    message_data = json.loads(value_str)
                    message_handler(message_data) # 외부에서 주입된 핸들러 함수 호출
                    consumer.commit(message=msg) # 메시지 처리 완료 후 오프셋 커밋
                except json.JSONDecodeError as e:
                    logger.error(f"Consumer: Failed to decode JSON from message: {e}")
                except Exception as e:
                    logger.error(f"Consumer: Error processing message: {e}", exc_info=True)

    except KeyboardInterrupt:
        logger.info("Consumer stopped by user.")
    finally:
        logger.info("Closing Kafka Consumer...")
        consumer.close()
        logger.info("Kafka Consumer closed.")
