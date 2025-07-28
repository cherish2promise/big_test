from kafka import KafkaProducer, KafkaConsumer # KafkaProducer, KafkaConsumer 임포트
from kafka.errors import NoBrokersAvailable, KafkaError # 에러 클래스 임포트
import json
import logging
import threading
import time

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# 실제 Kafka 브로커 주소로 변경 필요 (만약 localhost가 아니거나 포트가 다르다면)
KAFKA_BROKER = 'localhost:9092' 

# --- Producer 관련 ---
_producer = None

def get_kafka_producer():
    """싱글톤 패턴으로 Kafka Producer 인스턴스를 반환합니다."""
    global _producer
    if _producer is None:
        try:
            _producer = KafkaProducer(
                bootstrap_servers=KAFKA_BROKER,
                # 메시지 값을 JSON으로 직렬화하여 UTF-8로 인코딩
                value_serializer=lambda v: json.dumps(v, ensure_ascii=False).encode('utf-8'),
                # 메시지 키를 UTF-8로 인코딩
                key_serializer=lambda k: k.encode('utf-8')
            )
            logger.info(f"Kafka Producer 초기화 완료: {KAFKA_BROKER}")
        except NoBrokersAvailable as e:
            logger.error(f"Kafka 브로커를 찾을 수 없습니다: {e}")
            raise
        except Exception as e:
            logger.error(f"Kafka Producer 초기화 실패: {e}")
            raise
    return _producer

def send_message(topic: str, key: str, value: dict):
    """Kafka로 메시지를 발행하는 함수."""
    try:
        producer_instance = get_kafka_producer()
        # .send()는 Future 객체를 반환하며, 실제 전송 완료까지 대기하지 않습니다.
        # .get()을 호출하면 전송 완료될 때까지 동기적으로 대기할 수 있습니다.
        future = producer_instance.send(topic, key=key, value=value)
        
        # 디버깅 및 안정성 향상을 위해 10초 타임아웃으로 전송 결과를 확인합니다.
        # 프로덕션 환경에서는 이 부분을 비동기적으로 처리하거나, 필요한 경우에만 동기적으로 처리할 수 있습니다.
        record_metadata = future.get(timeout=10) 
        logger.info(f"Producer: 메시지 전송 완료 to topic '{record_metadata.topic}' [{record_metadata.partition}] at offset {record_metadata.offset}")

        logger.info(f"Producer: 토픽 '{topic}'에 키 '{key}'로 메시지 전송 요청됨")

    except KafkaError as e:
        logger.error(f"Producer: Kafka 메시지 전송 실패: {e}")
        raise
    except Exception as e:
        logger.error(f"Producer: 메시지 전송 중 예상치 못한 오류 발생: {e}")
        raise

def close_producer():
    """Kafka Producer를 플러시하고 닫는 함수."""
    global _producer
    if _producer:
        logger.info("Kafka Producer 플러싱 및 종료 중...")
        _producer.flush(timeout=5) # 남아있는 메시지 모두 전송, 5초 타임아웃
        _producer.close() # Producer를 명시적으로 닫습니다.
        _producer = None
        logger.info("Kafka Producer 종료 완료.")

# --- Consumer 관련 ---
def start_consumer(topic: str, group_id: str, message_handler): # message_handler 인수를 다시 사용합니다.
    """
    Kafka Consumer를 시작하고 메시지를 처리하는 함수.
    이 함수는 별도의 스레드에서 실행되어야 합니다.
    """
    consumer = None # finally 블록에서 consumer.close()를 호출할 수 있도록 초기화
    try:
        consumer = KafkaConsumer(
            topic,
            bootstrap_servers=KAFKA_BROKER,
            group_id=group_id,
            auto_offset_reset='earliest', # 가장 오래된 오프셋부터 시작
            enable_auto_commit=True, # 자동 커밋 활성화 (기본값 True이므로 명시적으로 설정)
            # 수신한 메시지 값을 자동으로 JSON 디코딩하여 Python 객체로 변환
            value_deserializer=lambda x: json.loads(x.decode('utf-8')), 
            # 수신한 메시지 키를 자동으로 UTF-8 디코딩
            key_deserializer=lambda x: x.decode('utf-8') if x else None 
        )
        logger.info(f"그룹 '{group_id}'의 Consumer가 토픽 '{topic}'을 구독했습니다.")

        for msg in consumer: # 메시지를 무한히 순회하며 처리
            # msg 객체는 Record 객체이며, key, value, topic, partition, offset 등의 속성을 가집니다.
            key = msg.key
            value_data = msg.value # value_deserializer에 의해 이미 딕셔너리 또는 기타 파싱된 형태
            
            logger.info(f"Consumer: 토픽 '{msg.topic}'에서 메시지 수신 (키: {key}, 값: {value_data}, 오프셋: {msg.offset})")
            
            # --- 이곳을 다시 활성화합니다! ---
            try:
                # message_handler 함수로 파싱된 딕셔너리 데이터를 전달
                message_handler(value_data) 
                logger.info(f"Consumer: 메시지 ID '{value_data.get('id', 'N/A')}' 처리 완료.")
            except Exception as e:
                # message_data가 딕셔너리가 아닐 경우를 대비하여 get('id') 대신 안전하게 접근
                msg_id_for_log = value_data.get('id') if isinstance(value_data, dict) else 'N/A'
                logger.error(f"Consumer: 메시지 ID '{msg_id_for_log}' 처리 중 message_handler 실행 오류: {e}", exc_info=True)
            # ----------------------------------

            # 'enable_auto_commit=True'로 설정했기 때문에 수동 커밋은 필요 없습니다.
            # KafkaConsumer는 주기적으로 자동으로 오프셋을 커밋합니다.

    except NoBrokersAvailable as e:
        logger.error(f"Consumer: Kafka 브로커를 찾을 수 없습니다: {e}")
    except KafkaError as e:
        logger.error(f"Consumer: Kafka 관련 오류 발생: {e}")
    except Exception as e:
        logger.error(f"Consumer 스레드에서 예상치 못한 오류 발생: {e}", exc_info=True)
    finally:
        logger.info("Kafka Consumer 종료 중...")
        if consumer: # consumer 객체가 성공적으로 생성되었을 경우에만 닫습니다.
            consumer.close()
        logger.info("Kafka Consumer 종료 완료.")